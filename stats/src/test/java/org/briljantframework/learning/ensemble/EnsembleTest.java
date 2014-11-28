package org.briljantframework.learning.ensemble;

import java.io.FileInputStream;
import java.util.Collection;

import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.dataframe.MixedDataFrame;
import org.briljantframework.io.CsvInputStream;
import org.briljantframework.io.DataFrameInputStream;
import org.briljantframework.learning.Predictions;
import org.briljantframework.learning.tree.ClassificationTree;
import org.briljantframework.learning.tree.Entropy;
import org.briljantframework.learning.tree.Gain;
import org.briljantframework.learning.tree.RandomSplitter;
import org.briljantframework.vector.Type;
import org.briljantframework.vector.Vector;
import org.junit.Test;

public class EnsembleTest {

  @Test
  public void testFit() throws Exception {

    try (DataFrameInputStream dfis = new CsvInputStream(new FileInputStream("iris.txt"))) {
      Collection<Type> colTypes = dfis.readColumnTypes();
      Collection<String> colNames = dfis.readColumnNames();
      DataFrame.Builder builder = new MixedDataFrame.Builder(colNames, colTypes);
      dfis.read(builder);

      DataFrame iris = builder.create();

      Vector y = iris.getColumn(4);
      DataFrame x = iris.newCopyBuilder().removeColumn(4).create();
      ClassificationTree.Builder dt =
          ClassificationTree.withSplitter(RandomSplitter.withMaximumFeatures(2).setCriterion(
              Gain.with(Entropy.getInstance())));

      ClassificationTree.Model model = dt.create().fit(x, y);

      Predictions predictions = model.predict(x);
      System.out.println(predictions);


    }
    // Container<DenseDataset, DefaultTarget> container = new CSVInputStream(new FileInputStream
    // ("erlang/adeb-rr/deps/rr/data/car.txt")).read(DenseDataset.FACTORY, DefaultTarget.FACTORY);
    //
    // DecisionTree.Builder dt = DecisionTree.withSplitter(RandomSplitter
    // .withMaximumFeatures(2)
    // .setCriterion(Gain.with(Entropy.INSTANCE)));
    //
    // Ensemble<Dataset> ensemble = Ensemble.withMember(dt)
    // .withSampler(Bootstrap.create())
    // .create();
    //
    // long start = System.currentTimeMillis();
    // Ensemble.Model<Dataset> model = ensemble.fit(container);
    // System.out.println(System.currentTimeMillis() - start);
    //
    // start = System.currentTimeMillis();
    // Predictions predictions = model.predict(container.getDataset());
    // System.out.println(System.currentTimeMillis() - start);
    //
    // int correct = 0;
    // for (int i = 0; i < predictions.size(); i++) {
    // if (predictions.get(i).getValue().equals(container.getTarget().getValue(i))) {
    // correct++;
    // }
    // }
    // System.out.println((double) correct / container.rows());
    //
    // SplitValidation.create().evaluate(ensemble, container);
  }
}

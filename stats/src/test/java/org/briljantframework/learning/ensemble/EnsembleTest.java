package org.briljantframework.learning.ensemble;

import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.dataframe.DataFrames;
import org.briljantframework.dataframe.MixedDataFrame;
import org.briljantframework.io.CsvInputStream;
import org.briljantframework.io.DataFrameInputStream;
import org.briljantframework.learning.Model;
import org.briljantframework.learning.Predictions;
import org.briljantframework.learning.tree.*;
import org.briljantframework.vector.Vector;
import org.junit.Test;

public class EnsembleTest {

  @Test
  public void testFit() throws Exception {

    try (DataFrameInputStream dfis = new CsvInputStream("../iris.txt")) {
      DataFrame iris = DataFrames.load(MixedDataFrame.Builder::new, dfis);

      Vector y = iris.getColumn(iris.columns() - 1);
      DataFrame x = iris.newCopyBuilder().removeColumn(iris.columns() - 1).create();

      ClassificationTree.Builder dt =
          ClassificationTree.withSplitter(RandomSplitter.withMaximumFeatures(6).setCriterion(
              Gain.with(Entropy.getInstance())));

      ClassificationForest forest = ClassificationForest.withSize(100).create();

      Model model = forest.fit(x, y);

      Predictions predictions = model.predict(x);
      System.out.println(predictions);
    }
  }
}

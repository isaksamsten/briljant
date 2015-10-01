package org.briljantframework.conformal;

import java.util.Random;

import org.briljantframework.array.ArrayPrinter;
import org.briljantframework.classification.RandomForest;
import org.briljantframework.data.dataframe.DataFrame;
import org.briljantframework.data.dataframe.DataFrames;
import org.briljantframework.data.dataframe.MixedDataFrame;
import org.briljantframework.data.vector.Vector;
import org.briljantframework.dataset.io.Datasets;
import org.briljantframework.evaluation.Validators;
import org.briljantframework.evaluation.partition.Partition;
import org.briljantframework.evaluation.partition.SplitPartitioner;
import org.junit.Test;

/**
 * @author Isak Karlsson <isak-kar@dsv.su.se>
 */
public class ConformalClassifierTest {

  @Test
  public void testConformalPredictions() throws Exception {
    ArrayPrinter.setMinimumTruncateSize(10000);
    DataFrame iris =
        DataFrames.permuteRecords(Datasets.loadIris(MixedDataFrame.Builder::new),
            new Random(32312));
    DataFrame x = iris.drop("Class");
    Vector y = iris.get("Class");

    System.out.println(Validators.splitValidation(0.3).test(
        RandomForest.withSize(100).withMaximumFeatures(2).build(), x, y));

    Partition train = new SplitPartitioner(0.66).partition(x, y).iterator().next();
    Partition test =
        new SplitPartitioner(0.5).partition(train.getValidationData(), train.getValidationTarget())
            .iterator().next();

    NonconformityLearner nc =
        new ProbabilityEstimateNonconformityLearner(RandomForest.withSize(100).build(),
            new Margin());
    ConformalClassifier classifier = new InductiveConformalClassifier(nc);
    ConformalPredictor predictor =
        classifier.fit(train.getTrainingData(), train.getTrainingTarget());
    predictor.calibrate(test.getTrainingData(), test.getTrainingTarget());

    Vector predictions = predictor.predict(test.getValidationData(), 0.05);

    System.out.println(predictor.conformalPredict(test.getValidationData(), 0.05));
    System.out.println(predictor.estimate(test.getValidationData()));
    Vector testY = test.getValidationTarget();
    double correct = 0, na = 0;
    for (int i = 0; i < predictions.size(); i++) {
      if (testY.loc().get(Object.class, i).equals(predictions.loc().get(Object.class, i))) {
        correct += 1;
      }

      if (predictions.loc().isNA(i)) {
        na += 1;
      }
    }
    System.out.println(correct / predictions.size());
    System.out.println(na / predictions.size());

  }
}

package org.briljantframework.conformal;

import org.briljantframework.array.ArrayPrinter;
import org.briljantframework.classification.NearestNeighbours;
import org.briljantframework.conformal.conformal.ConformalClassifierEvaluator;
import org.briljantframework.data.dataframe.DataFrame;
import org.briljantframework.data.dataframe.DataFrames;
import org.briljantframework.data.vector.Vector;
import org.briljantframework.dataset.io.Datasets;
import org.briljantframework.evaluation.Validators;
import org.briljantframework.evaluation.partition.Partition;
import org.briljantframework.evaluation.partition.SplitPartitioner;
import org.briljantframework.evaluation.result.EvaluationContext;
import org.junit.Test;

/**
 * @author Isak Karlsson <isak-kar@dsv.su.se>
 */
public class LearnerTest {

  @Test
  public void testConformalPredictions() throws Exception {
    ArrayPrinter.setMinimumTruncateSize(10000);
    // DataFrame iris =
    // DataFrames
    // .permuteRecords(Datasets.loadIris(MixedDataFrame.Builder::new), new Random(32312));
    // DataFrame x = iris.drop("Class");
    // Vector y = iris.get("Class");

    DataFrame sc = DataFrames.permuteRecords(Datasets.loadSyntheticControl());
    DataFrame x = sc.drop("0");
    Vector y = sc.get("0");

    System.out.println(Validators.splitValidation(0.3).test(new NearestNeighbours.Learner(1), x, y));

    Partition train = new SplitPartitioner(0.66).partition(x, y).iterator().next();
    Partition test =
        new SplitPartitioner(0.5).partition(train.getValidationData(), train.getValidationTarget())
            .iterator().next();

    // RandomForest forest = RandomForest.withSize(100).build();
    // ClassificationErrorFunction errorFunction = new Margin();
    // NonconformityLearner nc =
    // new ProbabilityEstimateNonconformity.Learner(RandomShapeletForest.withSize(100).build(),
    // errorFunction);
    Nonconformity.Learner nc = new DistanceNonconformity.Learner(1);
    ConformalClassifier.Learner
        classifier = new InductiveConformalClassifier.Learner(nc);
    ConformalClassifier predictor =
        classifier.fit(train.getTrainingData(), train.getTrainingTarget());
    predictor.calibrate(test.getTrainingData(), test.getTrainingTarget());
    //
    Vector predictions = predictor.predict(test.getValidationData(), 0.05);

    System.out.println(predictor.conformalPredict(test.getValidationData(), 0.05));
    System.out.println(predictor.estimate(test.getValidationData()));

    EvaluationContext context = new EvaluationContext();
    context.setPartition(test);
    context.setPredictor(predictor);

    new ConformalClassifierEvaluator(0.05).accept(context);
    System.out.println(context.getMeasures().get(0).getMean());

    Vector testY = test.getValidationTarget();
    double correct = 0, na = 0;
    for (int i = 0; i < predictions.size(); i++) {
      if (testY.loc().get(i).equals(predictions.loc().get(i))) {
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

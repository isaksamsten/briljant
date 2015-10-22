package org.briljantframework.conformal;

import org.briljantframework.array.ArrayPrinter;
import org.briljantframework.classification.RandomForest;
import org.briljantframework.data.dataframe.DataFrame;
import org.briljantframework.data.dataframe.DataFrames;
import org.briljantframework.data.vector.Vector;
import org.briljantframework.dataset.io.Datasets;
import org.briljantframework.evaluation.partition.Partition;
import org.briljantframework.evaluation.partition.SplitPartitioner;
import org.junit.Test;

/**
 * @author Isak Karlsson <isak-kar@dsv.su.se>
 */
public class InductiveConformalClassifierTest {

  @Test
  public void testConformalPredictions() throws Exception {
    ArrayPrinter.setMinimumTruncateSize(10000);
    // DataFrame iris =
    // DataFrames
    // .permuteRecords(Datasets.loadIris(MixedDataFrame.Builder::new), new Random(32312));
    // DataFrame x = iris.drop("Class");
    // Vector y = iris.get("Class");

    DataFrame sc = DataFrames.permuteRecords(Datasets.loadIris());
    DataFrame x = sc.drop("Class");
    Vector y = sc.get("Class");

    Partition train = new SplitPartitioner(0.66).partition(x, y).iterator().next();
    Partition test =
        new SplitPartitioner(0.5).partition(train.getValidationData(), train.getValidationTarget())
            .iterator().next();

    RandomForest.Learner forest =
        new RandomForest.Configurator(100).setMaximumFeatures(1).configure();
    ClassificationErrorFunction errorFunction = new Margin();
    Nonconformity.Learner nc = new ProbabilityEstimateNonconformity.Learner(forest, errorFunction);
    // Nonconformity.Learner nc = new DistanceNonconformity.Learner(1);
    InductiveConformalClassifier.Learner classifier = new InductiveConformalClassifier.Learner(nc);
    ConformalClassifier predictor =
        classifier.fit(train.getTrainingData(), train.getTrainingTarget());
    predictor.calibrate(test.getTrainingData(), test.getTrainingTarget());

    // EvaluationContextImpl context = new EvaluationContextImpl();
    // context.setPartition(test);
    // context.setPredictor(predictor);

    // new ConformalClassifierEvaluator(0.05).accept(context);
    // System.out.println(context.getMeasures());
  }
}

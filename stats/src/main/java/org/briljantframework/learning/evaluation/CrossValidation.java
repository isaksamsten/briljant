package org.briljantframework.learning.evaluation;

import java.util.ArrayList;
import java.util.List;

import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.learning.Classifier;
import org.briljantframework.learning.evaluation.result.ConfusionMatrix;
import org.briljantframework.learning.evaluation.result.Metric;
import org.briljantframework.learning.evaluation.result.Metrics;
import org.briljantframework.learning.evaluation.result.Result;
import org.briljantframework.vector.Vector;

/**
 * Created by isak on 01/10/14.
 */
public class CrossValidation extends AbstractEvaluator {

  private final int folds;


  public CrossValidation(List<Metric.Factory> producers, int folds) {
    super(producers);
    this.folds = folds;
  }


  public CrossValidation(int folds) {
    this(Metrics.CLASSIFICATION, folds);
  }

  /**
   * Create cross validation evaluator.
   *
   * @return the cross validation evaluator
   */
  public static CrossValidation create() {
    return new CrossValidation(10);
  }

  /**
   * With folds.
   *
   * @param folds the folds
   * @return the cross validation evaluator
   */
  public static CrossValidation withFolds(int folds) {
    return new CrossValidation(folds);
  }

  @Override
  public Result evaluate(Classifier classifier, DataFrame x, Vector y) {
    // FIXME(isak): re-implement
    // SupervisedDatasetPartitions<D, T> partitions =
    // SupervisedDatasetPartitions.create(supervisedDataset, folds);
    int[] trainIndex = new int[folds - 1];
    int testIndex = folds - 1;
    for (int i = 0; i < trainIndex.length; i++) {
      trainIndex[i] = i;
    }

    List<ConfusionMatrix> confusionMatrices = new ArrayList<>();
    List<Metric.Producer> producers = getMetricProducers();
    for (int i = 0; i < folds; i++) {
      // SupervisedDataset<D, T> trainingSet = partitions.takeAndMerge(trainIndex);
      // SupervisedDataset<? extends D, ? extends T> validationSet = partitions.get(testIndex);
      //
      // Model<?, ? super D> model = classifier.fit(trainingSet.getDataFrame(),
      // trainingSet.getTarget());
      // Predictions outSamplePredictions = model.predict(validationSet.getDataFrame());
      // Predictions inSamplePrediction = model.predict(trainingSet.getDataFrame());
      //
      // confusionMatrices.add(ConfusionMatrix.create(outSamplePredictions,
      // validationSet.getTarget()));
      // for (Metric.Producer producer : producers) {
      // producer.add(Metric.Sample.OUT, outSamplePredictions, validationSet.getTarget());
      // producer.add(Metric.Sample.IN, inSamplePrediction, trainingSet.getTarget());
      // }

      // Swap one of the training partitions to test and the test to training
      if (i < trainIndex.length) {
        int tmp = trainIndex[i];
        trainIndex[i] = testIndex;
        testIndex = tmp;
      }
    }
    return Result.create(Metrics.collect(producers), confusionMatrices);
  }

  @Override
  public String toString() {
    return String.format("Cross-validation with k = %d", getFolds());
  }

  /**
   * Gets folds.
   *
   * @return the folds
   */
  public int getFolds() {
    return folds;
  }
}

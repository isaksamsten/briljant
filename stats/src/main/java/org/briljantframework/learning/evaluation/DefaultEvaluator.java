package org.briljantframework.learning.evaluation;

import java.util.ArrayList;
import java.util.List;

import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.learning.Classifier;
import org.briljantframework.learning.Model;
import org.briljantframework.learning.Predictions;
import org.briljantframework.learning.evaluation.result.ConfusionMatrix;
import org.briljantframework.learning.evaluation.result.Metric;
import org.briljantframework.learning.evaluation.result.Metrics;
import org.briljantframework.learning.evaluation.result.Result;
import org.briljantframework.vector.Vector;

/**
 * The default
 * <p>
 * Created by Isak Karlsson on 01/12/14.
 */
public class DefaultEvaluator extends AbstractEvaluator {

  public DefaultEvaluator(List<? extends Metric.Factory> producers, Partitioner partitioner) {
    super(producers, partitioner);
  }

  public DefaultEvaluator(Partitioner partitioner) {
    this(Metrics.CLASSIFICATION, partitioner);
  }

  public DefaultEvaluator() {
    this(new RandomSplitPartitioner(0.33));
  }

  @Override
  public Result evaluate(Classifier classifier, DataFrame x, Vector y) {
    Iterable<Partition> partitions = getPartitioner().partition(x, y);
    List<ConfusionMatrix> confusionMatrices = new ArrayList<>();
    List<Metric.Producer> producers = getMetricProducers();

    for (Partition partition : partitions) {
      Model model = classifier.fit(partition.getTrainingData(), partition.getTrainingTarget());
      Predictions outSamplePredictions = model.predict(partition.getValidationData());
      Predictions inSamplePredictions = model.predict(partition.getTrainingData());

      confusionMatrices.add(ConfusionMatrix.create(outSamplePredictions,
          partition.getValidationTarget()));
      for (Metric.Producer producer : producers) {
        producer.add(Metric.Sample.IN, inSamplePredictions, partition.getTrainingTarget());
        producer.add(Metric.Sample.OUT, outSamplePredictions, partition.getValidationTarget());
      }
    }
    return Result.create(Metrics.collect(producers), confusionMatrices);
  }
}

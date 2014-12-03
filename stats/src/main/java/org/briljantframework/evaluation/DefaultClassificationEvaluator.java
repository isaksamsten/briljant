package org.briljantframework.evaluation;

import java.util.ArrayList;
import java.util.List;

import org.briljantframework.classification.Classifier;
import org.briljantframework.classification.Predictions;
import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.evaluation.result.*;
import org.briljantframework.vector.Vector;

/**
 * The default
 * <p>
 * Created by Isak Karlsson on 01/12/14.
 */
public class DefaultClassificationEvaluator extends AbstractClassificationEvaluator {

  public DefaultClassificationEvaluator(MeasureProvider measureProvider, Partitioner partitioner) {
    super(measureProvider, partitioner);
  }

  public DefaultClassificationEvaluator(Partitioner partitioner) {
    this(Measures.getDefaultClassificationMeasures(), partitioner);
  }

  public DefaultClassificationEvaluator() {
    this(new RandomSplitPartitioner(0.33));
  }

  @Override
  public Result evaluate(Classifier classifier, DataFrame x, Vector y) {
    Iterable<Partition> partitions = getPartitioner().partition(x, y);
    List<Measure.Builder> builders = getMeasureProvider().getMeasures();
    List<ConfusionMatrix> confusionMatrices = new ArrayList<>();

    for (Partition partition : partitions) {
      DataFrame trainingData = partition.getTrainingData();
      Vector trainingTarget = partition.getTrainingTarget();
      Classifier.Model model = classifier.fit(trainingData, trainingTarget);

      DataFrame validationData = partition.getValidationData();
      Vector validationTarget = partition.getValidationTarget();

      Predictions outSamplePredictions = model.predict(validationData);
      Predictions inSamplePredictions = model.predict(trainingData);

      confusionMatrices.add(ConfusionMatrix.compute(outSamplePredictions, validationTarget));
      for (Measure.Builder builder : builders) {
        builder.compute(Measure.Sample.IN, inSamplePredictions, trainingTarget);
        builder.compute(Measure.Sample.OUT, outSamplePredictions, validationTarget);
      }
    }
    return Result.create(collect(builders), confusionMatrices);
  }
}

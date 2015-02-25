package org.briljantframework.evaluation;

import java.util.ArrayList;
import java.util.List;

import org.briljantframework.classification.Classifier;
import org.briljantframework.classification.Predictor;
import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.evaluation.result.*;
import org.briljantframework.vector.Vector;
import org.briljantframework.vector.Vectors;

/**
 * The default
 * <p>
 * Created by Isak Karlsson on 01/12/14.
 */
public class DefaultClassificationValidator extends AbstractClassificationValidator {

  public DefaultClassificationValidator(List<Evaluator> consumers, Partitioner partitioner) {
    super(consumers, partitioner);
  }

  public DefaultClassificationValidator(Partitioner partitioner) {
    this(Measures.getDefaultClassificationMeasures(), partitioner);
  }

  public DefaultClassificationValidator() {
    this(new SplitPartitioner(0.33));
  }

  @Override
  public Result test(Classifier classifier, DataFrame x, Vector y) {
    Iterable<Partition> partitions = getPartitioner().partition(x, y);
    Vector domain = Vectors.unique(y);
    List<ConfusionMatrix> confusionMatrices = new ArrayList<>();
    EvaluationContext ctx = new EvaluationContext();
    for (Partition partition : partitions) {
      DataFrame trainingData = partition.getTrainingData();
      Vector trainingTarget = partition.getTrainingTarget();
      Predictor predictor = classifier.fit(trainingData, trainingTarget);
      Vector predictions = predictor.predict(partition.getValidationData());
      if (predictor.getCharacteristics().contains(Predictor.Characteristics.ESTIMATOR)) {
        ctx.setEstimation(predictor.estimate(partition.getValidationData()));
      }

      ctx.setPredictor(predictor);
      ctx.setPartition(partition);
      ctx.setPredictions(predictions);

      Vector evalData = partition.getValidationTarget();
      ConfusionMatrix matrix = ConfusionMatrix.compute(predictions, evalData, domain);
      confusionMatrices.add(matrix);
      for (Evaluator consumer : getMeasureProvider()) {
        consumer.accept(ctx);
      }
      predictor.evaluation(ctx);
    }
    return Result.create(collect(ctx.builders()), confusionMatrices);
  }
}

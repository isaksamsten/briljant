package org.briljantframework.evaluation;

import org.briljantframework.classification.Classifier;
import org.briljantframework.classification.Predictor;
import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.evaluation.measure.FitTime;
import org.briljantframework.evaluation.measure.PredictTime;
import org.briljantframework.evaluation.measure.TrainingSetSize;
import org.briljantframework.evaluation.measure.ValidationSetSize;
import org.briljantframework.evaluation.result.ConfusionMatrix;
import org.briljantframework.evaluation.result.EvaluationContext;
import org.briljantframework.evaluation.result.Evaluator;
import org.briljantframework.evaluation.result.Result;
import org.briljantframework.evaluation.result.Sample;
import org.briljantframework.vector.Vector;
import org.briljantframework.vector.Vectors;

import java.util.ArrayList;
import java.util.List;

/**
 * The default
 * <p>
 * Created by Isak Karlsson on 01/12/14.
 */
public class DefaultValidator extends AbstractValidator {

  public DefaultValidator(List<Evaluator> evaluators, Partitioner partitioner) {
    super(evaluators, partitioner);
  }

  public DefaultValidator(Partitioner partitioner) {
    this(Evaluator.getDefaultClassificationEvaluators(), partitioner);
  }

  public DefaultValidator() {
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
      long start = System.nanoTime();
      Predictor predictor = classifier.fit(trainingData, trainingTarget);
      double fitTime = (System.nanoTime() - start) / 1e6;

      start = System.nanoTime();
      Vector predictions = computeClassLabels(
          partition.getValidationData(), predictor, y.getType(), ctx);
      double predictTime = (System.nanoTime() - start) / 1e6;

      ctx.setPredictor(predictor);
      ctx.setPartition(partition);
      ctx.setPredictions(predictions);

      Vector evalData = partition.getValidationTarget();
      ConfusionMatrix matrix = ConfusionMatrix.compute(predictions, evalData, domain);
      confusionMatrices.add(matrix);
      for (Evaluator evaluator : getEvaluators()) {
        evaluator.accept(ctx);
      }
      predictor.evaluation(ctx);

      ctx.getOrDefault(TrainingSetSize.class, TrainingSetSize.Builder::new)
          .add(Sample.OUT, trainingData.rows());
      ctx.getOrDefault(ValidationSetSize.class, ValidationSetSize.Builder::new)
          .add(Sample.OUT, partition.getValidationData().rows());
      ctx.getOrDefault(FitTime.class, FitTime.Builder::new).add(Sample.OUT, fitTime);
      ctx.getOrDefault(PredictTime.class, PredictTime.Builder::new).add(Sample.OUT, predictTime);
    }
    return Result.create(collect(ctx.builders()), confusionMatrices);
  }
}

package org.briljantframework.evaluation;

import static org.briljantframework.matrix.Matrices.argmax;

import java.util.ArrayList;
import java.util.List;

import org.briljantframework.classification.Classifier;
import org.briljantframework.classification.Predictor;
import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.evaluation.measure.FitTime;
import org.briljantframework.evaluation.measure.PredictTime;
import org.briljantframework.evaluation.measure.TrainingSetSize;
import org.briljantframework.evaluation.measure.ValidationSetSize;
import org.briljantframework.evaluation.result.*;
import org.briljantframework.matrix.DoubleMatrix;
import org.briljantframework.vector.Vector;
import org.briljantframework.vector.Vectors;

/**
 * The default
 * <p>
 * Created by Isak Karlsson on 01/12/14.
 */
public class DefaultValidator extends AbstractValidator {

  public DefaultValidator(List<Evaluator> consumers, Partitioner partitioner) {
    super(consumers, partitioner);
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
      Vector classes = predictor.getClasses();
      Vector predictions;
      if (predictor.getCharacteristics().contains(Predictor.Characteristics.ESTIMATOR)) {
        DoubleMatrix estimate = predictor.estimate(partition.getValidationData());
        ctx.setEstimation(estimate);
        Vector.Builder builder = y.newBuilder();
        for (int i = 0; i < estimate.rows(); i++) {
          builder.set(i, classes, argmax(estimate.getRowView(i)));
        }
        predictions = builder.build();
      } else {
        predictions = predictor.predict(partition.getValidationData());
      }
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

      ctx.getOrDefault(TrainingSetSize.class, TrainingSetSize.Builder::new).add(Sample.OUT,
          trainingData.rows());
      ctx.getOrDefault(ValidationSetSize.class, ValidationSetSize.Builder::new).add(Sample.OUT,
          partition.getValidationData().rows());
      ctx.getOrDefault(FitTime.class, FitTime.Builder::new).add(Sample.OUT, fitTime);
      ctx.getOrDefault(PredictTime.class, PredictTime.Builder::new).add(Sample.OUT, predictTime);
    }
    return Result.create(collect(ctx.builders()), confusionMatrices);
  }
}

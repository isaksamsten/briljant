package org.briljantframework.classification;

import java.util.Collections;
import java.util.List;

import org.briljantframework.array.Arrays;
import org.briljantframework.array.DoubleArray;
import org.briljantframework.data.dataframe.DataFrame;
import org.briljantframework.data.vector.Vector;
import org.briljantframework.evaluation.AbstractValidator;
import org.briljantframework.evaluation.Evaluator;
import org.briljantframework.evaluation.MutableEvaluationContext;
import org.briljantframework.evaluation.Validator;
import org.briljantframework.evaluation.classification.ProbabilityEvaluator;
import org.briljantframework.evaluation.classification.ZeroOneLossEvaluator;
import org.briljantframework.evaluation.partition.FoldPartitioner;
import org.briljantframework.evaluation.partition.LeaveOneOutPartitioner;
import org.briljantframework.evaluation.partition.Partition;
import org.briljantframework.evaluation.partition.Partitioner;
import org.briljantframework.evaluation.partition.SplitPartitioner;

/**
 * @author Isak Karlsson <isak-kar@dsv.su.se>
 */
public class ClassifierValidator<T extends Classifier> extends AbstractValidator<T> {

  private static final List<Evaluator<? super Classifier>> EVALUATORS = java.util.Arrays.asList(
      new ZeroOneLossEvaluator(), new ProbabilityEvaluator());

  public static <T extends Classifier> Validator<T> holdout(DataFrame testX, Vector testY) {
    return new ClassifierValidator<T>(EVALUATORS, (x, y) -> Collections.singleton(new Partition(x,
        testX, y, testY)));
  }

  public static <T extends Classifier> Validator<T> splitValidation(double testFraction) {
    return new ClassifierValidator<>(EVALUATORS, new SplitPartitioner(testFraction));
  }

  public static <T extends Classifier> Validator<T> leaveOneOutValidation() {
    return new ClassifierValidator<>(EVALUATORS, new LeaveOneOutPartitioner());
  }

  public static <T extends Classifier> Validator<T> crossValidation(int folds) {
    return new ClassifierValidator<>(EVALUATORS, new FoldPartitioner(folds));
  }

  public ClassifierValidator(List<? extends Evaluator<? super T>> evaluators,
      Partitioner partitioner) {
    super(evaluators, partitioner);
  }

  @Override
  protected void predict(MutableEvaluationContext<T> ctx) {
    Partition partition = ctx.getEvaluationContext().getPartition();
    DataFrame x = partition.getValidationData();
    Vector y = partition.getValidationTarget();
    T predictor = ctx.getPredictor();
    Vector.Builder builder = y.newBuilder();

    // For the case where the classifier reports the ESTIMATOR characteristic
    // improve the performance by avoiding to recompute the classifications twice.
    if (predictor.getCharacteristics().contains(ClassifierCharacteristic.ESTIMATOR)) {
      Vector classes = predictor.getClasses();
      DoubleArray estimate = predictor.estimate(x);
      ctx.setEstimation(estimate);
      for (int i = 0; i < estimate.rows(); i++) {
        builder.loc().set(i, classes, Arrays.argmax(estimate.getRow(i)));
      }
      ctx.setPredictions(builder.build());
    } else {
      ctx.setPredictions(predictor.predict(x));
    }
  }
}

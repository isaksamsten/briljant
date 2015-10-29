package org.briljantframework.classifier.conformal.evaluation;

import java.util.HashSet;
import java.util.Set;

import org.briljantframework.classification.ClassifierValidator;
import org.briljantframework.classifier.conformal.ConformalClassifier;
import org.briljantframework.classifier.evaluation.ProbabilityEvaluator;
import org.briljantframework.data.dataframe.DataFrame;
import org.briljantframework.data.vector.Vector;
import org.briljantframework.evaluation.Evaluator;
import org.briljantframework.evaluation.MutableEvaluationContext;
import org.briljantframework.evaluation.Validator;
import org.briljantframework.evaluation.partition.FoldPartitioner;
import org.briljantframework.evaluation.partition.Partition;
import org.briljantframework.evaluation.partition.Partitioner;
import org.briljantframework.evaluation.partition.SplitPartitioner;
import org.briljantframework.supervised.Predictor;

/**
 * @author Isak Karlsson <isak-kar@dsv.su.se>
 */
public final class ConformalClassifierValidator<T extends ConformalClassifier> extends
    ClassifierValidator<T> {

  /**
   * The default evaluators for conformal classifiers
   */
  private static final Set<Evaluator<? super ConformalClassifier>> EVALUATORS = new HashSet<>();
  static {
    EVALUATORS.add(ProbabilityEvaluator.getInstance());
  }

  private final double calibrationSize;

  public ConformalClassifierValidator(Set<? extends Evaluator<? super T>> evaluators,
      Partitioner partitioner, double calibrationSize) {
    super(evaluators, partitioner);
    this.calibrationSize = calibrationSize;
  }

  public ConformalClassifierValidator(Partitioner partitioner, double calibrationSize) {
    super(partitioner);
    this.calibrationSize = calibrationSize;
  }

  @Override
  protected void fit(Predictor.Learner<? extends T> learner, MutableEvaluationContext<? super T> ctx) {
    DataFrame x = ctx.getPartition().getTrainingData();
    Vector y = ctx.getPartition().getTrainingTarget();
    SplitPartitioner partitioner = new SplitPartitioner(calibrationSize);
    Partition partition = partitioner.partition(x, y).iterator().next();
    T fit = learner.fit(partition.getTrainingData(), partition.getTrainingTarget());
    fit.calibrate(partition.getValidationData(), partition.getValidationTarget());

    // Set the predictor
    ctx.setPredictor(fit);
  }

  /**
   * Returns a k-fold cross validator for evaluating conformal classifiers. For each fold, the
   * specified calibration set size is used. The default {@linkplain Evaluator evaluators} are the
   * {@link ProbabilityEvaluator} and the {@link ConformalClassifierEvaluator} (with the specified
   * confidence level)
   * 
   * @param folds the number of folds
   * @param calibrationSize the calibration set size (in each fold)
   * @param confidence the confidence for which to evaluate the
   * @param <T> the type of validator
   * @return a new validator for evaluating conformal classifiers of the specified type
   */
  public static <T extends ConformalClassifier> Validator<T> crossValidator(int folds,
      double calibrationSize, double confidence) {
    Validator<T> validator =
        new ConformalClassifierValidator<>(EVALUATORS, new FoldPartitioner(folds), calibrationSize);
    validator.add(new ConformalClassifierEvaluator(confidence));
    return validator;
  }
}

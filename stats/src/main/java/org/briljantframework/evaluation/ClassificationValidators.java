package org.briljantframework.evaluation;

import java.util.List;

import org.briljantframework.evaluation.result.Evaluator;

/**
 * @author Isak Karlsson
 */
public class ClassificationValidators {

  public static ClassificationValidator splitValidation(double testFraction) {
    return new DefaultClassificationValidator(new SplitPartitioner(testFraction));
  }

  public static ClassificationValidator crossValidation(int folds) {
    return new DefaultClassificationValidator(new FoldPartitioner(folds));
  }

  public static ClassificationValidator crossValidation(List<Evaluator> measures, int folds) {
    return new DefaultClassificationValidator(measures, new FoldPartitioner(folds));
  }
}

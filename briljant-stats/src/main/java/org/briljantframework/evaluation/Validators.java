package org.briljantframework.evaluation;

import java.util.List;

import org.briljantframework.evaluation.result.Evaluator;

/**
 * @author Isak Karlsson
 */
public class Validators {

  public static Validator splitValidation(double testFraction) {
    return new DefaultValidator(new SplitPartitioner(testFraction));
  }

  public static Validator leaveOneOutValidation() {
    return new DefaultValidator(new LeaveOneOutPartitioner());
  }

  public static Validator crossValidation(int folds) {
    return new DefaultValidator(new FoldPartitioner(folds));
  }

  public static Validator crossValidation(List<Evaluator> measures, int folds) {
    return new DefaultValidator(measures, new FoldPartitioner(folds));
  }
}

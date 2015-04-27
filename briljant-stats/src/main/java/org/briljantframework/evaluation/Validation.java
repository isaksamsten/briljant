package org.briljantframework.evaluation;

import org.briljantframework.Check;
import org.briljantframework.classification.Classifier;
import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.evaluation.result.Result;
import org.briljantframework.vector.Vector;

/**
 * @author Isak Karlsson
 */
public final class Validation {
  private Validation() {}

  public static Result cv(int folds, Classifier c, DataFrame x, Vector y) {
    Check.size(x, y);
    return Validators.crossValidation(folds).test(c, x, y);
  }

  public static Result loocv(Classifier c, DataFrame x, Vector y) {
    Check.size(x, y);
    return Validators.leaveOneOutValidation().test(c, x, y);
  }

  public static Result split(double testFraction, Classifier c, DataFrame x, Vector y) {
    Check.size(x, y);
    return Validators.splitValidation(testFraction).test(c, x, y);
  }

}

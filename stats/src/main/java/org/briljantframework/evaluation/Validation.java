package org.briljantframework.evaluation;

import org.briljantframework.classification.Classifier;
import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.evaluation.result.Result;
import org.briljantframework.vector.Vector;

/**
 * Created by isak on 03/12/14.
 */
public final class Validation {
  private Validation() {}

  public static Result cv(int folds, Classifier c, DataFrame x, Vector y) {
    return ClassificationEvaluators.crossValidation(folds).evaluate(c, x, y);
  }

}

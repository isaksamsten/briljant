package org.briljantframework.evaluation;

import org.briljantframework.Check;
import org.briljantframework.classification.Classifier;
import org.briljantframework.classification.Predictor;
import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.evaluation.result.Result;
import org.briljantframework.vector.Vector;

/**
 * @author Isak Karlsson
 */
public final class Validation {

  private Validation() {
  }

  public static Result test(Predictor predictor, DataFrame xTrain, Vector yTrain,
                            DataFrame xTest, Vector yTest) {
    Check.size(xTrain.rows(), yTrain.size());
    return HoldoutValidator.withHoldout(xTest, yTest).evaluate(predictor, xTrain, yTrain);
  }

  public static Result cv(int folds, Classifier c, DataFrame x, Vector y) {
    Check.size(x.rows(), y.size());
    return Validators.crossValidation(folds).test(c, x, y);
  }

  public static Result loocv(Classifier c, DataFrame x, Vector y) {
    Check.size(x.rows(), y.size());
    return Validators.leaveOneOutValidation().test(c, x, y);
  }

  public static Result split(double testFraction, Classifier c, DataFrame x, Vector y) {
    Check.size(x.rows(), y.size());
    return Validators.splitValidation(testFraction).test(c, x, y);
  }

}

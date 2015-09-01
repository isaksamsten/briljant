/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Isak Karlsson
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.briljantframework.evaluation;

import org.briljantframework.Check;
import org.briljantframework.classification.Classifier;
import org.briljantframework.classification.Predictor;
import org.briljantframework.data.dataframe.DataFrame;
import org.briljantframework.evaluation.result.Result;
import org.briljantframework.data.vector.Vector;

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

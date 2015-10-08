/*
 * The MIT License (MIT)
 * 
 * Copyright (c) 2015 Isak Karlsson
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.briljantframework.regression;


import org.briljantframework.array.DoubleArray;
import org.briljantframework.data.dataframe.DataFrame;
import org.briljantframework.data.vector.Vector;
import org.briljantframework.linalg.LinearAlgebra;

/**
 * @author Isak Karlsson
 */
public class LinearRegression implements RegressionLearner {

  public LinearRegression() {}

  @Override
  public Regressor fit(DoubleArray x, DoubleArray y) {
    return new Model(LinearAlgebra.leastLinearSquares(x, y));
  }

  /**
   * The type Predictor.
   */
  public static final class Model implements Regressor {

    private final DoubleArray theta;

    /**
     * Instantiates a new Predictor.
     *
     * @param theta the theta
     */
    public Model(DoubleArray theta) {
      this.theta = theta;
    }

    /**
     * Gets theta.
     *
     * @return the theta
     */
    public DoubleArray getTheta() {
      return theta;
    }

    @Override
    public double predict(Vector y) {
      return 0;
    }

    @Override
    public Vector predict(DataFrame x) {
      return null;
    }
  }
}

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

package org.briljantframework.regression;


import org.briljantframework.Check;
import org.briljantframework.array.DoubleArray;
import org.briljantframework.classification.AbstractPredictor;
import org.briljantframework.classification.Classifier;
import org.briljantframework.data.dataframe.DataFrame;
import org.briljantframework.data.vector.Vector;
import org.briljantframework.linalg.LinearAlgebra;

/**
 * Created by Isak Karlsson on 29/09/14.
 */
public class LinearRegression implements Classifier {

  private LinearRegression() {
  }

  /**
   * Create linear regression.
   *
   * @return the linear regression
   */
  public static LinearRegression create() {
    return new LinearRegression();
  }

  @Override
  public Model fit(DataFrame x, Vector y) {
    Check.argument(x.rows() == y.size());
    DoubleArray yMatrix = y.toDoubleArray();
    return new Model(LinearAlgebra.leastLinearSquares(x.toDoubleArray(), yMatrix));
  }

  /**
   * The type Predictor.
   */
  public static final class Model extends AbstractPredictor {

    private final DoubleArray theta;

    /**
     * Instantiates a new Predictor.
     *
     * @param theta the theta
     */
    public Model(DoubleArray theta) {
      super(null);
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
    public DoubleArray estimate(Vector record) {
      throw new UnsupportedOperationException();
    }
  }
}

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

package org.briljantframework.optimize;

import org.briljantframework.array.DoubleArray;

/**
 * @author Isak Karlsson
 */
public interface DifferentialFunction extends MultivariateFunction {

  /**
   * Compute the value of this function at {@code x} and the {@code gradient}.
   *
   * <p>
   * The default implementation uses finite differences between points in {@code x} to determine the
   * partial derivatives. This enables users to only supplie the cost-function intended to be
   * minimized.
   *
   * <p>
   * Note that the precision and, hence, convergence is often better when an analytic solution is
   * provided
   *
   * @param x the value (input)
   * @param gradient the gradient (output) (input = initial guess)
   * @return the value at {@code x}
   */
  default double gradientCost(DoubleArray x, DoubleArray gradient) {
    final double FINITE_DIFFERENCE_THRESHOLD = 1.0E-8;
    double f = cost(x);

    for (int j = 0; j < x.size(); j++) {
      double temp = x.get(j);
      double h = FINITE_DIFFERENCE_THRESHOLD * Math.abs(temp);
      if (h == 0.0) {
        h = FINITE_DIFFERENCE_THRESHOLD;
      }
      x.set(j, temp + h);
      h = x.get(j) - temp;
      double fh = cost(x);
      x.set(j, temp);
      gradient.set(j, (fh - f) / h);
    }
    return f;
  }
}

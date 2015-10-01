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

import org.briljantframework.Bj;
import org.briljantframework.array.DoubleArray;

/**
 * <p>
 * References
 * <ul>
 * <li>Numerical Recipes in C: The Art of Scientific Computing Second Edition, <i>Press, H.,
 * et.al</i>, Cambridge University Press 1992 pp. 285</li>
 * </ul>
 *
 * @author Isak Karlsson
 */
public class BacktrackingLineSearch {

  private final static double RELATIVE_TOLERANCE = Math.ulp(1.0);
  private final static double DECREASE_FRACTION = 1.0E-4;

  public BacktrackingLineSearch() {}

  /**
   * @param function the optimizable multivariate function
   * @param xold the point
   * @param fold the value of {@code function} at {@code xold}
   * @param g the the gradient of {@code xold}
   * @param p the direction
   * @param x the new point
   * @param maxStepSize the
   * @return the new value of {@code function}
   */
  public double optimize(MultivariateFunction function, DoubleArray xold, double fold,
      DoubleArray g, DoubleArray p, DoubleArray x, double maxStepSize) {
    int n = xold.size();
    double pnorm = Bj.norm2(p);
    if (pnorm > maxStepSize) {
      double r = maxStepSize / pnorm;
      Bj.scal(r, p);
    }
    double slope = Bj.dot(g, p);
    if (slope >= 0) {
      return Double.NaN;
    }

    double maxValue = 0;
    for (int i = 0; i < n; i++) {
      double v = Math.abs(p.get(i) / Math.max(xold.get(i), 1));
      if (v > maxValue) {
        maxValue = v;
      }
    }
    double minScale = RELATIVE_TOLERANCE / maxValue;
    double alam = 1.0;
    double alam2 = 0;
    double f2 = 0;

    double a, b, disc, rhs1, rhs2, tmpalam;
    for (;;) {
      for (int i = 0; i < n; i++) {
        x.set(i, xold.get(i) + alam * p.get(i));
      }
      double f = function.cost(x);

      if (alam < minScale) {
        Bj.copy(xold, x);
        return alam;
      } else if (f <= fold + DECREASE_FRACTION * alam * slope) {
        return f;
      } else {
        if (alam == 1) {
          tmpalam = -slope / (2.0 * (f - slope - fold));
        } else {
          rhs1 = f - fold - alam * slope;
          rhs2 = f2 - fold - alam2 * slope;
          a = (rhs1 / (alam * alam) - rhs2 / (alam2 * alam2)) / (alam - alam2);
          b = (-alam2 * rhs1 / (alam * alam) + alam * rhs2 / (alam2 * alam2)) / (alam - alam2);
          if (a == 0.0) {
            tmpalam = -slope / (2.0 * b);
          } else {
            disc = b * b - 3.0 * a * slope;
            if (disc < 0.0) {
              tmpalam = 0.5 * alam;
            } else if (b <= 0.0) {
              tmpalam = (-b + Math.sqrt(disc)) / (3.0 * a);
            } else {
              tmpalam = -slope / (b + Math.sqrt(disc));
            }
          }
          if (tmpalam > 0.5 * alam) {
            tmpalam = 0.5 * alam;
          }
        }
      }
      alam2 = alam;
      f2 = f;
      alam = Math.max(tmpalam, 0.1 * alam);
    }
  }
}

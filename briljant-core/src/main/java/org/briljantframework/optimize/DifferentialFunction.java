package org.briljantframework.optimize;

import org.briljantframework.matrix.DoubleMatrix;

/**
 * @author Isak Karlsson
 */
public interface DifferentialFunction extends MultivariateFunction {

  /**
   * Compute the value of this function at {@code x} and the {@code gradient}.
   *
   * <p> The default implementation uses finite differences between points in {@code x}
   * to determine the partial derivatives. This enables users to only supplie the cost-function
   * intended to be minimized.
   *
   * <p> Note that the precision and, hence, convergence is often better when an analytic solution
   * is provided
   *
   * @param x        the value (input)
   * @param gradient the gradient (output) (input = initial guess)
   * @return the value at {@code x}
   */
  default double gradientCost(DoubleMatrix x, DoubleMatrix gradient) {
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

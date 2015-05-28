package org.briljantframework.optimize;

import org.briljantframework.matrix.DoubleMatrix;

/**
 * @author Isak Karlsson
 */
public interface MultivariateFunction {

  /**
   * Compute the cost at value {@code x}
   *
   * @param x the value
   * @return the cost
   */
  double cost(DoubleMatrix x);
}

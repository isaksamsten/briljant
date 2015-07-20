package org.briljantframework.optimize;

import org.briljantframework.array.DoubleArray;

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
  double cost(DoubleArray x);
}

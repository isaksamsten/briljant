package org.briljantframework.optimize;

import org.briljantframework.matrix.DoubleArray;

/**
 * Minimize a non-linear multivariate function
 *
 * @author Isak Karlsson
 */
public interface NonlinearOptimizer {

  /**
   * @param function cost function to minimize
   * @param x        the initial guess
   * @return the final cost
   */
  double optimize(DifferentialFunction function, DoubleArray x);
}

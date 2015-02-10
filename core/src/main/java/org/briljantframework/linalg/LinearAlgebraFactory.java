package org.briljantframework.linalg;

import org.briljantframework.matrix.DoubleMatrix;

/**
 * Created by isak on 2/10/15.
 */
public interface LinearAlgebraFactory {

  /**
   * Computes the sum of the absolute values of elements in a vector (double-precision).
   * 
   * @param x
   * @return
   */
  double asum(DoubleMatrix x);

  /**
   * Computes a constant times a vector plus a vector (double-precision).
   * 
   * @param alpha
   * @param x
   * @param y
   * @return
   */
  DoubleMatrix axpy(double alpha, DoubleMatrix x, DoubleMatrix y);

  /**
   * Copies a vector to another vector (double-precision).
   * 
   * @param source
   * @param dest
   */
  void copy(DoubleMatrix source, DoubleMatrix dest);
}

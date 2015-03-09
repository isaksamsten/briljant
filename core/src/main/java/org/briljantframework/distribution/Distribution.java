package org.briljantframework.distribution;

import java.util.Random;

import org.briljantframework.matrix.DoubleMatrix;

/**
 * @author Isak Karlsson
 */
public abstract class Distribution {

  protected final Random random;

  protected Distribution(Random random) {
    this.random = random;
  }

  protected Distribution() {
    this(new Random());
  }

  /**
   * Return a new pseudo-randomly generated number from this distribution.
   * 
   * @return a new random number
   */
  public abstract double next();

  /**
   * Return a vector of {@code size} consisting of pseudo-randomly generated numbers from this
   * distribution.
   * 
   * <p>
   * This is the same as
   * 
   * <pre>
   * Distribution dist;
   * DoubleMatrix rand = DoubleMatrix.newVector(size).assign(dist::next);
   * </pre>
   * 
   * @param size the size of the vector
   * @return a new double matrix with pseudo-random number
   */
  public DoubleMatrix next(int size) {
    return DoubleMatrix.newVector(size).assign(this::next);
  }

  /**
   * Return a matrix with dimensions {@code rows} and {@columns} consisting of pseudo-randomly
   * generated numbers from this distribution.
   * 
   * <p>
   * This is the same as
   * 
   * <pre>
   * Distributin dist;
   * DoubleMatrix rand = DoubleMatrix.newMatrix(rows, columns).assign(dist::next);
   * // or alternatively
   * dist.next(rows*columns).reshape(rows, columns)
   * </pre>
   * 
   * @param rows the number of rows
   * @param columns the number of columns
   * @return a matrix; shape = {@code [rows, columns]}
   */
  public DoubleMatrix next(int rows, int columns) {
    return DoubleMatrix.newMatrix(rows, columns).assign(this::next);
  }
}

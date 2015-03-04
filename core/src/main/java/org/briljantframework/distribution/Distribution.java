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


  public abstract double next();

  public DoubleMatrix next(int size) {
    DoubleMatrix matrix = DoubleMatrix.newVector(size);
    return matrix.assign(this::next);
  }

  public DoubleMatrix next(int rows, int columns) {
    return DoubleMatrix.newMatrix(rows, columns).assign(this::next);
  }
}

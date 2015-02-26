package org.briljantframework.distribution;

import java.util.Random;

import org.briljantframework.matrix.DoubleMatrix;
import org.briljantframework.matrix.Matrices;

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
    DoubleMatrix matrix = Matrices.newDoubleVector(size);
    return matrix.assign(this::next);
  }

  public DoubleMatrix next(int rows, int columns) {
    return Matrices.newDoubleMatrix(rows, columns).assign(this::next);
  }
}

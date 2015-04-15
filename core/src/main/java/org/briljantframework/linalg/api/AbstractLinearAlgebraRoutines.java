package org.briljantframework.linalg.api;

import com.google.common.base.Preconditions;

import org.briljantframework.matrix.api.MatrixFactory;

/**
 * @author Isak Karlsson
 */
public abstract class AbstractLinearAlgebraRoutines implements LinearAlgebraRoutines {

  private final MatrixFactory matrixFactory;

  protected AbstractLinearAlgebraRoutines(MatrixFactory matrixFactory) {
    this.matrixFactory = Preconditions.checkNotNull(matrixFactory);
  }

  public MatrixFactory getMatrixFactory() {
    return matrixFactory;
  }
}

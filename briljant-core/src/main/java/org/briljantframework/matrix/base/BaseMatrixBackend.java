package org.briljantframework.matrix.base;

import org.briljantframework.linalg.api.LinearAlgebraRoutines;
import org.briljantframework.matrix.api.MatrixBackend;
import org.briljantframework.matrix.api.MatrixFactory;
import org.briljantframework.matrix.api.MatrixRoutines;

/**
 * @author Isak Karlsson
 */
public class BaseMatrixBackend implements MatrixBackend {

  private MatrixFactory matrixFactory;
  private MatrixRoutines matrixRoutines;
  private LinearAlgebraRoutines linearAlgebraRoutines;

  @Override
  public boolean isAvailable() {
    return true;
  }

  @Override
  public int getPriority() {
    return Integer.MIN_VALUE;
  }

  @Override
  public MatrixFactory getMatrixFactory() {
    if (matrixFactory == null) {
      matrixFactory = new BaseMatrixFactory();
    }
    return matrixFactory;
  }

  @Override
  public MatrixRoutines getMatrixRoutines() {
    if (matrixRoutines == null) {
      matrixRoutines = new BaseMatrixRoutines(this);
    }
    return matrixRoutines;
  }

  @Override
  public LinearAlgebraRoutines getLinearAlgebraRoutines() {
    if (linearAlgebraRoutines == null) {
      linearAlgebraRoutines = new BaseLinearAlgebraRoutines(this);
    }
    return linearAlgebraRoutines;
  }
}

package org.briljantframework.matrix.netlib;

import org.briljantframework.linalg.api.LinearAlgebraRoutines;
import org.briljantframework.matrix.api.MatrixBackend;
import org.briljantframework.matrix.api.MatrixFactory;
import org.briljantframework.matrix.api.MatrixRoutines;

/**
 * Created by isak on 27/04/15.
 */
public class NetlibMatrixBackend implements MatrixBackend {

  private MatrixFactory matrixFactory;
  private MatrixRoutines matrixRoutines;
  private LinearAlgebraRoutines linearAlgebraRoutines;

  @Override
  public boolean isAvailable() {
    return true;
  }

  @Override
  public int getPriority() {
    return 100;
  }

  @Override
  public MatrixFactory getMatrixFactory() {
    if (matrixFactory == null) {
      matrixFactory = new NetlibMatrixFactory();
    }
    return matrixFactory;
  }

  @Override
  public MatrixRoutines getMatrixRoutines() {
    if (matrixRoutines == null) {
      matrixRoutines = new NetlibMatrixRoutines(this);
    }
    return matrixRoutines;
  }

  @Override
  public LinearAlgebraRoutines getLinearAlgebraRoutines() {
    if (linearAlgebraRoutines == null) {
      linearAlgebraRoutines = new NetlibLinearAlgebraRoutines(this);
    }
    return linearAlgebraRoutines;
  }
}

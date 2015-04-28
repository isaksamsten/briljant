package org.briljantframework.linalg.api;

import com.google.common.base.Preconditions;

import org.briljantframework.matrix.api.MatrixBackend;
import org.briljantframework.matrix.netlib.NetlibMatrixBackend;

/**
 * @author Isak Karlsson
 */
public abstract class AbstractLinearAlgebraRoutines implements LinearAlgebraRoutines {

  private final MatrixBackend matrixBackend;

  protected AbstractLinearAlgebraRoutines(MatrixBackend matrixFactory) {
    this.matrixBackend = Preconditions.checkNotNull(matrixFactory);
  }

  protected MatrixBackend getMatrixBackend() {
    return matrixBackend;
  }
}

package org.briljantframework.matrix.netlib;

import org.briljantframework.linalg.api.LinearAlgebraRoutines;
import org.briljantframework.matrix.api.MatrixBackend;
import org.briljantframework.matrix.api.MatrixFactory;
import org.briljantframework.matrix.api.MatrixRoutines;

import java.util.logging.Handler;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * @author Isak Karlsson
 */
public class NetlibMatrixBackend implements MatrixBackend {

  static {
    // This should suppress the output from the JNI logger
    Logger blasLogger = LogManager.getLogManager().getLogger("");
    if (blasLogger != null) {
      for (Handler handler : blasLogger.getHandlers()) {
        handler.close();
        blasLogger.removeHandler(handler);
      }
    }
  }

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
      matrixRoutines = new NetlibMatrixRoutines();
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

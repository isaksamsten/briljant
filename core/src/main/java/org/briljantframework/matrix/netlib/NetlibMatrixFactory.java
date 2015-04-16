package org.briljantframework.matrix.netlib;

import org.briljantframework.linalg.api.LinearAlgebraRoutines;
import org.briljantframework.matrix.DoubleMatrix;
import org.briljantframework.matrix.api.MatrixFactory;
import org.briljantframework.matrix.api.MatrixRoutines;
import org.briljantframework.matrix.base.BaseMatrixFactory;

/**
 * @author Isak Karlsson
 */
public class NetlibMatrixFactory extends BaseMatrixFactory {

  private final LinearAlgebraRoutines linalg = new NetlibLinearAlgebraRoutines(this);
  private final MatrixRoutines blas = new NetlibMatrixRoutines(this);

  public NetlibMatrixFactory() {
  }

  @Override
  public DoubleMatrix matrix(double[] data) {
    return new NetlibDoubleMatrix(this, data);
  }

  @Override
  public DoubleMatrix doubleMatrix(int rows, int columns) {
    return new NetlibDoubleMatrix(this, rows, columns);
  }

  @Override
  public DoubleMatrix doubleVector(int size) {
    return new NetlibDoubleMatrix(this, size);
  }

  @Override
  public MatrixRoutines getMatrixRoutines() {
    return blas;
  }

  @Override
  public LinearAlgebraRoutines getLinearAlgebraRoutines() {
    return linalg;
  }

  public static MatrixFactory getInstance() {
    return new NetlibMatrixFactory();
  }
}

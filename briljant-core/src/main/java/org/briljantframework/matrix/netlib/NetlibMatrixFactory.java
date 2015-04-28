package org.briljantframework.matrix.netlib;

import org.briljantframework.matrix.DoubleMatrix;
import org.briljantframework.matrix.api.MatrixFactory;
import org.briljantframework.matrix.base.BaseMatrixFactory;

/**
 * @author Isak Karlsson
 */
class NetlibMatrixFactory extends BaseMatrixFactory {

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

  public static MatrixFactory getInstance() {
    return new NetlibMatrixFactory();
  }
}

package org.briljantframework.matrix.netlib;

import com.github.fommil.netlib.BLAS;

import org.briljantframework.exceptions.NonConformantException;
import org.briljantframework.matrix.AbstractDoubleArray;
import org.briljantframework.matrix.DoubleArray;
import org.briljantframework.matrix.Op;
import org.briljantframework.matrix.api.ArrayFactory;

import java.util.Objects;

/**
 * @author Isak Karlsson
 */
class NetlibDoubleArray extends AbstractDoubleArray {

  private static BLAS blas = BLAS.getInstance();
  private final double[] data;

  NetlibDoubleArray(ArrayFactory bj, int size) {
    super(bj, size);
    data = new double[size];
  }

  NetlibDoubleArray(ArrayFactory bj, double[] data) {
    super(bj, Objects.requireNonNull(data).length);
    this.data = data;
  }

  public NetlibDoubleArray(ArrayFactory bj, int... shape) {
    super(bj, shape);
    this.data = new double[size()];
  }

  public NetlibDoubleArray(ArrayFactory bj, int offset, int[] shape, int[] stride,
                           double[] data) {
    super(bj, offset, shape, stride);
    this.data = data;
  }

  public NetlibDoubleArray(ArrayFactory bj, double[] data, int rows, int columns) {
    super(bj, rows, columns);
    this.data = data;
  }

  @Override
  protected double getElement(int i) {
    return data[i];
  }

  @Override
  protected void setElement(int i, double value) {
    data[i] = value;
  }

  @Override
  public DoubleArray newEmptyArray(int... shape) {
    return new NetlibDoubleArray(getMatrixFactory(), shape);
  }

  @Override
  protected DoubleArray makeView(int offset, int[] shape, int[] stride) {
    return new NetlibDoubleArray(
        getMatrixFactory(),
        offset,
        shape,
        stride,
        data
    );
  }

  @Override
  public DoubleArray mmul(double alpha, Op a, DoubleArray other, Op b) {
    if (!isView() && !other.isView()) {
      String transA = "n";
      int thisRows = rows();
      int thisColumns = columns();
      if (a.isTrue()) {
        thisRows = columns();
        thisColumns = rows();
        transA = "t";
      }

      String transB = "n";
      int otherRows = other.rows();
      int otherColumns = other.columns();
      if (b.isTrue()) {
        otherRows = other.columns();
        otherColumns = other.rows();
        transB = "t";
      }

      if (thisColumns != otherRows) {
        throw new NonConformantException(this, other);
      }
      double[] tmp = new double[Math.multiplyExact(thisRows, otherColumns)];
      blas.dgemm(transA, transB, thisRows, otherColumns, otherRows, alpha, data,
                 this.rows(), other.data(), other.rows(), 0, tmp,
                 thisRows);

      return new NetlibDoubleArray(getMatrixFactory(), tmp, thisRows, otherColumns);
    } else {
      return super.mmul(alpha, a, other, b);
    }

  }

  @Override
  public boolean isView() {
    return false;
  }

  @Override
  public double[] data() {
    return data;
  }
}

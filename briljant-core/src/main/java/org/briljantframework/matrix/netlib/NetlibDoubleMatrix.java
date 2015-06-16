package org.briljantframework.matrix.netlib;

import com.github.fommil.netlib.BLAS;

import org.briljantframework.Check;
import org.briljantframework.exceptions.NonConformantException;
import org.briljantframework.matrix.AbstractDoubleMatrix;
import org.briljantframework.matrix.DoubleMatrix;
import org.briljantframework.matrix.Indexer;
import org.briljantframework.matrix.T;
import org.briljantframework.matrix.api.MatrixFactory;

/**
 * @author Isak Karlsson
 */
class NetlibDoubleMatrix extends AbstractDoubleMatrix {

  private static BLAS blas = BLAS.getInstance();
  private final double[] data;

  NetlibDoubleMatrix(MatrixFactory bj, int size) {
    super(bj, size);
    data = new double[size];
  }

  NetlibDoubleMatrix(MatrixFactory bj, int rows, int columns) {
    super(bj, rows, columns);
    data = new double[Math.multiplyExact(rows, columns)];
  }

  NetlibDoubleMatrix(MatrixFactory bj, double[] data, int rows, int columns) {
    super(bj, rows, columns);
    Check.size(size(), data.length);
    this.data = data;
  }

  public NetlibDoubleMatrix(MatrixFactory bj, double[] data) {
    this(bj, data, data.length, 1);
  }

  @Override
  public DoubleMatrix asDoubleMatrix() {
    return this;
  }

  @Override
  public void set(int i, int j, double value) {
    data[Indexer.columnMajor(0, i, j, rows(), columns())] = value;
  }

  @Override
  public void set(int index, double value) {
    data[index] = value;
  }

  @Override
  public double get(int i, int j) {
    return data[Indexer.columnMajor(0, i, j, rows(), columns())];
  }

  @Override
  public double get(int index) {
    return data[index];
  }

  @Override
  public DoubleMatrix mmul(double alpha, T a, DoubleMatrix other, T b) {
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

      return new NetlibDoubleMatrix(getMatrixFactory(), tmp, thisRows, otherColumns);
    } else {
      return super.mmul(alpha, a, other, b);
    }

  }

  @Override
  public DoubleMatrix reshape(int rows, int columns) {
    Check.size(CHANGED_TOTAL_SIZE, Math.multiplyExact(rows, columns), this);
    return new NetlibDoubleMatrix(getMatrixFactory(), data, rows, columns);
  }

  @Override
  public DoubleMatrix copy() {
    return new NetlibDoubleMatrix(
        getMatrixFactory(), data.clone(), rows(), columns());
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

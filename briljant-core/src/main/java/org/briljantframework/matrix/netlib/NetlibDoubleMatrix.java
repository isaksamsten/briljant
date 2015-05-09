package org.briljantframework.matrix.netlib;

import com.github.fommil.netlib.BLAS;

import org.briljantframework.Check;
import org.briljantframework.exceptions.NonConformantException;
import org.briljantframework.matrix.AbstractDoubleMatrix;
import org.briljantframework.matrix.DoubleMatrix;
import org.briljantframework.matrix.Indexer;
import org.briljantframework.matrix.T;
import org.briljantframework.matrix.api.MatrixFactory;
import org.briljantframework.matrix.storage.DoubleArrayStorage;
import org.briljantframework.matrix.storage.DoubleStorage;
import org.briljantframework.matrix.storage.Storage;

/**
 * @author Isak Karlsson
 */
class NetlibDoubleMatrix extends AbstractDoubleMatrix {

  private static BLAS blas = BLAS.getInstance();

  private final DoubleStorage values;

  NetlibDoubleMatrix(MatrixFactory bj, int size) {
    super(bj, size);
    values = new DoubleStorage(new double[size]);
  }

  NetlibDoubleMatrix(MatrixFactory bj, int rows, int columns) {
    super(bj, rows, columns);
    values = new DoubleStorage(new double[Math.multiplyExact(rows, columns)]);
  }

  NetlibDoubleMatrix(MatrixFactory bj, DoubleStorage values, int rows, int columns) {
    super(bj, rows, columns);
    Check.size(size(), values.size());
    this.values = values;
  }

  public NetlibDoubleMatrix(MatrixFactory bj, double[] data) {
    this(bj, new DoubleStorage(data), data.length, 1);
  }

  @Override
  public DoubleMatrix asDoubleMatrix() {
    return this;
  }

  @Override
  public void set(int i, int j, double value) {
    values.setDouble(Indexer.columnMajor(i, j, rows(), columns()), value);
  }

  @Override
  public void set(int index, double value) {
    values.setDouble(index, value);
  }

  @Override
  public double get(int i, int j) {
    return values.getDouble(Indexer.columnMajor(i, j, rows(), columns()));
  }

  @Override
  public double get(int index) {
    return values.getDouble(index);
  }

  @Override
  public DoubleMatrix addi(double alpha, DoubleMatrix other) {
    Check.equalShape(this, other);
    Storage o = other.getStorage();
    if (o.getNativeType().equals(Double.TYPE) && o instanceof DoubleArrayStorage) {
      double[] dy = o.doubleArray();
      blas.daxpy(size(), alpha, dy, 1, values.doubleArray(), 1);
    } else {
      super.addi(alpha, other);
    }
    return this;
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
      Storage otherStorage = other.getStorage();
      double[] tmp = new double[Math.multiplyExact(thisRows, otherColumns)];

      blas.dgemm(transA, transB, thisRows, otherColumns, otherRows, alpha, values.doubleArray(),
                 this.rows(), otherStorage.doubleArray(), other.rows(), 0, tmp,
                 thisRows);

      return new NetlibDoubleMatrix(getMatrixFactory(), new DoubleStorage(tmp), thisRows,
                                    otherColumns);
    } else {
      return super.mmul(alpha, a, other, b);
    }

  }

  @Override
  public DoubleMatrix reshape(int rows, int columns) {
    Check.size(CHANGED_TOTAL_SIZE, Math.multiplyExact(rows, columns), this);
    return new NetlibDoubleMatrix(getMatrixFactory(), values, rows, columns);
  }

  @Override
  public DoubleMatrix copy() {
    return new NetlibDoubleMatrix(
        getMatrixFactory(), (DoubleStorage) values.copy(), rows(), columns());
  }

  @Override
  public DoubleMatrix newEmptyMatrix(int rows, int columns) {
    return new NetlibDoubleMatrix(getMatrixFactory(), rows, columns);
  }

  @Override
  public boolean isView() {
    return false;
  }

  @Override
  public Storage getStorage() {
    return values;
  }
}

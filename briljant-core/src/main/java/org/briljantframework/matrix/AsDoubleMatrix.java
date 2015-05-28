package org.briljantframework.matrix;

import org.briljantframework.Check;
import org.briljantframework.matrix.api.MatrixFactory;
import org.briljantframework.matrix.storage.Storage;

abstract class AsDoubleMatrix extends AbstractDoubleMatrix {

  public AsDoubleMatrix(MatrixFactory bj, int rows, int columns) {
    super(bj, rows, columns);
  }

  @Override
  public DoubleMatrix reshape(int rows, int columns) {
    Check.size(CHANGED_TOTAL_SIZE, Math.multiplyExact(rows, columns), this);
    return new AsDoubleMatrix(bj, rows, columns) {
      @Override
      public void set(int index, double value) {
        AsDoubleMatrix.this.set(index, value);
      }

      @Override
      public void set(int i, int j, double value) {
        set(Indexer.columnMajor(i, j, rows(), columns()), value);
      }

      @Override
      public double get(int i, int j) {
        return get(Indexer.columnMajor(i, j, rows(), columns()));
      }

      @Override
      public double get(int index) {
        return AsDoubleMatrix.this.get(index);
      }

    };
  }

  @Override
  public DoubleMatrix newEmptyMatrix(int rows, int columns) {
    return bj.doubleMatrix(rows, columns);
  }

  @Override
  public final boolean isView() {
    return true;
  }
}
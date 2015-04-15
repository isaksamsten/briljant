package org.briljantframework.matrix;

import org.briljantframework.Check;
import org.briljantframework.matrix.api.MatrixFactory;

abstract class AsDoubleMatrix extends AbstractDoubleMatrix {

  public AsDoubleMatrix(MatrixFactory bj, int rows, int columns) {
    super(bj, rows, columns);
  }

  @Override
  public DoubleMatrix reshape(int rows, int columns) {
    Check.size(CHANGED_TOTAL_SIZE, Math.multiplyExact(rows, columns), this);
    return new AsDoubleMatrix(bj, rows, columns) {
      @Override
      public void set(int row, int column, double value) {
        AsDoubleMatrix.this.set(row, column, value);
      }

      @Override
      public void set(int index, double value) {
        AsDoubleMatrix.this.set(index, value);
      }

      @Override
      public double get(int i, int j) {
        return AsDoubleMatrix.this.get(i, j);
      }

      @Override
      public double get(int index) {
        return AsDoubleMatrix.this.get(index);
      }

      @Override
      public Storage getStorage() {
        return AsDoubleMatrix.this.getStorage();
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
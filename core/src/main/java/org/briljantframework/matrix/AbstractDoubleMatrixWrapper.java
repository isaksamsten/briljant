package org.briljantframework.matrix;

import org.briljantframework.Check;
import org.briljantframework.matrix.storage.Storage;

abstract class AbstractDoubleMatrixWrapper extends AbstractDoubleMatrix {

  public AbstractDoubleMatrixWrapper(int rows, int columns) {
    super(rows, columns);
  }

  @Override
  public DoubleMatrix reshape(int rows, int columns) {
    Check.size(CHANGED_TOTAL_SIZE, Math.multiplyExact(rows, columns), this);
    return new AbstractDoubleMatrixWrapper(rows, columns) {
      @Override
      public void set(int row, int column, double value) {
        AbstractDoubleMatrixWrapper.this.set(row, column, value);
      }

      @Override
      public void set(int index, double value) {
        AbstractDoubleMatrixWrapper.this.set(index, value);
      }

      @Override
      public Storage getStorage() {
        return AbstractDoubleMatrixWrapper.this.getStorage();
      }

      @Override
      public double get(int i, int j) {
        return AbstractDoubleMatrixWrapper.this.get(i, j);
      }

      @Override
      public double get(int index) {
        return AbstractDoubleMatrixWrapper.this.get(index);
      }


    };
  }

  @Override
  public DoubleMatrix newEmptyMatrix(int rows, int columns) {
    return DoubleMatrix.newMatrix(rows, columns);
  }

  @Override
  public boolean isView() {
    return true;
  }

  @Override
  public boolean isArrayBased() {
    return false;
  }


}
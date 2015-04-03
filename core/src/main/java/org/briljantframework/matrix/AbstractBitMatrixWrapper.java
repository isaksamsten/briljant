package org.briljantframework.matrix;

import org.briljantframework.Check;
import org.briljantframework.matrix.storage.Storage;

/**
 * @author Isak Karlsson
 */
abstract class AbstractBitMatrixWrapper extends AbstractBitMatrix {

  public AbstractBitMatrixWrapper(int rows, int columns) {
    super(rows, columns);
  }

  @Override
  public BitMatrix reshape(int rows, int columns) {
    Check.size(CHANGED_TOTAL_SIZE, Math.multiplyExact(rows, columns), this);
    return new AbstractBitMatrixWrapper(rows, columns) {
      @Override
      public void set(int row, int column, boolean value) {
        AbstractBitMatrixWrapper.this.set(row, column, value);
      }

      @Override
      public void set(int index, boolean value) {
        AbstractBitMatrixWrapper.this.set(index, value);
      }

      @Override
      public Storage getStorage() {
        return AbstractBitMatrixWrapper.this.getStorage();
      }

      @Override
      public boolean get(int i, int j) {
        return AbstractBitMatrixWrapper.this.get(i, j);
      }

      @Override
      public boolean get(int index) {
        return AbstractBitMatrixWrapper.this.get(index);
      }


    };
  }


  @Override
  public BitMatrix newEmptyMatrix(int rows, int columns) {
    return BitMatrix.newMatrix(rows, columns);
  }

  @Override
  public boolean isView() {
    return true;
  }
}

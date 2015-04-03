package org.briljantframework.matrix;

import org.briljantframework.Check;
import org.briljantframework.matrix.storage.Storage;

/**
 * @author Isak Karlsson
 */
abstract class AbstractIntMatrixWrapper extends AbstractIntMatrix {

  public AbstractIntMatrixWrapper(int rows, int columns) {
    super(rows, columns);
  }

  @Override
  public IntMatrix reshape(int rows, int columns) {
    Check.size(CHANGED_TOTAL_SIZE, Math.multiplyExact(rows, columns), this);
    return new AbstractIntMatrixWrapper(rows, columns) {
      @Override
      public int get(int i, int j) {
        return AbstractIntMatrixWrapper.this.get(i, j);
      }

      @Override
      public int get(int index) {
        return AbstractIntMatrixWrapper.this.get(index);
      }

      @Override
      public Storage getStorage() {
        return AbstractIntMatrixWrapper.this.getStorage();
      }

      @Override
      public void set(int index, int value) {
        AbstractIntMatrixWrapper.this.set(index, value);
      }

      @Override
      public void set(int row, int column, int value) {
        AbstractIntMatrixWrapper.this.set(row, column, value);
      }


    };
  }

  @Override
  public IntMatrix newEmptyMatrix(int rows, int columns) {
    return IntMatrix.newMatrix(rows, columns);
  }

  @Override
  public boolean isView() {
    return true;
  }
}

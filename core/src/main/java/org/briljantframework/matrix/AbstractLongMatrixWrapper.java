package org.briljantframework.matrix;

import org.briljantframework.Check;
import org.briljantframework.matrix.storage.Storage;

abstract class AbstractLongMatrixWrapper extends AbstractLongMatrix {

  public AbstractLongMatrixWrapper(int rows, int columns) {
    super(rows, columns);
  }

  @Override
  public AbstractLongMatrix reshape(int rows, int columns) {
    Check.size(CHANGED_TOTAL_SIZE, Math.multiplyExact(rows, columns), this);
    return new AbstractLongMatrixWrapper(rows, columns) {
      @Override
      public long get(int i, int j) {
        return AbstractLongMatrixWrapper.this.get(i, j);
      }

      @Override
      public long get(int index) {
        return AbstractLongMatrixWrapper.this.get(index);
      }

      @Override
      public Storage getStorage() {
        return AbstractLongMatrixWrapper.this.getStorage();
      }

      @Override
      public void set(int index, long value) {
        AbstractLongMatrixWrapper.this.set(index, value);
      }

      @Override
      public void set(int row, int column, long value) {
        AbstractLongMatrixWrapper.this.set(row, column, value);
      }


    };
  }

  @Override
  public boolean isView() {
    return true;
  }

  @Override
  public LongMatrix newEmptyMatrix(int rows, int columns) {
    return LongMatrix.newMatrix(rows, columns);
  }


}
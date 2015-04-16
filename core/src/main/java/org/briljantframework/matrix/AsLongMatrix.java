package org.briljantframework.matrix;

import org.briljantframework.Check;
import org.briljantframework.matrix.api.MatrixFactory;
import org.briljantframework.matrix.storage.Storage;

abstract class AsLongMatrix extends AbstractLongMatrix {

  public AsLongMatrix(MatrixFactory bj, int rows, int columns) {
    super(bj, rows, columns);
  }

  @Override
  public AbstractLongMatrix reshape(int rows, int columns) {
    Check.size(CHANGED_TOTAL_SIZE, Math.multiplyExact(rows, columns), this);
    return new AsLongMatrix(bj, rows, columns) {
      @Override
      public long get(int i, int j) {
        return AsLongMatrix.this.get(i, j);
      }

      @Override
      public long get(int index) {
        return AsLongMatrix.this.get(index);
      }

      @Override
      public void set(int index, long value) {
        AsLongMatrix.this.set(index, value);
      }

      @Override
      public void set(int row, int column, long value) {
        AsLongMatrix.this.set(row, column, value);
      }

      @Override
      public Storage getStorage() {
        return AsLongMatrix.this.getStorage();
      }
    };
  }

  @Override
  public boolean isView() {
    return true;
  }

  @Override
  public LongMatrix newEmptyMatrix(int rows, int columns) {
    return bj.longMatrix(rows, columns);
  }


}
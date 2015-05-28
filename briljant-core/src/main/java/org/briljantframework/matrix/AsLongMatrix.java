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
        return get(Indexer.columnMajor(i, j, rows(), columns()));
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
      public void set(int i, int j, long value) {
        set(Indexer.columnMajor(i, j, rows(), columns()), value);
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
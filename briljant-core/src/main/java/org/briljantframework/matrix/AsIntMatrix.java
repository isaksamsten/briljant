package org.briljantframework.matrix;

import org.briljantframework.Check;
import org.briljantframework.matrix.api.MatrixFactory;

/**
 * @author Isak Karlsson
 */
public abstract class AsIntMatrix extends AbstractIntMatrix {

  public AsIntMatrix(MatrixFactory bj, int rows, int columns) {
    super(bj, rows, columns);
  }

  @Override
  public IntMatrix reshape(int rows, int columns) {
    Check.size(CHANGED_TOTAL_SIZE, Math.multiplyExact(rows, columns), this);
    return new AsIntMatrix(bj, rows, columns) {
      @Override
      public int get(int i, int j) {
        return AsIntMatrix.this.get(Indexer.columnMajor(0, i, j, rows(), columns()));
      }

      @Override
      public int get(int index) {
        return AsIntMatrix.this.get(index);
      }

      @Override
      public void set(int index, int value) {
        AsIntMatrix.this.set(index, value);
      }

      @Override
      public void set(int i, int j, int value) {
        set(Indexer.columnMajor(0, i, j, rows(), columns()), value);
      }

    };
  }

  @Override
  public boolean isView() {
    return true;
  }
}

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
        return AsIntMatrix.this.get(i, j);
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
      public void set(int row, int column, int value) {
        AsIntMatrix.this.set(row, column, value);
      }

      @Override
      public Storage getStorage() {
        return AsIntMatrix.this.getStorage();
      }
    };
  }

  @Override
  public IntMatrix newEmptyMatrix(int rows, int columns) {
    return bj.intMatrix(rows, columns);
  }

  @Override
  public boolean isView() {
    return true;
  }
}

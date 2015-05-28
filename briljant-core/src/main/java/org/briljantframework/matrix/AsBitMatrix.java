package org.briljantframework.matrix;

import org.briljantframework.Check;
import org.briljantframework.matrix.api.MatrixFactory;

/**
 * @author Isak Karlsson
 */
abstract class AsBitMatrix extends AbstractBitMatrix {

  public AsBitMatrix(MatrixFactory bj, int rows, int columns) {
    super(bj, rows, columns);
  }

  @Override
  public BitMatrix reshape(int rows, int columns) {
    Check.size(CHANGED_TOTAL_SIZE, Math.multiplyExact(rows, columns), this);
    return new AsBitMatrix(bj, rows, columns) {
      @Override
      public void set(int i, int j, boolean value) {
        set(Indexer.columnMajor(i, j, rows(), columns()), value);
      }

      @Override
      public void set(int index, boolean value) {
        AsBitMatrix.this.set(index, value);
      }

      @Override
      public boolean get(int i, int j) {
        return get(Indexer.columnMajor(i, j, rows(), columns()));
      }

      @Override
      public boolean get(int index) {
        return AsBitMatrix.this.get(index);
      }
    };
  }

  @Override
  public BitMatrix newEmptyMatrix(int rows, int columns) {
    return bj.booleanMatrix(rows, columns);
  }

  @Override
  public boolean isView() {
    return true;
  }
}

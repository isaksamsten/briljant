package org.briljantframework.matrix.base;

import org.briljantframework.Check;
import org.briljantframework.matrix.AbstractBitMatrix;
import org.briljantframework.matrix.BitMatrix;
import org.briljantframework.matrix.Indexer;
import org.briljantframework.matrix.api.MatrixFactory;

/**
 * @author Isak Karlsson
 */
class BaseBitMatrix extends AbstractBitMatrix {

  private boolean[] storage;

  BaseBitMatrix(MatrixFactory bj, int rows, int cols) {
    super(bj, rows, cols);
    this.storage = new boolean[Math.multiplyExact(rows, cols)];
  }

  BaseBitMatrix(MatrixFactory bj, int size) {
    this(bj, new boolean[size]);
  }

  BaseBitMatrix(MatrixFactory bj, boolean[] values) {
    this(bj, values, values.length, 1);
  }

  BaseBitMatrix(MatrixFactory bj, boolean[] storage, int rows, int columns) {
    super(bj, rows, columns);
    Check.size(storage.length, Math.multiplyExact(rows, columns));
    this.storage = storage;
  }

  @Override
  public BitMatrix reshape(int rows, int columns) {
    Check.size(CHANGED_TOTAL_SIZE, Math.multiplyExact(rows, columns), this);
    return new BaseBitMatrix(getMatrixFactory(), storage, rows, columns);
  }

  public BitMatrix copy() {
    return new BaseBitMatrix(getMatrixFactory(), storage.clone(), rows(), columns());
  }

  @Override
  public void set(int i, int j, boolean value) {
    set(Indexer.columnMajor(0, i, j, rows(), columns()), value);
  }

  @Override
  public void set(int index, boolean value) {
    storage[index] = value;
  }

  @Override
  public boolean get(int i, int j) {
    return get(Indexer.columnMajor(0, i, j, rows(), columns()));
  }

  @Override
  public boolean get(int index) {
    return storage[index];
  }

  @Override
  public boolean isView() {
    return false;
  }
}

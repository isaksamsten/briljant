package org.briljantframework.matrix.base;

import org.briljantframework.Check;
import org.briljantframework.matrix.AbstractBitMatrix;
import org.briljantframework.matrix.BitMatrix;
import org.briljantframework.matrix.Indexer;
import org.briljantframework.matrix.Storage;
import org.briljantframework.matrix.api.MatrixFactory;

/**
 * @author Isak Karlsson
 */
class BaseBitMatrix extends AbstractBitMatrix {

  private Storage storage;

  BaseBitMatrix(MatrixFactory bj, int rows, int cols) {
    super(bj, rows, cols);
    this.storage = new BooleanStorage(new boolean[Math.multiplyExact(rows, cols)]);
  }

  BaseBitMatrix(MatrixFactory bj, int size) {
    this(bj, new BooleanStorage(size));
  }

  BaseBitMatrix(MatrixFactory bj, boolean[] values) {
    this(bj, new BooleanStorage(values), values.length, 1);
  }

  BaseBitMatrix(MatrixFactory bj, Storage storage, int rows, int columns) {
    super(bj, rows, columns);
    Check.size(storage.size(), Math.multiplyExact(rows, columns));
    this.storage = storage;
  }

  BaseBitMatrix(MatrixFactory bj, Storage storage) {
    super(bj, storage.size());
    this.storage = storage;
  }

  @Override
  public BitMatrix reshape(int rows, int columns) {
    Check.size(CHANGED_TOTAL_SIZE, Math.multiplyExact(rows, columns), this);
    return new BaseBitMatrix(getMatrixFactory(), storage, rows, columns);
  }

  @Override
  public BitMatrix newEmptyMatrix(int rows, int columns) {
    return new BaseBitMatrix(getMatrixFactory(), rows, columns);
  }

  public BitMatrix copy() {
    return new BaseBitMatrix(getMatrixFactory(), storage.copy(), rows(), columns());
  }

  @Override
  public void set(int i, int j, boolean value) {
    set(Indexer.columnMajor(i, j, rows(), columns()), value);
  }

  @Override
  public void set(int index, boolean value) {
    storage.setBoolean(index, value);
  }

  @Override
  public boolean get(int i, int j) {
    return get(Indexer.columnMajor(i, j, rows(), columns()));
  }

  @Override
  public boolean get(int index) {
    return storage.getBoolean(index);
  }

  @Override
  public boolean isView() {
    return false;
  }

  @Override
  public Storage getStorage() {
    return storage;
  }
}

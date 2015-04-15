package org.briljantframework.matrix.base;

import org.briljantframework.Check;
import org.briljantframework.matrix.AbstractIntMatrix;
import org.briljantframework.matrix.Indexer;
import org.briljantframework.matrix.IntMatrix;
import org.briljantframework.matrix.Storage;
import org.briljantframework.matrix.api.MatrixFactory;

/**
 * @author Isak Karlsson
 */
class BaseIntMatrix extends AbstractIntMatrix {

  private Storage storage;

  BaseIntMatrix(MatrixFactory bj, int size) {
    this(bj, new IntStorage(size), size);
  }

  BaseIntMatrix(MatrixFactory bj, int rows, int columns) {
    this(bj, IntStorage.withSize(Math.multiplyExact(rows, columns)), rows, columns);
  }

  BaseIntMatrix(MatrixFactory bj, Storage storage, int size) {
    super(bj, size);
    this.storage = storage;
  }

  BaseIntMatrix(MatrixFactory bj, Storage storage, int rows, int cols) {
    super(bj, rows, cols);
    this.storage = storage;
  }

  BaseIntMatrix(MatrixFactory bj, int[] values) {
    this(bj, new IntStorage(values), values.length);
  }

  BaseIntMatrix(MatrixFactory bj, Storage storage) {
    this(bj, storage, storage.size());
  }


  @Override
  public IntMatrix reshape(int rows, int columns) {
    Check.size(CHANGED_TOTAL_SIZE, Math.multiplyExact(rows, columns), this);
    return new BaseIntMatrix(getMatrixFactory(), getStorage(), rows, columns);
  }

  @Override
  public IntMatrix newEmptyMatrix(int rows, int columns) {
    return new BaseIntMatrix(getMatrixFactory(), rows, columns);
  }

  @Override
  public boolean isView() {
    return false;
  }

  @Override
  public Storage getStorage() {
    return storage;
  }

  @Override
  public int get(int i, int j) {
    return storage.getInt(Indexer.columnMajor(i, j, rows(), columns()));
  }

  @Override
  public int get(int index) {
    return storage.getInt(index);
  }

  @Override
  public void set(int index, int value) {
    storage.setInt(index, value);
  }

  @Override
  public void set(int i, int j, int value) {
    storage.setInt(Indexer.columnMajor(i, j, rows(), columns()), value);
  }

  @Override
  public IntMatrix copy() {
    return new BaseIntMatrix(getMatrixFactory(), getStorage().copy(), rows(), columns());
  }

}

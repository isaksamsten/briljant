package org.briljantframework.matrix.base;

import org.briljantframework.Check;
import org.briljantframework.matrix.AbstractLongMatrix;
import org.briljantframework.matrix.Indexer;
import org.briljantframework.matrix.LongMatrix;
import org.briljantframework.matrix.storage.LongStorage;
import org.briljantframework.matrix.storage.Storage;
import org.briljantframework.matrix.api.MatrixFactory;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Isak Karlsson
 */
class BaseLongMatrix extends AbstractLongMatrix {

  private Storage storage;

  BaseLongMatrix(MatrixFactory bj, int rows, int cols) {
    super(bj, rows, cols);
    this.storage = new LongStorage(new long[Math.multiplyExact(rows, cols)]);
  }

  BaseLongMatrix(MatrixFactory bj, int size) {
    this(bj, new LongStorage(new long[size]), size);
  }

  BaseLongMatrix(MatrixFactory bj, Storage storage, int size) {
    super(bj, size);
    this.storage = checkNotNull(storage);
  }

  BaseLongMatrix(MatrixFactory bj, Storage storage, int rows, int cols) {
    super(bj, rows, cols);
    this.storage = checkNotNull(storage);
  }

  public BaseLongMatrix(MatrixFactory bj, Storage storage) {
    super(bj, checkNotNull(storage).size());
    this.storage = storage;
  }

  @Override
  public LongMatrix reshape(int rows, int columns) {
    Check.size(CHANGED_TOTAL_SIZE, Math.multiplyExact(rows, columns), this);
    return new BaseLongMatrix(getMatrixFactory(), getStorage(), rows, columns);
  }

  @Override
  public boolean isView() {
    return false;
  }

  public Storage getStorage() {
    return storage;
  }

  @Override
  public LongMatrix newEmptyMatrix(int rows, int columns) {
    return new BaseLongMatrix(getMatrixFactory(), rows, columns);
  }

  @Override
  public long get(int i, int j) {
    return storage.getLong(Indexer.columnMajor(i, j, rows(), columns()));
  }

  @Override
  public long get(int index) {
    return storage.getLong(index);
  }

  @Override
  public void set(int index, long value) {
    storage.setLong(index, value);
  }

  @Override
  public void set(int i, int j, long value) {
    storage.setLong(Indexer.columnMajor(i, j, rows(), columns()), value);
  }

}

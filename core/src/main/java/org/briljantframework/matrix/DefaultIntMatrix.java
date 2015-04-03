package org.briljantframework.matrix;

import org.briljantframework.Check;
import org.briljantframework.matrix.storage.IntStorage;
import org.briljantframework.matrix.storage.Storage;

/**
 * @author Isak Karlsson
 */
public class DefaultIntMatrix extends AbstractIntMatrix {

  private final Storage storage;

  public DefaultIntMatrix(Storage storage, int size) {
    super(size);
    this.storage = storage;
  }

  public DefaultIntMatrix(Storage storage, int rows, int cols) {
    super(rows, cols);
    this.storage = storage;
  }

  public DefaultIntMatrix(Matrix matrix) {
    this(matrix.getStorage(), matrix.rows(), matrix.columns());
  }

  public DefaultIntMatrix(int[] values, int size) {
    this(new IntStorage(values), size);
  }

  public DefaultIntMatrix(int size) {
    this(IntStorage.withSize(size), size);
  }

  public DefaultIntMatrix(int rows, int columns) {
    this(IntStorage.withSize(Math.multiplyExact(rows, columns)), rows, columns);
  }

  public DefaultIntMatrix(int... values) {
    this(values, values.length);
  }

  public DefaultIntMatrix(Storage storage) {
    this(storage, storage.size());
  }


  @Override
  public IntMatrix reshape(int rows, int columns) {
    Check.size(CHANGED_TOTAL_SIZE, Math.multiplyExact(rows, columns), this);
    return new DefaultIntMatrix(getStorage(), rows, columns);
  }

  @Override
  public IntMatrix newEmptyMatrix(int rows, int columns) {
    return new DefaultIntMatrix(rows, columns);
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
    return getStorage().getInt(Indexer.columnMajor(i, j, rows(), columns()));
  }

  @Override
  public int get(int index) {
    return getStorage().getInt(index);
  }

  @Override
  public void set(int index, int value) {
    getStorage().setInt(index, value);
  }

  @Override
  public void set(int i, int j, int value) {
    getStorage().setInt(Indexer.columnMajor(i, j, rows(), columns()), value);
  }

  @Override
  public IntMatrix copy() {
    return new DefaultIntMatrix(getStorage().copy(), rows(), columns());
  }
}

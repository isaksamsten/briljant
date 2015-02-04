package org.briljantframework.matrix;

import java.util.Arrays;

import org.briljantframework.matrix.storage.BooleanStorage;
import org.briljantframework.matrix.storage.Storage;

/**
 * @author Isak Karlsson
 */
public class DefaultBitMatrix extends AbstractBitMatrix {
  private final Storage storage;

  public DefaultBitMatrix(int rows, int cols) {
    super(rows, cols);
    this.storage = new BooleanStorage(new boolean[Math.multiplyExact(rows, cols)]);
  }

  public DefaultBitMatrix(int size, boolean defaultValue) {
    this(size);
    Arrays.fill(getStorage().asBooleanArray(), defaultValue);
  }

  public DefaultBitMatrix(int rows, int columns, boolean defaultValue) {
    this(rows, columns);
    Arrays.fill(getStorage().asBooleanArray(), defaultValue);
  }

  public DefaultBitMatrix(boolean... values) {
    this(new BooleanStorage(values), values.length, 1);
  }

  public DefaultBitMatrix(Storage storage, int rows, int columns) {
    super(rows, columns);
    this.storage = storage;
  }

  public DefaultBitMatrix(Matrix matrix) {
    this(matrix.getStorage(), matrix.rows(), matrix.columns());
  }

  public DefaultBitMatrix(Storage storage) {
    this(storage, storage.size());
  }

  public DefaultBitMatrix(Storage storage, int size) {
    super(size);
    this.storage = storage;
  }

  public DefaultBitMatrix(int size) {
    this(BooleanStorage.withSize(size), size);

  }

  @Override
  public void set(int i, int j, boolean value) {
    set(Indexer.columnMajor(i, j, rows(), columns()), value);
  }

  @Override
  public void set(int index, boolean value) {
    getStorage().setBoolean(index, value);
  }

  @Override
  public boolean get(int i, int j) {
    return get(Indexer.columnMajor(i, j, rows(), columns()));
  }

  @Override
  public boolean get(int index) {
    return getStorage().getBoolean(index);
  }

  @Override
  public BitMatrix reshape(int rows, int columns) {
    Check.size(CHANGED_TOTAL_SIZE, Math.multiplyExact(rows, columns), this);
    return new DefaultBitMatrix(storage, rows, columns);
  }

  @Override
  public boolean isView() {
    return false;
  }

  @Override
  public BitMatrix newEmptyMatrix(int rows, int columns) {
    return new DefaultBitMatrix(rows, columns);
  }

  public BitMatrix copy() {
    return new DefaultBitMatrix(storage.copy(), rows(), columns());
  }

  @Override
  public Storage getStorage() {
    return storage;
  }
}

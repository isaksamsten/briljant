package org.briljantframework.matrix;

import org.briljantframework.matrix.storage.LongStorage;
import org.briljantframework.matrix.storage.Storage;

/**
 * Created by isak on 1/30/15.
 */
public class DefaultLongMatrix extends AbstractLongMatrix {
  private final Storage storage;

  public DefaultLongMatrix(Storage storage, int size) {
    super(size);
    this.storage = storage;
  }

  public DefaultLongMatrix(Storage storage, int rows, int cols) {
    super(rows, cols);
    this.storage = storage;
  }

  public DefaultLongMatrix(int rows, int cols) {
    super(rows, cols);
    this.storage = new LongStorage(new long[Math.multiplyExact(rows, cols)]);
  }

  public DefaultLongMatrix(Matrix matrix) {
    this(matrix.getStorage(), matrix.rows(), matrix.columns());
  }

  public DefaultLongMatrix(int size) {
    this(new LongStorage(new long[size]), size);
  }

  @Override
  public LongMatrix reshape(int rows, int columns) {
    return null;
  }

  @Override
  public boolean isView() {
    return false;
  }

  @Override
  public Storage getStorage() {
    return null;
  }

  @Override
  public LongMatrix newEmptyMatrix(int rows, int columns) {
    return null;
  }
}

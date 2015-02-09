package org.briljantframework.matrix;

import static com.google.common.base.Preconditions.checkNotNull;

import org.briljantframework.matrix.storage.LongStorage;
import org.briljantframework.matrix.storage.Storage;

/**
 * @author Isak Karlsson
 */
public class DefaultLongMatrix extends AbstractLongMatrix {
  private final Storage storage;

  public DefaultLongMatrix(Storage storage, int size) {
    super(size);
    this.storage = checkNotNull(storage);
  }

  public DefaultLongMatrix(Storage storage, int rows, int cols) {
    super(rows, cols);
    this.storage = checkNotNull(storage);
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

  public DefaultLongMatrix(Storage storage) {
    super(checkNotNull(storage).size());
    this.storage = storage;
  }

  @Override
  public LongMatrix reshape(int rows, int columns) {
    Check.size(CHANGED_TOTAL_SIZE, Math.multiplyExact(rows, columns), this);
    return new DefaultLongMatrix(getStorage(), rows, columns);
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
  public LongMatrix newEmptyMatrix(int rows, int columns) {
    return new DefaultLongMatrix(rows, columns);
  }
}

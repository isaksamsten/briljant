package org.briljantframework.matrix;

/**
 * @author Isak Karlsson
 */
public abstract class AbstractStorage implements Storage {

  private final int size;

  protected AbstractStorage(int size) {
    this.size = size;
  }

  @Override
  public boolean getBoolean(int index) {
    return getInt(index) == 1;
  }

  @Override
  public void setBoolean(int index, boolean value) {
    setInt(index, value ? 1 : 0);
  }

  @Override
  public int size() {
    return size;
  }

  @Override
  public String toString() {
    return String.format("Storage for %s of %d values", getNativeType(), size());
  }
}

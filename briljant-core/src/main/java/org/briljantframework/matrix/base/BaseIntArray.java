package org.briljantframework.matrix.base;

import org.briljantframework.matrix.AbstractIntArray;
import org.briljantframework.matrix.IntArray;
import org.briljantframework.matrix.api.ArrayFactory;

import java.util.Objects;

/**
 * @author Isak Karlsson
 */
class BaseIntArray extends AbstractIntArray {

  private final int[] data;

  BaseIntArray(ArrayFactory bj, int size) {
    super(bj, size);
    this.data = new int[size];
  }

  BaseIntArray(ArrayFactory bj, boolean ignore, int[] values) {
    super(bj, Objects.requireNonNull(values).length);
    this.data = values;
  }

  BaseIntArray(ArrayFactory bj, int... shape) {
    super(bj, shape);
    this.data = new int[size()];
  }

  BaseIntArray(ArrayFactory bj, int offset, int[] shape, int[] stride, int[] data) {
    super(bj, offset, shape, stride);
    this.data = data;
  }

  @Override
  public IntArray newEmptyArray(int... shape) {
    return new BaseIntArray(getMatrixFactory(), shape);
  }

  @Override
  public boolean isView() {
    return false;
  }

  @Override
  public int getElement(int index) {
    return data[index];
  }

  @Override
  public void setElement(int index, int value) {
    data[index] = value;
  }

  @Override
  protected IntArray makeView(int offset, int[] shape, int[] stride) {
    return new BaseIntArray(
        getMatrixFactory(),
        offset,
        shape,
        stride,
        data
    );
  }

  @Override
  public int[] data() {
    return data;
  }
}

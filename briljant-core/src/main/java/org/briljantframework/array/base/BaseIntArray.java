package org.briljantframework.array.base;

import org.briljantframework.array.AbstractIntArray;
import org.briljantframework.array.IntArray;
import org.briljantframework.array.api.ArrayFactory;

/**
 * @author Isak Karlsson
 */
class BaseIntArray extends AbstractIntArray {

  private final int[] data;

  BaseIntArray(ArrayFactory bj, int size) {
    super(bj, new int[]{size});
    this.data = new int[size];
  }

  BaseIntArray(ArrayFactory bj, int[] shape) {
    super(bj, shape);
    this.data = new int[size()];
  }

  BaseIntArray(ArrayFactory bj, int offset, int[] shape, int[] stride, int majorStride,
               int[] data) {
    super(bj, offset, shape, stride, majorStride);
    this.data = data;
  }

  BaseIntArray(ArrayFactory bj, boolean ignore, int[] data) {
    super(bj, new int[]{data.length});
    this.data = data;
  }

  @Override
  public IntArray newEmptyArray(int... shape) {
    return new BaseIntArray(getMatrixFactory(), shape);
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
  protected IntArray makeView(int offset, int[] shape, int[] stride, int majorStride) {
    return new BaseIntArray(
        getMatrixFactory(),
        offset,
        shape,
        stride,
        majorStride,
        data
    );
  }

  @Override
  protected int elementSize() {
    return data.length;
  }

  @Override
  public int[] data() {
    return data;
  }
}

package org.briljantframework.array.base;

import org.briljantframework.array.AbstractLongArray;
import org.briljantframework.array.LongArray;
import org.briljantframework.array.api.ArrayFactory;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Isak Karlsson
 */
class BaseLongArray extends AbstractLongArray {

  private long[] data;

  BaseLongArray(ArrayFactory bj, int[] shape) {
    super(bj, shape);
    this.data = new long[size()];
  }

  BaseLongArray(ArrayFactory bj, long[] data) {
    super(bj, new int[]{checkNotNull(data).length});
    this.data = data;
  }

  BaseLongArray(ArrayFactory bj, int offset, int[] shape, int[] stride, int majorStride,
                long[] data) {
    super(bj, offset, shape, stride, majorStride);
    this.data = data;
  }

  @Override
  public LongArray newEmptyArray(int... shape) {
    return new BaseLongArray(getMatrixFactory(), shape);
  }

  @Override
  protected LongArray makeView(int offset, int[] shape, int[] stride, int majorStride) {
    return new BaseLongArray(getMatrixFactory(), offset, shape, stride, majorStride, data);
  }

  @Override
  protected int elementSize() {
    return data.length;
  }

  @Override
  public long getElement(int index) {
    return data[index];
  }

  @Override
  public void setElement(int index, long value) {
    data[index] = value;
  }
}
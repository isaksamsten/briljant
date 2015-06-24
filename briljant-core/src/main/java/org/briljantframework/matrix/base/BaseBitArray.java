package org.briljantframework.matrix.base;

import org.briljantframework.matrix.AbstractBitArray;
import org.briljantframework.matrix.BitArray;
import org.briljantframework.matrix.api.ArrayFactory;

import java.util.Objects;

/**
 * @author Isak Karlsson
 */
class BaseBitArray extends AbstractBitArray {

  private final boolean[] data;

  BaseBitArray(ArrayFactory bj, int size) {
    super(bj, size);
    data = new boolean[size];
  }

  BaseBitArray(ArrayFactory bj, boolean[] data) {
    super(bj, Objects.requireNonNull(data).length);
    this.data = data;
  }

  public BaseBitArray(ArrayFactory bj, int[] shape) {
    super(bj, shape);
    this.data = new boolean[size()];
  }

  public BaseBitArray(ArrayFactory bj, int offset, int[] shape, int[] stride, int majorStride,
                      boolean[] data) {
    super(bj, offset, shape, stride, majorStride);
    this.data = data;
  }

  @Override
  protected BitArray makeView(int offset, int[] shape, int[] stride, int majorStride) {
    return new BaseBitArray(getMatrixFactory(), offset, shape, stride, majorStride, data);
  }

  @Override
  protected int elementSize() {
    return data.length;
  }

  @Override
  public void setElement(int index, boolean value) {
    data[index] = value;
  }

  @Override
  public boolean getElement(int index) {
    return data[index];
  }

  @Override
  public BitArray newEmptyArray(int... shape) {
    return new BaseBitArray(getMatrixFactory(), shape);
  }
}

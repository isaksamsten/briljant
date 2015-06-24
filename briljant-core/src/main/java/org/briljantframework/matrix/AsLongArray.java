package org.briljantframework.matrix;

import org.briljantframework.matrix.api.ArrayFactory;

abstract class AsLongArray extends AbstractLongArray {

  AsLongArray(ArrayFactory bj, int offset, int[] shape, int[] stride, int majorStride) {
    super(bj, offset, shape, stride, majorStride);
  }

  @Override
  public LongArray newEmptyArray(int... shape) {
    return getMatrixFactory().longArray(shape);
  }

  @Override
  protected LongArray makeView(int offset, int[] shape, int[] stride, int majorStride) {
    return new AsLongArray(getMatrixFactory(), offset, shape, stride, majorStride) {
      @Override
      protected void setElement(int i, long value) {
        AsLongArray.this.setElement(i, value);
      }

      @Override
      protected long getElement(int i) {
        return AsLongArray.this.getElement(i);
      }

      @Override
      protected int elementSize() {
        return AsLongArray.this.elementSize();
      }
    };
  }

  @Override
  public boolean isView() {
    return true;
  }
}
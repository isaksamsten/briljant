package org.briljantframework.matrix;

import org.briljantframework.matrix.api.ArrayFactory;

abstract class AsLongArray extends AbstractLongArray {

  AsLongArray(ArrayFactory bj, int offset, int[] shape, int[] stride) {
    super(bj, offset, shape, stride);
  }

  @Override
  public LongArray newEmptyArray(int... shape) {
    return getMatrixFactory().longArray(shape);
  }

  @Override
  protected LongArray makeView(int offset, int[] shape, int[] stride) {
    return new AsLongArray(getMatrixFactory(), offset, shape, stride) {
      @Override
      protected void setElement(int i, long value) {
        AsLongArray.this.setElement(i, value);
      }

      @Override
      protected long getElement(int i) {
        return AsLongArray.this.getElement(i);
      }
    };
  }

  @Override
  public boolean isView() {
    return true;
  }
}
package org.briljantframework.matrix;

import org.briljantframework.matrix.api.ArrayFactory;

/**
 * @author Isak Karlsson
 */
abstract class AsIntArray extends AbstractIntArray {

  AsIntArray(ArrayFactory bj, int offset, int[] shape, int[] stride) {
    super(bj, offset, shape, stride);
  }

  @Override
  public IntArray newEmptyArray(int... shape) {
    return getMatrixFactory().intArray(shape);
  }

  @Override
  protected IntArray makeView(int offset, int[] shape, int[] stride) {
    return new AsIntArray(getMatrixFactory(), offset, shape, stride) {
      @Override
      protected void setElement(int i, int value) {
        AsIntArray.this.setElement(i, value);
      }

      @Override
      protected int getElement(int i) {
        return AsIntArray.this.getElement(i);
      }
    };
  }

  @Override
  public boolean isView() {
    return true;
  }
}

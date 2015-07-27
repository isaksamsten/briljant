package org.briljantframework.array;

import org.briljantframework.array.api.ArrayFactory;

/**
 * @author Isak Karlsson
 */
abstract class AsIntArray extends AbstractIntArray {

  AsIntArray(ArrayFactory bj, int offset, int[] shape, int[] stride, int majorStride) {
    super(bj, offset, shape, stride, majorStride);
  }

  @Override
  public IntArray newEmptyArray(int... shape) {
    return getMatrixFactory().intArray(shape);
  }

  @Override
  protected IntArray makeView(int offset, int[] shape, int[] stride, int majorStride) {
    return new AsIntArray(getMatrixFactory(), offset, shape, stride, majorStride) {
      @Override
      protected void setElement(int i, int value) {
        AsIntArray.this.setElement(i, value);
      }

      @Override
      protected int getElement(int i) {
        return AsIntArray.this.getElement(i);
      }

      @Override
      protected int elementSize() {
        return AsIntArray.this.elementSize();
      }
    };
  }

  @Override
  public int[] data() {
    int[] array = new int[elementSize()];
    for (int i = 0; i < size(); i++) {
      array[i] = get(i);
    }
    return array;
  }

  @Override
  public boolean isView() {
    return true;
  }
}

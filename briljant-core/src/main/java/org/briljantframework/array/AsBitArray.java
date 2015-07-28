package org.briljantframework.array;

import org.briljantframework.array.api.ArrayFactory;

/**
 * @author Isak Karlsson
 */
abstract class AsBitArray extends AbstractBitArray {

  public AsBitArray(ArrayFactory bj, int offset, int[] shape, int[] stride, int majorStride) {
    super(bj, offset, shape, stride, majorStride);
  }

  @Override
  public BitArray newEmptyArray(int... shape) {
    return getMatrixFactory().booleanArray(shape);
  }

  @Override
  public BitArray asView(int offset, int[] shape, int[] stride, int majorStride) {
    return new AsBitArray(getMatrixFactory(), offset, shape, stride, majorStride) {
      @Override
      protected void setElement(int i, boolean value) {
        AsBitArray.this.setElement(i, value);
      }

      @Override
      protected boolean getElement(int i) {
        return AsBitArray.this.getElement(i);
      }

      @Override
      protected int elementSize() {
        return AsBitArray.this.elementSize();
      }
    };
  }

  @Override
  public boolean isView() {
    return true;
  }
}

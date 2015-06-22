package org.briljantframework.matrix;

import org.briljantframework.matrix.api.ArrayFactory;

/**
 * @author Isak Karlsson
 */
abstract class AsBitArray extends AbstractBitArray {

  public AsBitArray(ArrayFactory bj, int offset, int[] shape, int[] stride) {
    super(bj, offset, shape, stride);
  }

  @Override
  public BitArray newEmptyArray(int... shape) {
    return getMatrixFactory().booleanArray(shape);
  }

  @Override
  protected BitArray makeView(int offset, int[] shape, int[] stride) {
    return new AsBitArray(getMatrixFactory(), offset, shape, stride) {
      @Override
      protected void setElement(int i, boolean value) {
        AsBitArray.this.setElement(i, value);
      }

      @Override
      protected boolean getElement(int i) {
        return AsBitArray.this.getElement(i);
      }
    };
  }

  @Override
  public boolean isView() {
    return true;
  }
}

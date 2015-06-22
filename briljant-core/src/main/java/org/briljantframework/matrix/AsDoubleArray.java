package org.briljantframework.matrix;

import org.briljantframework.matrix.api.ArrayFactory;

abstract class AsDoubleArray extends AbstractDoubleArray {

  public AsDoubleArray(ArrayFactory bj, int offset, int[] shape, int[] stride) {
    super(bj, offset, shape, stride);
  }

  @Override
  public DoubleArray newEmptyArray(int... shape) {
    return getMatrixFactory().doubleArray(shape);
  }

  @Override
  protected DoubleArray makeView(int offset, int[] shape, int[] stride) {
    return new AsDoubleArray(getMatrixFactory(), offset, shape, stride) {
      @Override
      protected void setElement(int i, double value) {
        AsDoubleArray.this.setElement(i, value);
      }

      @Override
      protected double getElement(int i) {
        return AsDoubleArray.this.getElement(i);
      }
    };
  }

  @Override
  public final boolean isView() {
    return true;
  }
}
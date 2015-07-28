package org.briljantframework.array;

import org.briljantframework.array.api.ArrayFactory;

abstract class AsDoubleArray extends AbstractDoubleArray {

  public AsDoubleArray(ArrayFactory bj, int offset, int[] shape, int[] stride, int majorStride) {
    super(bj, offset, shape, stride, majorStride);
  }

  @Override
  public DoubleArray newEmptyArray(int... shape) {
    return getMatrixFactory().doubleArray(shape);
  }

  @Override
  public DoubleArray asView(int offset, int[] shape, int[] stride, int majorStride) {
    return new AsDoubleArray(getMatrixFactory(), offset, shape, stride, majorStride) {
      @Override
      protected void setElement(int i, double value) {
        AsDoubleArray.this.setElement(i, value);
      }

      @Override
      protected double getElement(int i) {
        return AsDoubleArray.this.getElement(i);
      }

      @Override
      protected int elementSize() {
        return AsDoubleArray.this.elementSize();
      }
    };
  }

  @Override
  public double[] data() {
    double[] v = new double[elementSize()];
    for (int i = 0; i < v.length; i++) {
      v[i] = getElement(i);
    }
    return v;
  }

  @Override
  public final boolean isView() {
    return true;
  }
}
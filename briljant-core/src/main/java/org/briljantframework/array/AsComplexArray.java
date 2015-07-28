package org.briljantframework.array;

import org.briljantframework.complex.Complex;
import org.briljantframework.array.api.ArrayFactory;

/**
 * @author Isak Karlsson
 */
abstract class AsComplexArray extends AbstractComplexArray {

  public AsComplexArray(ArrayFactory bj, int offset, int[] shape, int[] stride, int majorStride) {
    super(bj, offset, shape, stride, majorStride);
  }

  @Override
  public ComplexArray newEmptyArray(int... shape) {
    return getArrayFactory().complexArray(shape);
  }

  @Override
  public ComplexArray asView(int offset, int[] shape, int[] stride, int majorStride) {
    return new AsComplexArray(getArrayFactory(), offset, shape, stride, majorStride) {
      @Override
      protected void setElement(int i, Complex value) {
        AsComplexArray.this.setElement(i, value);
      }

      @Override
      protected Complex getElement(int i) {
        return AsComplexArray.this.getElement(i);
      }

      @Override
      protected int elementSize() {
        return AsComplexArray.this.elementSize();
      }
    };
  }

  @Override
  public boolean isView() {
    return true;
  }
}

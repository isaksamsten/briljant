package org.briljantframework.matrix;

import org.briljantframework.complex.Complex;
import org.briljantframework.matrix.api.ArrayFactory;

/**
 * @author Isak Karlsson
 */
abstract class AsComplexArray extends AbstractComplexArray {

  public AsComplexArray(ArrayFactory bj, int offset, int[] shape, int[] stride, int majorStride) {
    super(bj, offset, shape, stride, majorStride);
  }

  @Override
  public ComplexArray newEmptyArray(int... shape) {
    return getMatrixFactory().complexArray(shape);
  }

  @Override
  protected ComplexArray makeView(int offset, int[] shape, int[] stride, int majorStride) {
    return new AsComplexArray(getMatrixFactory(), offset, shape, stride, majorStride) {
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

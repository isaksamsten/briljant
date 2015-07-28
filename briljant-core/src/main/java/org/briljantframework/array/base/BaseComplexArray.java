package org.briljantframework.array.base;

import org.briljantframework.complex.Complex;
import org.briljantframework.array.AbstractComplexArray;
import org.briljantframework.array.ComplexArray;
import org.briljantframework.array.api.ArrayFactory;

import java.util.Objects;

/**
 * @author Isak Karlsson
 */
class BaseComplexArray extends AbstractComplexArray {

  private Complex[] data;
  private Complex defaultValue = Complex.ZERO;

  BaseComplexArray(ArrayFactory bj, int size) {
    super(bj, size);
    this.data = new Complex[size];
  }

  BaseComplexArray(ArrayFactory bj, Complex[] data) {
    super(bj, Objects.requireNonNull(data).length);
    this.data = data;
  }

  BaseComplexArray(ArrayFactory bj, int[] shape) {
    super(bj, shape);
    this.data = new Complex[size()];
  }

  BaseComplexArray(ArrayFactory bj,
                   int offset,
                   int[] shape,
                   int[] stride,
                   int majorStride,
                   Complex[] data) {
    super(bj, offset, shape, stride, majorStride);
    this.data = data;
  }

  @Override
  public ComplexArray newEmptyArray(int... shape) {
    return new BaseComplexArray(getMatrixFactory(), shape);
  }

  @Override
  public ComplexArray asView(int offset, int[] shape, int[] stride, int majorStride) {
    return new BaseComplexArray(getMatrixFactory(), offset, shape, stride, majorStride, data);
  }

  @Override
  protected int elementSize() {
    return data.length;
  }

  @Override
  protected void setElement(int i, Complex value) {
    data[i] = value;
  }

  @Override
  protected Complex getElement(int i) {
    Complex r = data[i];
    if (r == null) {
      return defaultValue;
    } else {
      return r;
    }
  }
}

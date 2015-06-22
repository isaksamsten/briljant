package org.briljantframework.matrix.base;

import org.briljantframework.complex.Complex;
import org.briljantframework.matrix.AbstractComplexArray;
import org.briljantframework.matrix.ComplexArray;
import org.briljantframework.matrix.api.ArrayFactory;

import java.util.Objects;

/**
 * Created by Isak Karlsson on 07/01/15.
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

  BaseComplexArray(ArrayFactory bj, int... shape) {
    super(bj, shape);
    this.data = new Complex[size()];
  }

  BaseComplexArray(ArrayFactory bj,
                   int offset,
                   int[] shape,
                   int[] stride,
                   Complex[] data) {
    super(bj, offset, shape, stride);
    this.data = data;
  }

  @Override
  public ComplexArray newEmptyArray(int... shape) {
    return new BaseComplexArray(getMatrixFactory(), shape);
  }

  @Override
  protected ComplexArray makeView(int offset, int[] shape, int[] stride) {
    return new BaseComplexArray(getMatrixFactory(), offset, shape, stride, data);
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

  @Override
  public boolean isView() {
    return false;
  }
}

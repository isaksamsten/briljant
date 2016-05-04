/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Isak Karlsson
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.briljantframework.array.base;

import java.util.Objects;

import org.apache.commons.math3.complex.Complex;
import org.briljantframework.array.AbstractComplexArray;
import org.briljantframework.array.ComplexArray;
import org.briljantframework.array.api.ArrayBackend;

/**
 * @author Isak Karlsson
 */
class BaseComplexArray extends AbstractComplexArray {

  private Complex[] data;
  private Complex defaultValue = Complex.ZERO;

  BaseComplexArray(ArrayBackend bj, int size) {
    super(bj, size);
    this.data = new Complex[size];
  }

  BaseComplexArray(ArrayBackend bj, Complex[] data) {
    super(bj, Objects.requireNonNull(data).length);
    this.data = data;
  }

  BaseComplexArray(ArrayBackend bj, int[] shape) {
    super(bj, shape);
    this.data = new Complex[size()];
  }

  private BaseComplexArray(ArrayBackend bj, int offset, int[] shape, int[] stride, Complex[] data) {
    super(bj, offset, shape, stride);
    this.data = data;
  }

  @Override
  public ComplexArray asView(int offset, int[] shape, int[] stride) {
    return new BaseComplexArray(getArrayBackend(), offset, shape, stride, data);
  }

  @Override
  public ComplexArray newEmptyArray(int... shape) {
    return new BaseComplexArray(getArrayBackend(), shape);
  }

  @Override
  protected int elementSize() {
    return data.length;
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
  protected void setElement(int i, Complex value) {
    data[i] = value;
  }
}

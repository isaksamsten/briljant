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
package org.briljantframework.array.netlib;

import java.util.Arrays;
import java.util.Objects;

import net.mintern.primitive.Primitive;
import net.mintern.primitive.comparators.DoubleComparator;

import org.briljantframework.array.AbstractDoubleArray;
import org.briljantframework.array.DoubleArray;
import org.briljantframework.array.api.ArrayBackend;

import com.github.fommil.netlib.BLAS;

/**
 * @author Isak Karlsson
 */
class NetlibDoubleArray extends AbstractDoubleArray {

  private static BLAS blas = BLAS.getInstance();
  private final double[] data;

  NetlibDoubleArray(ArrayBackend backend, int size) {
    super(backend, new int[] {size});
    data = new double[size];
  }

  NetlibDoubleArray(ArrayBackend bj, double[] data) {
    super(bj, new int[] {Objects.requireNonNull(data).length});
    this.data = data;
  }

  NetlibDoubleArray(ArrayBackend bj, int[] shape) {
    super(bj, shape);
    this.data = new double[size()];
  }

  private NetlibDoubleArray(ArrayBackend bj, int offset, int[] shape, int[] stride, int majorStride,
      double[] data) {
    super(bj, offset, shape, stride, majorStride);
    this.data = data;
  }

  public NetlibDoubleArray(ArrayBackend bj, double[] data, int rows, int columns) {
    super(bj, new int[] {rows, columns});
    this.data = data;
  }

  @Override
  public DoubleArray asView(int offset, int[] shape, int[] stride) {
    return new NetlibDoubleArray(getArrayBackend(), offset, shape, stride, majorStride, data);
  }

  @Override
  public DoubleArray newEmptyArray(int... shape) {
    return new NetlibDoubleArray(getArrayBackend(), shape);
  }

  @Override
  protected int elementSize() {
    return data.length;
  }

  @Override
  public DoubleArray copy() {
    // This will speed up copying of
    if (dims() == 1 && stride(0) == 1 && getOffset() == 0) {
      return new NetlibDoubleArray(getArrayBackend(), Arrays.copyOf(data, size()));
    }
    return super.copy();
  }

  @Override
  public void sort(DoubleComparator cmp) {
    if (!isView() && isVector() && stride(0) == 1) {
      Primitive.sort(data(), getOffset(), size(), cmp);
    } else {
      super.sort(cmp);
    }
  }

  @Override
  public void timesAssign(double scalar) {
    getArrayBackend().getArrayRoutines().scal(scalar, this);
  }

  @Override
  public void plusAssign(DoubleArray other) {
    getArrayBackend().getArrayRoutines().axpy(1, other, this);
  }

  @Override
  protected double getElement(int i) {
    return data[i];
  }

  @Override
  protected void setElement(int i, double value) {
    data[i] = value;
  }

  @Override
  public double[] data() {
    return data;
  }
}

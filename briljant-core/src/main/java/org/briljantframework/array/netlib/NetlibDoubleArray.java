/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Isak Karlsson
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.briljantframework.array.netlib;

import com.github.fommil.netlib.BLAS;

import org.briljantframework.Check;
import org.briljantframework.array.AbstractDoubleArray;
import org.briljantframework.array.DoubleArray;
import org.briljantframework.array.Op;
import org.briljantframework.array.api.ArrayFactory;
import org.briljantframework.exceptions.NonConformantException;

import java.util.Objects;

/**
 * @author Isak Karlsson
 */
class NetlibDoubleArray extends AbstractDoubleArray {

  private static BLAS blas = BLAS.getInstance();
  private final double[] data;

  NetlibDoubleArray(ArrayFactory bj, int size) {
    super(bj, new int[]{size});
    data = new double[size];
  }

  NetlibDoubleArray(ArrayFactory bj, double[] data) {
    super(bj, new int[]{Objects.requireNonNull(data).length});
    this.data = data;
  }

  public NetlibDoubleArray(ArrayFactory bj, int[] shape) {
    super(bj, shape);
    this.data = new double[size()];
  }

  public NetlibDoubleArray(ArrayFactory bj, int offset, int[] shape, int[] stride, int majorStride,
                           double[] data) {
    super(bj, offset, shape, stride, majorStride);
    this.data = data;
  }

  public NetlibDoubleArray(ArrayFactory bj, double[] data, int rows, int columns) {
    super(bj, new int[]{rows, columns});
    this.data = data;
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
  public DoubleArray newEmptyArray(int... shape) {
    return new NetlibDoubleArray(getArrayFactory(), shape);
  }

  @Override
  public DoubleArray asView(int offset, int[] shape, int[] stride, int majorStride) {
    return new NetlibDoubleArray(
        getArrayFactory(),
        offset,
        shape,
        stride,
        majorStride,
        data
    );
  }

  @Override
  protected int elementSize() {
    return data.length;
  }

  @Override
  public DoubleArray mmul(double alpha, Op transA, DoubleArray b, Op transB) {
    Check.argument(b.isMatrix(), "require 2d-array argument");
    Check.state(isMatrix(), "require 2d-array");

    // Copy this or the argument if needed
    DoubleArray self = isContiguous() && stride(0) == 1 ? this : this.copy();
    b = b.isContiguous() && b.stride(0) == 1 ? b : b.copy();

    int m = self.size(transA == Op.KEEP ? 0 : 1);
    int bm = b.size(transB == Op.KEEP ? 0 : 1);
    int n = b.size(transB == Op.KEEP ? 1 : 0);
    int k = self.size(transA == Op.KEEP ? 1 : 0);

    if (m == 0 || k == 0 || n == 0 || bm == 0) {
      throw new IllegalArgumentException("empty result");
    }
    if (b.size(transB == Op.KEEP ? 0 : 1) != self.size(transA == Op.KEEP ? 1 : 0)) {
      throw new NonConformantException(this, b);
    }

    double[] ca = new double[m * n];
    blas.dgemm(
        transA.asString(),
        transB.asString(),
        m,
        n,
        k,
        alpha,
        self.data(),
        self.getOffset(),
        Math.max(1, self.stride(1)),
        b.data(),
        b.getOffset(),
        Math.max(1, b.stride(1)),
        1.0,
        ca,
        0,
        Math.max(1, n)
    );
    return new NetlibDoubleArray(getArrayFactory(), ca, m, n);
  }

  @Override
  public double[] data() {
    return data;
  }
}

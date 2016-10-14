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

import org.briljantframework.array.AbstractArray;
import org.briljantframework.array.Array;
import org.briljantframework.array.ShapeUtils;
import org.briljantframework.array.api.ArrayBackend;

import java.util.Arrays;

/**
 * @author Isak Karlsson
 */
class NetlibReferenceArray<T> extends AbstractArray<T> {

  private final T[] data;

  NetlibReferenceArray(ArrayBackend bj, T[] data) {
    this(bj, new int[] {data.length}, data);
  }

  private NetlibReferenceArray(ArrayBackend bj, int[] shape, T[] data) {
    super(bj, shape);
    this.data = data;
  }

  private NetlibReferenceArray(ArrayBackend bj, int offset, int[] shape, int[] stride, T[] data) {
    super(bj, offset, shape, stride);
    this.data = data;
  }

  @SuppressWarnings("unchecked")
  NetlibReferenceArray(ArrayBackend bj, int[] shape) {
    super(bj, shape);
    this.data = (T[]) new Object[ShapeUtils.size(shape)];
  }

  @Override
  protected int elementSize() {
    return data.length;
  }

  @Override
  public Array<T> asView(int offset, int[] shape, int[] stride) {
    return new NetlibReferenceArray<>(getArrayBackend(), offset, shape, stride, data);
  }

  @Override
  public Array<T> newEmptyArray(int... shape) {
    @SuppressWarnings("unchecked")
    T[] data = (T[]) new Object[ShapeUtils.size(shape)];
    return new NetlibReferenceArray<>(getArrayBackend(), shape, data);
  }

  @Override
  @SuppressWarnings("unchecked")
  public <E> E[] toArray(E[] a) {
    if (a.length < size())
      return (E[]) Arrays.copyOf(data, size(), a.getClass());
    System.arraycopy(data, 0, a, 0, size());
    if (a.length > size())
      a[size()] = null;
    return a;
  }

  @Override
  public Object[] toArray() {
    return Arrays.copyOf(data, size());
  }

  @Override
  protected void setElement(int i, T value) {
    data[i] = value;
  }

  @Override
  protected T getElement(int i) {
    return data[i];
  }
}

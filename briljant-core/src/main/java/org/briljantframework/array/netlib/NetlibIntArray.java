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

import net.mintern.primitive.Primitive;
import net.mintern.primitive.comparators.IntComparator;

import org.briljantframework.array.AbstractIntArray;
import org.briljantframework.array.IntArray;
import org.briljantframework.array.api.ArrayBackend;

/**
 * @author Isak Karlsson
 */
class NetlibIntArray extends AbstractIntArray {

  private final int[] data;

  NetlibIntArray(ArrayBackend bj, int size) {
    super(bj, new int[] {size});
    this.data = new int[size];
  }

  NetlibIntArray(ArrayBackend bj, int[] shape) {
    super(bj, shape);
    this.data = new int[size()];
  }

  private NetlibIntArray(ArrayBackend bj, int offset, int[] shape, int[] stride, int[] data) {
    super(bj, offset, shape, stride);
    this.data = data;
  }

  NetlibIntArray(ArrayBackend bj, boolean ignore, int[] data) {
    super(bj, new int[] {data.length});
    this.data = data;
  }

  @Override
  public void sort(IntComparator cmp) {
    if (!isView() && isVector() && stride(0) == 1) {
      Primitive.sort(data, getOffset(), size(), cmp);
    } else {
      super.sort(cmp);
    }
  }

  @Override
  public int getElement(int index) {
    return data[index];
  }

  @Override
  public void setElement(int index, int value) {
    data[index] = value;
  }

  @Override
  public IntArray asView(int offset, int[] shape, int[] stride) {
    return new NetlibIntArray(getArrayBackend(), offset, shape, stride, data);
  }

  @Override
  public IntArray newEmptyArray(int... shape) {
    return new NetlibIntArray(getArrayBackend(), shape);
  }

  @Override
  protected int elementSize() {
    return data.length;
  }

  int[] getBackingArray() {
    return data;
  }
}

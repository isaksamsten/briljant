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

import java.util.Objects;

import org.briljantframework.array.AbstractBooleanArray;
import org.briljantframework.array.BooleanArray;
import org.briljantframework.array.api.ArrayBackend;

/**
 * @author Isak Karlsson
 */
class NetlibBooleanArray extends AbstractBooleanArray {

  private final boolean[] data;

  NetlibBooleanArray(ArrayBackend backend, int size) {
    super(backend, size);
    data = new boolean[size];
  }

  NetlibBooleanArray(ArrayBackend backend, boolean[] data) {
    super(backend, Objects.requireNonNull(data).length);
    this.data = data;
  }

  NetlibBooleanArray(ArrayBackend bj, int[] shape) {
    super(bj, shape);
    this.data = new boolean[size()];
  }

  private NetlibBooleanArray(ArrayBackend bj, int offset, int[] shape, int[] stride, boolean[] data) {
    super(bj, offset, shape, stride);
    this.data = data;
  }

  @Override
  public BooleanArray asView(int offset, int[] shape, int[] stride) {
    return new NetlibBooleanArray(getArrayBackend(), offset, shape, stride, data);
  }

  @Override
  public BooleanArray newEmptyArray(int... shape) {
    return new NetlibBooleanArray(getArrayBackend(), shape);
  }

  @Override
  protected int elementSize() {
    return data.length;
  }

  @Override
  public boolean getElement(int index) {
    return data[index];
  }

  @Override
  public void setElement(int index, boolean value) {
    data[index] = value;
  }
}

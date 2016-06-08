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

import net.mintern.primitive.comparators.IntComparator;

import org.briljantframework.Check;
import org.briljantframework.array.AbstractIntArray;
import org.briljantframework.array.IntArray;
import org.briljantframework.array.Range;
import org.briljantframework.array.api.ArrayBackend;

/**
 * @author Isak Karlsson
 */
class NetlibIntRange extends AbstractIntArray implements Range {

  private final int start, end, step;

  NetlibIntRange(ArrayBackend backend, int start, int end, int step) {
    super(backend, new int[] {computeRangeSize(start, end, step)});
    this.start = start;
    this.end = end;
    this.step = step;
  }

  private static int computeRangeSize(int start, int end, int step) {
    if ((start < end && step < 0) || (start > end) && step > 0) {
      throw new IllegalArgumentException();
    }
    int i = Math.abs(end - start);
    if (i % step == 0) {
      return i / Math.abs(step);
    } else {
      return i / Math.abs(step) + 1;
    }
  }

  private NetlibIntRange(ArrayBackend bj, int offset, int[] shape, int[] stride, int start, int end,
      int step) {
    super(bj, offset, shape, stride);
    this.start = start;
    this.end = end;
    this.step = step;
  }

  @Override
  public IntArray asView(int offset, int[] shape, int[] stride) {
    return new NetlibIntRange(getArrayBackend(), offset, shape, stride, start(), end(), step());
  }

  @Override
  public IntArray newEmptyArray(int... shape) {
    return new NetlibIntArray(getArrayBackend(), shape);
  }

  @Override
  public int start() {
    return start;
  }

  @Override
  public int end() {
    return end;
  }

  @Override
  public int step() {
    return step;
  }

  @Override
  public boolean contains(Object v) {
    if (!(v instanceof Integer)) {
      return false;
    }
    int value = (Integer) v;
    return value % step == 0 && value < end && value >= start;
  }

  @Override
  public boolean isView() {
    return true;
  }

  @Override
  protected int elementSize() {
    return computeRangeSize(start(), end(), step());
  }

  @Override
  public void sort() {
    // already sorted in this order
  }

  @Override
  public void sort(IntComparator cmp) {
    throw new UnsupportedOperationException("Can't sort ranges in-place");
  }

  @Override
  protected void setElement(int index, int value) {
    throw new UnsupportedOperationException("Can't set element of immutable range");
  }

  @Override
  protected int getElement(int index) {
    Check.index(index, computeRangeSize(start(), end(), step()));
    return start + index * step;
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(new int[] {start, end, step});
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Range) {
      Range rng = (Range) obj;
      // Two ranges are exactly the same if they have the same start, end and step size
      // and the same shape and stride
      return start() == rng.start() && end() == rng.end() && step() == rng.step()
          && Arrays.equals(this.getShape(), rng.getShape())
          && Arrays.equals(getStride(), rng.getStride());
    }
    return super.equals(obj);
  }

}

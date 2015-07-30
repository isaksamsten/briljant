package org.briljantframework.array.base;

import com.google.common.base.Objects;

import org.briljantframework.array.AbstractIntArray;
import org.briljantframework.array.IntArray;
import org.briljantframework.array.Range;
import org.briljantframework.array.api.ArrayFactory;

/**
 * @author Isak Karlsson
 */
class BaseRange extends AbstractIntArray implements Range {

  private final int start, end, step;

  public BaseRange(ArrayFactory bj, int start, int end, int step) {
    super(bj, new int[]{getSize(start, end, step)});
    this.start = start;
    this.end = end;
    this.step = step;
  }

  public BaseRange(ArrayFactory bj, int offset, int[] shape, int[] stride, int majorStride,
                   int start, int end, int step) {
    super(bj, offset, shape, stride, majorStride);
    this.start = start;
    this.end = end;
    this.step = step;
  }

  private static int getSize(int start, int end, int step) {
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

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Range) {
      Range rng = (Range) obj;
      return start() == rng.start() &&
             end() == rng.end() &&
             step() == rng.step();
    }
    return super.equals(obj);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(start, end, step);
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
  public boolean contains(int value) {
    return value % step == 0 && value < end && value >= start;
  }

  @Override
  public IntArray newEmptyArray(int... shape) {
    return new BaseIntArray(getArrayFactory(), shape);
  }

  @Override
  public IntArray asView(int offset, int[] shape, int[] stride, int majorStride) {
    return new BaseRange(getArrayFactory(), offset, shape, stride, majorStride,
                         start(), end(), step());
  }

  @Override
  protected int elementSize() {
    return getSize(start(), end(), step());
  }

  @Override
  public boolean isView() {
    return true;
  }

  @Override
  protected void setElement(int index, int value) {
    throw new UnsupportedOperationException();
  }

  @Override
  protected int getElement(int index) {
    if (index < getSize(start(), end(), step()) && index >= 0) {
      return start + index * step;
    } else {
      throw new IndexOutOfBoundsException(String.format("0 >= %d > %d", index, size()));
    }
  }

  @Override
  public int[] data() {
    int[] data = new int[size()];
    for (int i = 0; i < data.length; i++) {
      data[i] = get(i);
    }
    return data;
  }
}

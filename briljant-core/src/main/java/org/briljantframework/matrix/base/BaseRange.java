package org.briljantframework.matrix.base;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import org.briljantframework.matrix.AbstractIntArray;
import org.briljantframework.matrix.IntArray;
import org.briljantframework.matrix.Range;
import org.briljantframework.matrix.api.ArrayFactory;

/**
 * @author Isak Karlsson
 */
class BaseRange extends AbstractIntArray implements Range {

  private final int start, end, step;

  BaseRange(ArrayFactory bj, int start, int end, int step) {
    super(bj, getSize(start, end, step));
    Preconditions.checkArgument(start < end);
    this.start = start;
    this.end = end;
    this.step = step;
  }

  public BaseRange(ArrayFactory bj, int offset, int[] shape, int[] stride,
                   int start, int end, int step) {
    super(bj, offset, shape, stride);
    this.start = start;
    this.end = end;
    this.step = step;
  }

  private static int getSize(int start, int end, int step) {
    int i = end - start;
    if (i % step == 0) {
      return i / step;
    } else {
      return i / step + 1;
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
    return new BaseIntArray(getMatrixFactory(), shape);
  }

  @Override
  protected IntArray makeView(int offset, int[] shape, int[] stride) {
    return new BaseRange(getMatrixFactory(), offset, shape, stride, start(), end(), step());
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
}

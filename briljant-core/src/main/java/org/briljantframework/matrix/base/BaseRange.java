package org.briljantframework.matrix.base;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import org.briljantframework.matrix.AbstractIntMatrix;
import org.briljantframework.matrix.Indexer;
import org.briljantframework.matrix.IntMatrix;
import org.briljantframework.matrix.Range;
import org.briljantframework.matrix.api.MatrixFactory;

/**
 * @author Isak Karlsson
 */
class BaseRange extends AbstractIntMatrix implements Range {

  private final int start, end, step;

  BaseRange(MatrixFactory bj, int start, int end, int step) {
    super(bj, getSize(start, end, step), 1);
    Preconditions.checkArgument(start < end);
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
  public void set(int i, int j, int value) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void set(int index, int value) {
    throw new UnsupportedOperationException();
  }

  @Override
  public IntMatrix reshape(int rows, int columns) {
    return copy().reshape(rows, columns);
  }

  @Override
  public boolean isView() {
    return true;
  }

  @Override
  public int get(int i, int j) {
    return get(Indexer.columnMajor(0, i, j, rows(), columns()));
  }

  @Override
  public int get(int index) {
    if (index < size() && index >= 0) {
      return start + index * step;
    } else {
      throw new IndexOutOfBoundsException();
    }
  }
}

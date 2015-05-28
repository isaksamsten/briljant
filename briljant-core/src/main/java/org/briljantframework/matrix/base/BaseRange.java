package org.briljantframework.matrix.base;

import com.google.common.base.Preconditions;

import org.briljantframework.matrix.AbstractIntMatrix;
import org.briljantframework.matrix.Indexer;
import org.briljantframework.matrix.IntMatrix;
import org.briljantframework.matrix.Range;
import org.briljantframework.matrix.storage.Storage;
import org.briljantframework.matrix.api.MatrixFactory;

/**
 * @author Isak Karlsson
 */
class BaseRange extends AbstractIntMatrix implements Range {

  private final int start, end, step;

  BaseRange(MatrixFactory bj, int start, int end, int step) {
    super(bj, 1, (end - start) / step);
    Preconditions.checkArgument(start < end);
    this.start = start;
    this.end = end;
    this.step = step;
  }

//  /**
//   * Construct an interval from {@code start} (inclusive) to {@code end} (exclusive) by {@code
//   * step}
//   *
//   * @param start the start
//   * @param end   the end
//   * @param step  the step
//   * @return the range
//   */
//  public static Range range(int start, int end, int step) {
//    return new Range(start, end, step);
//  }
//
//  /**
//   * Construct an interval from {@code start} (inclusive) to {@code end} (exclusive) by {@code 1}
//   *
//   * @param start the start
//   * @param end   the end
//   * @return the range
//   */
//  public static Range range(int start, int end) {
//    return new Range(start, end, 1);
//  }
//
//  /**
//   * Construct an interval from {@code 0} (inclusive) to {@code end} (exclusive) by {@code 1}.
//   *
//   * @param end the end
//   * @return a new range
//   */
//  public static Range range(int end) {
//    return range(0, end);
//  }

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
    return false;
  }

  @Override
  public IntMatrix newEmptyMatrix(int rows, int columns) {
    return new BaseIntMatrix(getMatrixFactory(), rows, columns);
  }

  @Override
  public int get(int i, int j) {
    return get(Indexer.columnMajor(i, j, rows(), columns()));
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

package org.briljantframework.matrix;

import java.util.Iterator;

import org.briljantframework.matrix.storage.Storage;

import com.google.common.base.Preconditions;

/**
 * @author Isak Karlsson
 */
public class Range extends AbstractIntMatrix {

  private final int start, end, step;

  private Range(int start, int end, int step) {
    super(1, (end - start) / step);
    Preconditions.checkArgument(start < end);
    this.start = start;
    this.end = end;
    this.step = step;
  }

  /**
   * Construct an interval from {@code start} (inclusive) to {@code end} (exclusive) by {@code step}
   * 
   * @param start the start
   * @param end the end
   * @param step the step
   * @return the range
   */
  public static Range range(int start, int end, int step) {
    return new Range(start, end, step);
  }

  /**
   * Construct an interval from {@code start} (inclusive) to {@code end} (exclusive) by {@code 1}
   *
   * @param start the start
   * @param end the end
   * @return the range
   */
  public static Range range(int start, int end) {
    return new Range(start, end, 1);
  }

  /**
   * Construct an interval from {@code 0} (inclusive) to {@code end} (exclusive) by {@code 1}.
   *
   * @param end the end
   * @return a new range
   */
  public static Range range(int end) {
    return range(0, end);
  }

  public int start() {
    return start;
  }

  public int end() {
    return end;
  }

  public int step() {
    return step;
  }

  @Override
  public Iterator<Integer> iterator() {
    return new Iterator<Integer>() {
      private int current = start;

      @Override
      public boolean hasNext() {
        return current < end;
      }

      @Override
      public Integer next() {
        int tmp = current;
        current += step;
        return tmp;
      }
    };
  }

  public boolean contains(Object o) {
    if (o instanceof Integer) {
      int value = (int) o;
      return value % step == 0 && value < end && value >= start;
    } else {
      return false;
    }
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
  public Storage getStorage() {
    return copy().getStorage();
  }

  @Override
  public IntMatrix newEmptyMatrix(int rows, int columns) {
    return new DefaultIntMatrix(rows, columns);
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

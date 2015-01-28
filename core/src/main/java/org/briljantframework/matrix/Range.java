package org.briljantframework.matrix;

import static com.google.common.base.Preconditions.checkElementIndex;

import java.util.Collection;
import java.util.Iterator;

import com.google.common.base.Preconditions;

/**
 * @author Isak Karlsson
 */
public class Range extends AbstractIntMatrix implements Collection<Integer> {

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

  @Override
  public Object[] toArray() {
    throw new UnsupportedOperationException();
  }

  @Override
  public <T> T[] toArray(T[] ts) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean add(Integer integer) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean remove(Object o) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean containsAll(Collection<?> collection) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean addAll(Collection<? extends Integer> collection) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean removeAll(Collection<?> collection) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean retainAll(Collection<?> collection) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void clear() {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isEmpty() {
    return size() == 0;
  }

  @Override
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
  public IntMatrix newEmptyMatrix(int rows, int columns) {
    return new ArrayIntMatrix(rows, columns);
  }

  @Override
  public int get(int i, int j) {
    return get(Indexer.columnMajor(i, j, rows(), columns()));
  }

  @Override
  public int get(int index) {
    return start + (checkElementIndex(index, size()) * step);
  }

  @Override
  public boolean isArrayBased() {
    return false;
  }
}

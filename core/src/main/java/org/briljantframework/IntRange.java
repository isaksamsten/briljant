package org.briljantframework;

import java.util.AbstractSet;
import java.util.Iterator;

import com.google.common.base.Preconditions;

/**
 * A range represents a closed interval (with fast implementation of {@link #contains(Object)}).
 *
 * Created by Isak Karlsson on 09/11/14.
 */
public class IntRange extends AbstractSet<Integer> {

  private final int start, end, step;

  private IntRange(int start, int end, int step) {
    Preconditions.checkArgument(start < end);
    this.start = start;
    this.end = end;
    this.step = step;
  }

  /**
   *
   *
   * @param start the start
   * @param end the end
   * @param step the step
   * @return the range
   */
  public static IntRange closed(int start, int end, int step) {
    return new IntRange(start, end, step);
  }

  /**
   * Construct a closed interval
   *
   * @param start the start
   * @param end the end
   * @return the range
   */
  public static IntRange closed(int start, int end) {
    return new IntRange(start, end, 1);
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
  public int size() {
    return (end - start) / step;
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
  public String toString() {
    return String.format("%d %d %d", start, end, step);
  }
}

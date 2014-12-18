package org.briljantframework;

import java.util.function.IntBinaryOperator;

/**
 * QuickSort implementation from <a
 * href="https://github.com/apache/mahout/blob/master/math/src/main/java/org
 * /apache/mahout/math/Sorting.java">Apache Mahout</a>
 *
 * @author Isak Karlsson
 */
public class Sort {

  /**
   * This is used for 'external' sorting. The comparator takes <em>indices</em>, not values, and
   * compares the external values found at those indices.
   * 
   * @param a
   * @param b
   * @param c
   * @param comp
   * @return
   */
  private static int med3(int a, int b, int c, IntBinaryOperator comp) {
    int comparisonab = comp.applyAsInt(a, b);
    int comparisonac = comp.applyAsInt(a, c);
    int comparisonbc = comp.applyAsInt(b, c);
    return comparisonab < 0 ? (comparisonbc < 0 ? b : (comparisonac < 0 ? c : a))
        : (comparisonbc > 0 ? b : (comparisonac > 0 ? c : a));
  }

  private static void checkBounds(int arrLength, int start, int end) {
    if (start > end) {
      // K0033=Start index ({0}) is greater than end index ({1})
      throw new IllegalArgumentException("Start index " + start + " is greater than end index "
          + end);
    }
    if (start < 0) {
      throw new ArrayIndexOutOfBoundsException("Array index out of range " + start);
    }
    if (end > arrLength) {
      throw new ArrayIndexOutOfBoundsException("Array index out of range " + end);
    }
  }

  /**
   * Sorts some external data with QuickSort.
   *
   * @param start the start index to sort.
   * @param end the last + 1 index to sort.
   * @param comp the comparator.
   * @param swap an object that can exchange the positions of two items.
   * @throws IllegalArgumentException if {@code start > end}.
   * @throws ArrayIndexOutOfBoundsException if {@code start < 0} or {@code end > array.length}.
   */
  public static void quickSort(int start, int end, IntBinaryOperator comp, Swappable swap) {
    checkBounds(end + 1, start, end);
    quickSort0(start, end, comp, swap);
  }

  private static void quickSort0(int start, int end, IntBinaryOperator comp, Swappable swap) {
    int length = end - start;
    if (length < 7) {
      insertionSort(start, end, comp, swap);
      return;
    }
    int middle = (start + end) / 2;
    if (length > 7) {
      int bottom = start;
      int top = end - 1;
      if (length > 40) {
        // for lots of data, bottom, middle and top are medians near the beginning, middle or end of
        // the data
        int skosh = length / 8;
        bottom = med3(bottom, bottom + skosh, bottom + (2 * skosh), comp);
        middle = med3(middle - skosh, middle, middle + skosh, comp);
        top = med3(top - (2 * skosh), top - skosh, top, comp);
      }
      middle = med3(bottom, middle, top, comp);
    }

    int partitionIndex = middle; // an index, not a value.

    // regions from a to b and from c to d are what we will recursively sort
    int a = start;
    int b = a;
    int c = end - 1;
    int d = c;
    while (b <= c) {
      // copy all values equal to the partition value to before a..b. In the process, advance b
      // as long as values less than the partition or equal are found, also stop when a..b collides
      // with c..d
      int comparison;
      while (b <= c && (comparison = comp.applyAsInt(b, partitionIndex)) <= 0) {
        if (comparison == 0) {
          if (a == partitionIndex) {
            partitionIndex = b;
          } else if (b == partitionIndex) {
            partitionIndex = a;
          }
          swap.swap(a, b);
          a++;
        }
        b++;
      }
      // at this point [start..a) has partition values, [a..b) has values < partition
      // also, either b>c or v[b] > partition value

      while (c >= b && (comparison = comp.applyAsInt(c, partitionIndex)) >= 0) {
        if (comparison == 0) {
          if (c == partitionIndex) {
            partitionIndex = d;
          } else if (d == partitionIndex) {
            partitionIndex = c;
          }
          swap.swap(c, d);

          d--;
        }
        c--;
      }
      // now we also know that [d..end] contains partition values,
      // [c..d) contains values > partition value
      // also, either b>c or (v[b] > partition OR v[c] < partition)

      if (b <= c) {
        // v[b] > partition OR v[c] < partition
        // swapping will let us continue to grow the two regions
        if (c == partitionIndex) {
          partitionIndex = b;
        } else if (b == partitionIndex) {
          partitionIndex = d;
        }
        swap.swap(b, c);
        b++;
        c--;
      }
    }
    // now we know
    // b = c+1
    // [start..a) and [d..end) contain partition value
    // all of [a..b) are less than partition
    // all of [c..d) are greater than partition

    // shift [a..b) to beginning
    length = Math.min(a - start, b - a);
    int l = start;
    int h = b - length;
    while (length-- > 0) {
      swap.swap(l, h);
      l++;
      h++;
    }

    // shift [c..d) to end
    length = Math.min(d - c, end - 1 - d);
    l = b;
    h = end - length;
    while (length-- > 0) {
      swap.swap(l, h);
      l++;
      h++;
    }

    // recurse left and right
    length = b - a;
    if (length > 0) {
      quickSort0(start, start + length, comp, swap);
    }

    length = d - c;
    if (length > 0) {
      quickSort0(end - length, end, comp, swap);
    }
  }

  /**
   * In-place insertion sort that is fast for pre-sorted data.
   *
   * @param start Where to start sorting (inclusive)
   * @param end Where to stop (exclusive)
   * @param comp Sort order.
   * @param swap How to swap items.
   */
  private static void insertionSort(int start, int end, IntBinaryOperator comp, Swappable swap) {
    for (int i = start + 1; i < end; i++) {
      for (int j = i; j > start && comp.applyAsInt(j - 1, j) > 0; j--) {
        swap.swap(j - 1, j);
      }
    }
  }



}

package org.briljantframework.matrix;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.briljantframework.IntRange;
import org.briljantframework.matrix.slice.Range;
import org.briljantframework.matrix.slice.Slice;
import org.junit.Test;

public class IntRangeTest {

  @Test
  public void testToString() throws Exception {
    Range r = Range.exclusive(0, 1000);
    System.out.println(r);

  }

  @Test
  public void testInclusive() throws Exception {
    Range range = Range.inclusive(0, 3);
    Slice slice = range.getSlice();
    int[] values = new int[range.length()];
    int i = 0;
    while (slice.hasNext(range.length())) {
      values[i++] = slice.next();
    }
    assertArrayEquals(new int[] {0, 1, 2, 3}, values);
  }

  @Test
  public void testExclusive() throws Exception {
    Range range = Range.exclusive(0, 3);
    Slice slice = range.getSlice();
    int[] values = new int[range.length()];
    int i = 0;
    while (slice.hasNext(4)) {
      values[i++] = slice.next();
    }
    assertArrayEquals(new int[] {0, 1, 2}, values);
  }

  @Test
  public void testLength() throws Exception {
    assertEquals(5, new Range(0, 10, 2).length());
    assertEquals(1, new Range(0, 4, 2).length());
    assertEquals(0, new Range(1, 1, 1).length());
  }

  @Test
  public void testSlice() throws Exception {
    IntRange range = IntRange.closed(0, 10, 1);

    for (int i = 0; i < 10; i++) {
      System.out.println(i + " in " + range + " " + range.contains(i));
    }
  }
}

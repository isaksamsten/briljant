package org.briljantframework.array;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * @author Isak Karlsson <isak-kar@dsv.su.se>
 */
public class ArrayTest {

  @Test
  public void testSet() throws Exception {
    Array<Integer> a = Arrays.range(3 * 3).reshape(3, 3).boxed();
    Array<Integer> b = Arrays.newArray(3, 3);
    b.set(a.where(i -> i > 2), 10);
    assertEquals(Arrays.newVector(null, null, null, 10, 10, 10, 10, 10, 10).reshape(3, 3), b);
  }

  @Test
  public void testGetBooleanArray() throws Exception {
    Array<Integer> a = Arrays.range(3 * 3).reshape(3, 3).boxed();
    Array<Integer> x = a.get(a.where(i -> i > 2));
    assertEquals(Arrays.newVector(3, 4, 5, 6, 7, 8), x);
  }
}

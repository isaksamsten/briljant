package org.briljantframework.array.api;

import static org.junit.Assert.assertEquals;

import org.briljantframework.array.Array;
import org.briljantframework.array.DoubleArray;
import org.junit.Test;

/**
 * @author Isak Karlsson <isak-kar@dsv.su.se>
 */
public abstract class ArrayFactoryTest {

  @Test
  public void testArrayGet_BooleanArray() throws Exception {
    Array<Integer> a = getFactory().range(3 * 3).reshape(3, 3).boxed();
    Array<Integer> x = a.get(a.where(i -> i > 2));
    assertEquals(getFactory().newVector(3, 4, 5, 6, 7, 8), x);
  }

  public abstract ArrayFactory getFactory();

  @Test
  public void testArraySet_BooleanArray() throws Exception {
    Array<Integer> a = getFactory().range(3 * 3).reshape(3, 3).boxed();
    Array<Integer> b = getFactory().newArray(3, 3);
    b.set(a.where(i -> i > 2), 10);
    assertEquals(getFactory().newVector(null, null, null, 10, 10, 10, 10, 10, 10).reshape(3, 3), b);
  }

  @Test
  public void testDoubleArraySet_BooleanArray() throws Exception {
    DoubleArray a = getFactory().range(3 * 3).reshape(3, 3).asDouble();
    DoubleArray b = getFactory().newDoubleArray(3, 3);
    b.set(a.where(i -> i > 2), 10);
    assertEquals(getFactory().newDoubleVector(0, 0, 0, 10, 10, 10, 10, 10, 10).reshape(3, 3), b);
  }

  @Test
  public void testDoubleArrayGet_BooleanArray() throws Exception {
    DoubleArray a = getFactory().range(3 * 3).reshape(3, 3).asDouble();
    DoubleArray x = a.get(a.where(i -> i > 2));
    assertEquals(getFactory().newDoubleVector(3, 4, 5, 6, 7, 8), x);
  }
}

package org.briljantframework.array.api;

import static org.briljantframework.array.ArraySelector.ALL;
import static org.junit.Assert.assertEquals;

import org.briljantframework.array.Array;
import org.briljantframework.array.DoubleArray;
import org.briljantframework.array.IntArray;
import org.briljantframework.array.Range;
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

  @Test
  public void testDoubleArrayGet_Range() throws Exception {
    DoubleArray a = getFactory().range(9 * 3).reshape(9, 3).asDouble();
    assertEquals(getFactory().newDoubleVector(1, 2, 10, 11).reshape(2, 2),
        a.get(getFactory().range(1, 3), getFactory().range(2)));
    assertEquals(getFactory().newDoubleVector(0, 2, 4, 6, 8, 9, 11, 13, 15, 17, 18, 20, 22, 24, 26)
        .reshape(5, 3), a.get(getFactory().range(0, 9, 2), ALL));
  }

  @Test
  public void testDoubleArraySelect_IntIndexer() throws Exception {
    IntArray a = getFactory().range(3 * 3 * 3).reshape(3, 3, 3);
    // System.out.println(a.select(IntArray.of(0, 1, 0), IntArray.of(1, 1, 1)));

    // System.out.println(a.reshape(3, 3, 3).select(IntArray.of(0, 1), IntArray.of(0, 0)));
    //
    // System.out.println(a.reshape(3, 3, 3).select(IntArray.of(0, 1), IntArray.of(0, 0),
    // IntArray.of(0, 0)));


    IntArray i = Range.of(2).reshape(2, 1).copy();
//    for (int j = 0; j < i.size(0); j++) {
//      i.select(j).assign(j == 0 ? 2 : 1);
//    }
//    System.out.println(i);

    IntArray i2 = i.copy();
    i2.assign(0);

    IntArray b = a.reshape(3, 3, 3).slice(i,i,i);
    System.out.println(b);
  }
}

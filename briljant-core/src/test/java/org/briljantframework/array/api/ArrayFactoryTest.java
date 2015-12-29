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
  public void testDoubleArray_assign_broadcast() throws Exception {
    DoubleArray zeros = getFactory().newDoubleArray(3, 3);
    zeros.assign(getFactory().newDoubleVector(1, 2, 3).reshape(1, 3));
    assertEquals(getFactory().newDoubleMatrix(new double[][] { {1, 2, 3}, {1, 2, 3}, {1, 2, 3}}),
        zeros);

  }

  @Test
  public void testBroadCastIgen() throws Exception {
    IntArray i = Range.of(4 * 3).reshape(4, 3);
    System.out.println(i.get(ALL, Range.of(1, 3)));
  }

  @Test
  public void testDoubleArraySelect_IntIndexer() throws Exception {
    IntArray x = Range.of(10 * 20 * 30).reshape(10, 20, 30);
    IntArray y =
        x.getSlice(IntArray.of(1, 2, 3, 4).reshape(2, 2), IntArray.zeros(2, 2, 2), Range.of(3));
    System.out.println(y);
    // IntArray y = Range.of(10 * 20 * 30 * 40 * 50).reshape(10, 20, 30, 40, 50);
    // System.out.println(y.getSlice(Range.of(10), IntArray.zeros(2, 3, 4), Range.of(30),
    // IntArray.zeros(2, 3, 4), Range.of(50)));

    // IntArray x = Range.of(4 * 3).reshape(4, 3);
    // System.out.println(x);
    // System.out.println(x.getSlice(Range.of(4), IntArray.of(0, 1, 0, 1)));
    // System.out.println(x.getSlice(IntArray.of(0, 1, 0, 1).reshape(2, 2), Range.of(3)));



    // IntArray a = getFactory().range(4 * 3).reshape(4, 3).copy();
    // IntArray rows = IntArray.of(0, 3).reshape(2, 1);
    // IntArray columns = IntArray.of(0, 2).reshape(1, 2);
    //
    // List<IntArray> x = broadcastArrays(Arrays.asList(rows, columns));
    // System.out.println(x);
    //
    // System.out.println(a.getSlice(null, x.get(0)));

    // System.out.println(a.select(IntArray.of(0, 1, 0), IntArray.of(1, 1, 1)));

    // System.out.println(a.reshape(3, 3, 3).select(IntArray.of(0, 1), IntArray.of(0, 0)));
    //
    // System.out.println(a.reshape(3, 3, 3).select(IntArray.of(0, 1), IntArray.of(0, 0),
    // IntArray.of(0, 0)));
    // IntArray b = getFactory().range(6 * 6).reshape(6, 6).copy();
    // b.setSlice(new IntArray[] {IntArray.of(5, 4, 1)}, IntArray.of(1, 2, 3, 4, 5, 6));
    // System.out.println(b);

    // System.out.println(org.briljantframework.array.Arrays.broadcastTo(Range.of(3), 3, 3,3));

    // IntArray r = Range.of(3).reshape(3,1);
    // System.out.println(org.briljantframework.array.Arrays.repeat(r, 2));
    //
    // IntArray i = Range.of(2).reshape(2, 1).copy();
    // System.out.println(i);
    // for (int j = 0; j < i.size(0); j++) {
    // i.select(j).assign(j == 0 ? 2 : 1);
    // }
    // System.out.println(i);

    // IntArray i2 = i.copy();
    // i2.assign(0);
    // System.out.println(a);
    // IntArray rows = IntArray.of(0, 1, 2, 0, 1, 2).reshape(3, 2, 1);
    // i = org.briljantframework.array.Arrays.broadcastTo(i, rows.getShape());
    // System.out.println(i);
    // System.out.println(rows);
    // List<IntArray> index = Arrays.asList(rows, i, i);
    // System.out.println(a.getSlice(index));
    // IntArray value = IntArray.of(1000, 1000, 1000, 1000, 1000, 1000).reshape(2, 1, 3);
    // System.out.println(value);
    // a.reshape(3, 3, 3).setSlice(index, value);
    // System.out.println(a);
    // IntArray x = Range.of(3 * 3 * 3).reshape(3, 3, 3);
    // IntArray i = broadcastTo(IntArray.of(0, 0, 0).reshape(1,3), 3,2,3);

    // NOTE: when encountering ALL create a new array with dims (DIM_IN_LOC, ..., last)

    // NOTE: first: no selections in dim 1; second: no selections in dim 2 and so on
    // IntArray i = IntArray.zeros(1, 3, 3);
    // for (int n = 0; n < i.size(0); n++) {
    // i.select(n).assign(n);
    // }

    // IntArray j = IntArray.of(0, 1, 1, 0, 1, 1, 0, 1, 1).reshape(3, 3);
    // System.out.println(Arrays.toString(j.getStride()));
    // System.out.println(j.asView(new int[]{1,3,3,3}, new int[]{0,0,1,3}));
    //
    // System.out.println(j);

    // IntArray k = IntArray.zeros(3, 1, 3, 3);
    // int dim = 3;
    // for (int n = 0; n < k.size(dim); n++) {
    // k.select(dim, n).assign(n);
    // }
    // System.out.println(k);

    // List<IntArray> x1 = new ArrayList<>(broadcastArrays(Arrays.asList(i, j, k)));
    // System.out.println(x1.get(1));
    // for (IntArray xs : x1) {
    // System.out.println(xs);
    // }
    // x1.add(k);
    // List<IntArray> x2 = broadcastArrays(x1);
    // System.out.println(x.getSlice(i, j, k));

    // IntArray f = x.get(Range.of(2), Range.of(2));
    // System.out.println(f);
    // System.out.println(f.getSlice(IntArray.of(0)));


  }
}

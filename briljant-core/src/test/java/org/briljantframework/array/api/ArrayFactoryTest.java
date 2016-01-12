/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Isak Karlsson
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.briljantframework.array.api;

import static org.briljantframework.array.BasicIndex.ALL;
import static org.junit.Assert.assertEquals;

import org.briljantframework.array.Array;
import org.briljantframework.array.Arrays;
import org.briljantframework.array.DoubleArray;
import org.briljantframework.array.IntArray;
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
  public void testGetVector() throws Exception {
    DoubleArray x = DoubleArray.of(1, 2, 3, 4);
    DoubleArray y = DoubleArray.zeros(4);
    Arrays.axpy(2, x, y);
    System.out.println(y);
  }

  @Test
  public void testName() throws Exception {
    DoubleArray x = Arrays.linspace(0, 9, 10).reshape(2, 5).getRow(0).transpose();
    DoubleArray y = Arrays.linspace(0, 9, 10).reshape(2, 5).getRow(0);

    System.out.println(x);
    System.out.println(y);

    Arrays.axpy(1, x, y);
    System.out.println(y);
  }

  @Test
  public void testfuck() throws Exception {
    DoubleArray x1 = Arrays.linspace(0, 11, 12).reshape(3, 4);
    DoubleArray y1 = Arrays.linspace(0, 11, 12).reshape(4, 3);
    DoubleArray x = x1.getRow(2);
    DoubleArray y = y1.getColumn(0);

    System.out.println(x1);
    System.out.println(y1);
    double alpha = 1;

    System.out.println(x.ravel());
    System.out.println(y.ravel());
    Arrays.axpy(alpha, x.ravel(), y.ravel());
    System.out.println(y);


    for (int i = 0; i < x.size(); i++) {
      System.out.println(x.data()[x.getOffset() + i*3] + " + " + y.data()[y.getOffset() + i]);
    }

  }
}

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
package org.briljantframework.array;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by isak on 5/3/16.
 */
public abstract class AbstractBaseArrayTest<S extends BaseArray<S>> {

  /**
   * Create a list of arrays that will be manipulated and given back to the test method.
   * 
   * @param <E>
   */
  protected interface ArrayTest<E extends BaseArray<E>> {
    List<E> create();

    void test(List<E> actual);
  }

  /**
   * Create:
   * <p>
   * 
   * <pre>
   * 0 => [1,2,3,4]
   * 1 => [5,6,7,8]
   *
   * 2 => [[1,2],[3,4]]
   * 3 => [[5,6],[7,8]]
   *
   * 4 => [[[1,2]],[[1,2]]]
   * 5 => [[[5,6]],[[7,8]]]
   * </pre>
   * <p>
   * Test: for each pair, that: 2 has been changed to 5
   */
  protected abstract ArrayTest<S> createSetSingleIndexTest();

  @Test
  public final void setSingleIndex() throws Exception {
    ArrayTest<S> test = createSetSingleIndexTest();
    List<S> arrays = test.create();

    List<S> actual = new ArrayList<>();
    arrays.get(0).set(1, arrays.get(1), 0);
    actual.add(arrays.get(0));

    arrays.get(2).set(2, arrays.get(3), 0);
    actual.add(arrays.get(2));

    arrays.get(4).set(2, arrays.get(5), 0);
    actual.add(arrays.get(4));

    test.test(actual);
  }

  /**
   * Create:
   * <p>
   * 
   * <pre>
   *   0 => [a,b,c,d]
   *
   *   1 => [[a,b,c,d],[a,b,c,d]]
   *   2 => [[e,e,e,e],[e,e,e,e]]
   *
   *   3 => [[[e,e],[e,e]]]
   *
   * </pre>
   * <p>
   * Test:
   * <p>
   * 
   * <pre>
   *   0 => [[e,e,e,e],[e,e,e,e]]
   * </pre>
   */
  protected abstract ArrayTest<S> createSetMatrixIndexTest();

  @Test
  public final void setMatrixIndex() throws Exception {
    ArrayTest<S> test = createSetMatrixIndexTest();
    List<S> arrays = test.create();

    try {
      S a = arrays.get(0);
      for (int i = 0; i < a.size(); i++) {
        a.set(0, i, a, 0, a.size() - i);
      }
      Assert.fail();
    } catch (IllegalStateException ignored) { // only for matrices
    }

    S a = arrays.get(1);
    S b = arrays.get(2);
    for (int i = 0; i < a.rows(); i++) {
      for (int j = 0; j < a.columns(); j++) {
        a.set(i, j, b, i, j);
      }
    }

    test.test(Collections.singletonList(a));

    try {
      S array = arrays.get(3);
      array.set(0, 1, array, 0, 1);
      Assert.fail();
    } catch (IllegalStateException ignored) { // only for matrices
    }
  }

  @Test
  public void setNDIndex() throws Exception {

  }

  @Test
  public void setToSingleIndexFromNDIndex() throws Exception {

  }

  @Test
  public void setToNDIndexFromSingleIndex() throws Exception {

  }

  @Test
  public void reverse() throws Exception {

  }

  /**
   * Create:
   * 
   * <pre>
   *   0 => 3 x 3 x 3 empty array
   *   1 => [a]
   *
   *   2 => 1 x 3 x 4 empty array
   *   3 => [a,b,c,d]
   * </pre>
   *
   * Test:
   * 
   * <pre>
   *   0 => 3 x 3 x 3 array filled with a
   *   1 => 1 x 3 x 4 where each vector along the last dimension equals [a,b,c,d]
   * </pre>
   *
   */
  protected abstract ArrayTest<S> createAssignTest();

  @Test
  public void assign() throws Exception {
    ArrayTest<S> test = createAssignTest();
    List<S> arrays = test.create();
    arrays.get(0).assign(arrays.get(1));
    arrays.get(2).assign(arrays.get(3));

    test.test(java.util.Arrays.asList(arrays.get(0), arrays.get(2)));
  }

  @Test
  public void forEach() throws Exception {

  }

  @Test
  public void setColumn() throws Exception {

  }

  @Test
  public void getColumn() throws Exception {

  }

  @Test
  public void setRow() throws Exception {

  }

  @Test
  public void getRow() throws Exception {

  }

  @Test
  public void reshape() throws Exception {

  }

  @Test
  public void ravel() throws Exception {

  }

  @Test
  public void select() throws Exception {

  }

  @Test
  public void select1() throws Exception {

  }

  @Test
  public void getView() throws Exception {

  }

  @Test
  public void getView1() throws Exception {

  }

  @Test
  public void getVector() throws Exception {

  }

  @Test
  public void getVectors() throws Exception {

  }

  @Test
  public void setVector() throws Exception {

  }

  @Test
  public void getDiagonal() throws Exception {

  }

  @Test
  public void get() throws Exception {

  }

  @Test
  public void get1() throws Exception {

  }

  @Test
  public void set5() throws Exception {

  }

  @Test
  public void set6() throws Exception {

  }

  @Test
  public void getView2() throws Exception {

  }

  @Test
  public void size() throws Exception {

  }

  @Test
  public void size1() throws Exception {

  }

  @Test
  public void vectors() throws Exception {

  }

  @Test
  public void stride() throws Exception {

  }

  @Test
  public void getOffset() throws Exception {

  }

  @Test
  public void getShape() throws Exception {

  }

  @Test
  public void getStride() throws Exception {

  }

  @Test
  public void isSquare() throws Exception {

  }

  @Test
  public void rows() throws Exception {

  }

  @Test
  public void columns() throws Exception {

  }

  @Test
  public void dims() throws Exception {

  }

  @Test
  public void isVector() throws Exception {

  }

  @Test
  public void isMatrix() throws Exception {

  }

  @Test
  public void asView() throws Exception {

  }

  @Test
  public void asView1() throws Exception {

  }

  @Test
  public void newEmptyArray() throws Exception {

  }

  @Test
  public void isView() throws Exception {

  }

  @Test
  public void isContiguous() throws Exception {

  }

  @Test
  public void transpose() throws Exception {

  }

  @Test
  public void copy() throws Exception {

  }

}

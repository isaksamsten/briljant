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

import static org.junit.Assume.assumeFalse;
import static org.junit.Assume.assumeNotNull;

import java.util.List;

import org.junit.Assume;
import org.junit.Test;

/**
 * Created by isak on 5/3/16.
 */
public abstract class AbstractBaseArrayTest<S extends BaseArray<S>> {


  @Test
  public void testAssign2() throws Exception {
    DoubleArray a = DoubleArray.of(1, 2, 3, 4);
    DoubleArray b = DoubleArray.of(1);

    a.assign(b);

    System.out.println(a);

    Arrays.plusAssign(b, a);
    System.out.println(a);
  }

  /**
   * Requires:
   *
   * <pre>
   *   actual:
   *     to: array of length > 4 with any dim
   *     from array of length > 4 with any dim
   *   expected:
   *     array with dim = to.dim where the element at location 1 is set to the value of from
   *     at location 3
   * </pre>
   */
  protected abstract ArrayTest<S> createSetSingleIndexTest();

  @Test
  public final void testSetFromSingleIndex() throws Exception {
    ArrayTest<S> test = createSetSingleIndexTest();
    assumeNotNull(test);

    List<TestCase<S>> testCase = test.getTestCases();
    Assume.assumeFalse(testCase.isEmpty());
    for (TestCase<S> t : testCase) {
      S to = t.getActual("to");
      S from = t.getActual("from");
      to.setFrom(1, from, 3);
      test.assertEqual(to, t.getExpected());
    }
  }

  /**
   * Requires:
   * <p>
   *
   * <pre>
   *   actual:
   *     to: 2d-array with size(0) and size(2) > 2
   *     from: 2d-array with size(0) and size(2) > 3
   *   expected:
   *     array with dims() == to.dims() where location [1,1] is equal to the value at location [3,3] in from
   * </pre>
   */
  protected abstract ArrayTest<S> createSetMatrixIndexTest();

  @Test
  public final void testSetMatrixIndex() throws Exception {
    ArrayTest<S> test = createSetMatrixIndexTest();
    assumeNotNull(test);

    List<TestCase<S>> testCase = test.getTestCases();
    Assume.assumeFalse(testCase.isEmpty());
    for (TestCase<S> t : testCase) {
      S to = t.getActual("to");
      S from = t.getActual("from");
      to.setFrom(1, 1, from, 2, 2);
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

  protected ArrayTest<S> createReverseTest() {
    return null;
  }

  @Test
  public void reverse() throws Exception {
    ArrayTest<S> test = createReverseTest();
    assumeNotNull(test);
    assumeFalse(test.isEmpty());
    for (TestCase<S> testCase : test) {
      test.assertEqual(testCase.getActual().reverse(), testCase.getExpected());
    }
  }

  /**
   * Create:
   *
   * <pre>
   *  actual:
   *    to: array with any dim
   *    from: array with any dim that can be broadcasted to 'to'
   *  expected:
   *     an array where to is assigned from
   * </pre>
   *
   */
  protected abstract ArrayTest<S> createAssignTest();

  @Test
  public void assign() throws Exception {
    ArrayTest<S> test = createAssignTest();
    assumeNotNull(test);

    List<TestCase<S>> testCase = test.getTestCases();
    Assume.assumeFalse(testCase.isEmpty());

    for (TestCase<S> t : testCase) {
      S to = t.getActual("to");
      S from = t.getActual("from");

      to.assign(from);

      S expected = t.getExpected();
      test.assertEqual(to, expected);
    }
  }

  protected abstract ArrayTest<S> createForEachTest();

  @Test
  public void testForEach() throws Exception {
    ArrayTest<S> test = createForEachTest();
    assumeNotNull(test);
    List<TestCase<S>> testCase = test.getTestCases();
    assumeFalse(testCase.isEmpty());
    for (TestCase<S> t : testCase) {
      int dim = t.getPayload("dim", Integer.class);
      t.getActual().forEach(dim, s -> test.assertEqual(s, t.getExpected()));
    }
  }

  protected ArrayTest<S> createSetColumnTest() {
    return null;
  }

  @Test
  public void setColumn() throws Exception {
    ArrayTest<S> test = createSetColumnTest();
    assumeNotNull(test);
    List<TestCase<S>> testCase = test.getTestCases();
    assumeFalse(testCase.isEmpty());

    for (TestCase<S> t : testCase) {
      S empty = t.getActual("empty");
      List<?> columns = t.getPayload("columns", List.class);
      for (Object column : columns) {
        S actual = t.getActual(column);
        assumeNotNull("Actual for column " + column + " is null", actual);
        empty.setColumn((int) column, actual);
      }
      test.assertEqual(empty, t.getExpected());
    }
  }

  protected ArrayTest<S> createGetColumnTest() {
    return null;
  }

  @Test
  public void getColumn() throws Exception {
    ArrayTest<S> test = createGetColumnTest();
    assumeNotNull(test);

    List<TestCase<S>> testCases = test.getTestCases();
    assumeFalse(testCases.isEmpty());

    for (TestCase<S> testCase : testCases) {
      S array = testCase.getActual();
      assumeNotNull("Actual array missing", array);
      for (int i = 0; i < array.columns(); i++) {
        test.assertEqual(array.getColumn(i), testCase.getExpected(i));
      }
    }
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

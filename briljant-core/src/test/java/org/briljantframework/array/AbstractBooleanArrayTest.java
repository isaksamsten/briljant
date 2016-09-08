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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * @author Isak Karlsson <isak-kar@dsv.su.se>
 */
public abstract class AbstractBooleanArrayTest extends AbstractBaseArrayTest<BooleanArray>
    implements ArrayBackendTest {

  private final class BooleanArrayTest extends ArrayTest<BooleanArray> {

    @Override void assertEqual(BooleanArray actual, BooleanArray expected) {
      ArrayAssert.assertArrayEquals(actual, expected);
    }
  }

  @Override
  protected ArrayTest<BooleanArray> createSetSingleIndexTest() {
    ArrayTest<BooleanArray> test = new BooleanArrayTest();
    //@formatter:off
//    test.addTest(
//        "to", getArrayFactory().newDoubleArray(5),
//        "from", getArrayFactory().newDoubleVector(1,2,3,4,5),
//        getArrayFactory().newDoubleVector(0,4,0,0,0)
//    );
//
//    test.addTest(
//        "to", getArrayFactory().newDoubleArray(6).reshape(2,3),
//        "from", getArrayFactory().newDoubleVector(1,2,3,4,5,6).reshape(3,2),
//        getArrayFactory().newDoubleVector(0,4,0,0,0,0).reshape(2,3)
//    );
    //@formatter:on

    return test;
  }

  @Override
  protected ArrayTest<BooleanArray> createSetMatrixIndexTest() {
    ArrayTest<BooleanArray> test = new BooleanArrayTest();
    //@formatter:off
//    test.addTest(
//        "to", getArrayFactory().newDoubleArray(4, 2),
//        "from", getArrayFactory().newDoubleVector(1,2,3,4,5,6,7,8,9).reshape(3,3),
//        getArrayFactory().newDoubleVector(0,0,0,0,0,9,0,0).reshape(4,2)
//    );
    //@formatter:off
    return test;
  }

  @Override
  protected ArrayTest<BooleanArray> createAssignTest() {
    return null;
  }

  @Override
  protected ArrayTest<BooleanArray> createForEachTest() {
    return null;
  }

  // @Override
  // protected final ArrayTest<BooleanArray> createSetSingleIndexTest() {
  // return new ArrayTest<BooleanArray>() {
  // @Override
  // public List<BooleanArray> getTestData() {
//        // @formatter:off
//        boolean[] a0 = {true, true, false, false};
//        boolean[] a1 = {false, false, true, true};
//
//        return java.util.Arrays.asList(
//            getArrayFactory().newBooleanVector(a0),
//            getArrayFactory().newBooleanVector(a1),
//
//            getArrayFactory().newBooleanVector(a0).reshape(2,2),
//            getArrayFactory().newBooleanVector(a1).reshape(2,2),
//
//            getArrayFactory().newBooleanVector(true,true,true,true).reshape(2,1,2),
//            getArrayFactory().newBooleanVector(false,false,false,false).reshape(2,1,2)
//        );
//        // @formatter:on
  // }
  //
  // @Override
  // public void test(List<BooleanArray> actual) {
  // assertEquals(false, actual.get(0).get(1));
  // assertEquals(false, actual.get(1).get(0, 1));
  // assertEquals(false, actual.get(2).get(0, 0, 1));
  // }
  // };
  //
  // }
  //
  // @Override
  // protected final ArrayTest<BooleanArray> createSetMatrixIndexTest() {
  // return new ArrayTest<BooleanArray>() {
  // @Override
  // public List<BooleanArray> getTestData() {
//        //@formatter:off
//        return java.util.Arrays.asList(
//            getArrayFactory().newBooleanVector(true,true,false,false),
//
//            getArrayFactory().newBooleanVector(true,true,true,true,true,true,true,true).reshape(2,4),
//            getArrayFactory().newBooleanArray(2,4),
//
//            getArrayFactory().newBooleanArray(2,1,2)
//        );
//        //@formatter:on
  // }
  //
  // @Override
  // public void test(List<BooleanArray> actual) {
  // for (int i = 0; i < actual.get(0).size(); i++) {
  // assertEquals(false, actual.get(0).get(i));
  // }
  // }
  // };
  // }
  //
  // @Override
  // protected final ArrayTest<BooleanArray> createAssignTest() {
  // return new ArrayTest<BooleanArray>() {
  // @Override
  // public List<BooleanArray> getTestData() {
//        //@formatter:off
//        return java.util.Arrays.asList(
//            getArrayFactory().newBooleanArray(3,3,3),
//            getArrayFactory().newBooleanVector(true),
//
//            getArrayFactory().newBooleanArray(1,3,4),
//            getArrayFactory().newBooleanVector(true,true,false,false)
//        );
//        //@formatter:on
  // }
  //
  // @Override
  // public void test(List<BooleanArray> actual) {
  // BooleanArray first = actual.get(0);
  // assertArrayEquals(new int[] {3, 3, 3}, first.getShape());
  // for (int i = 0; i < first.size(); i++) {
  // assertEquals(true, first.get(10));
  // }
  //
  // BooleanArray second = actual.get(1);
  // assertArrayEquals(new int[] {1, 3, 4}, second.getShape());
  // for (int i = 0; i < second.vectors(2); i++) {
  // BooleanArray vector = second.getVector(2, i);
  // assertEquals(vector, getArrayFactory().newBooleanVector(true, true, false, false));
  // }
  // }
  // };
  // }

  @Test
  public void testAny_withDim() throws Exception {
    BooleanArray array = getArrayFactory().newBooleanArray(3, 3, 3);
    array.set(new int[] {0, 0, 0}, true);
    array.set(new int[] {0, 1, 0}, true);
    array.set(new int[] {0, 0, 1}, true);

    BooleanArray expected_0 = BooleanArray.falses(3, 3);
    expected_0.set(0, 0, true);
    expected_0.set(0, 1, true);
    expected_0.set(1, 0, true);

    BooleanArray expected_1 = BooleanArray.falses(3, 3);
    expected_1.set(0, 0, true);
    expected_1.set(0, 1, true);

    BooleanArray expected_2 = BooleanArray.falses(3, 3);
    expected_2.set(0, 0, true);
    expected_2.set(0, 1, true);

    assertEquals(expected_0, array.any(0));
    assertEquals(expected_1, array.any(1));
    assertEquals(expected_2, array.any(2));
  }

  @Test
  public void testAny() throws Exception {
    assertEquals(true, getArrayFactory().newBooleanVector(true, false, true, false).any());
    assertEquals(false, getArrayFactory().newBooleanVector(false, false, false).any());
  }

  @Test
  public void testAll_withDim() throws Exception {
    BooleanArray array = getArrayFactory().newBooleanArray(3, 3, 3);
    array.set(new int[] {0, 0, 0}, true);
    array.set(new int[] {0, 1, 0}, true);
    array.set(new int[] {0, 2, 0}, true);
    array.set(new int[] {0, 0, 1}, true);

    BooleanArray expected_0 = getArrayFactory().newBooleanArray(3, 3);
    BooleanArray expected_1 = getArrayFactory().newBooleanArray(3, 3);
    expected_1.set(0, 0, true);
    BooleanArray expected_2 = getArrayFactory().newBooleanArray(3, 3);

    assertEquals(expected_0, array.all(0));
    assertEquals(expected_1, array.all(1));
    assertEquals(expected_2, array.all(2));
  }

  @Test
  public void testAll() throws Exception {
    assertEquals(false, getArrayFactory().newBooleanVector(true, false, true, false).all());
    assertEquals(true, getArrayFactory().newBooleanVector(true, true, true).all());
  }

}

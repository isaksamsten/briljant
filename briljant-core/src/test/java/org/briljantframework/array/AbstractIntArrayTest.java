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

/**
 * Created by isak on 5/3/16.
 */
public abstract class AbstractIntArrayTest extends AbstractBaseArrayTest<IntArray>
    implements ArrayBackendTest {

  private static class IntArrayTest extends ArrayTest<IntArray> {
    @Override
    void assertEqual(IntArray actual, IntArray expected) {
      ArrayAssert.assertArrayEquals(actual, expected);
    }
  }


  @Override
  protected ArrayTest<IntArray> createSetSingleIndexTest() {
    ArrayTest<IntArray> test = new IntArrayTest();
    //@formatter:off
    test.addTestCase(
        "to", getArrayFactory().newIntArray(5),
        "from", getArrayFactory().newIntVector(1,2,3,4,5),
        getArrayFactory().newIntVector(0,4,0,0,0)
    );

    test.addTestCase(
        "to", getArrayFactory().newIntArray(6).reshape(2,3),
        "from", getArrayFactory().newIntVector(1,2,3,4,5,6).reshape(3,2),
        getArrayFactory().newIntVector(0,4,0,0,0,0).reshape(2,3)
    );
    //@formatter:on

    return test;
  }

  @Override
  protected ArrayTest<IntArray> createSetMatrixIndexTest() {
    ArrayTest<IntArray> test = new IntArrayTest();
    //@formatter:off
    test.addTestCase(
        "to", getArrayFactory().newIntArray(4, 2),
        "from", getArrayFactory().newIntVector(1,2,3,4,5,6,7,8,9).reshape(3,3),
        getArrayFactory().newIntVector(0,0,0,0,0,9,0,0).reshape(4,2)
    );
    //@formatter:off
    return test;
  }

  @Override
  protected ArrayTest<IntArray> createAssignTest() {
    return null;
  }

  @Override
  protected ArrayTest<IntArray> createForEachTest() {
    return null;
  }
  // @Override
  // protected ArrayTest<IntArray> createSetSingleIndexTest() {
  // return new ArrayTest<IntArray>() {
  // @Override
  // public List<IntArray> getTestData() {
//        // @formatter:off
//        return java.util.Arrays.asList(
//            getArrayFactory().newIntVector(1,2,3,4),
//            getArrayFactory().newIntVector(5,6,7,8),
//
//            getArrayFactory().newIntVector(1,3,2,4).reshape(2,2),
//            getArrayFactory().newIntVector(5,7,6,8).reshape(2,2),
//
//            getArrayFactory().newIntVector(1,3,2,4).reshape(2,1,2),
//            getArrayFactory().newIntVector(5,7,6,8).reshape(2,1,2)
//        );
//        // @formatter:on
  // }
  //
  // @Override
  // public void test(List<IntArray> actual) {
  // assertEquals(5, actual.get(0).get(1));
  // assertEquals(5, actual.get(1).get(0, 1));
  // assertEquals(5, actual.get(2).get(0, 0, 1));
  // }
  // };
  //
  // }
  //
  // @Override
  // protected ArrayTest<IntArray> createSetMatrixIndexTest() {
  // return new ArrayTest<IntArray>() {
  // @Override
  // public List<IntArray> getTestData() {
//        //@formatter:off
//        return java.util.Arrays.asList(
//            getArrayFactory().newIntVector(1,2,3,4),
//
//            getArrayFactory().newIntVector(1,2,3,4,5,6,7,8).reshape(2,4),
//            getArrayFactory().newIntArray(2,4),
//
//            getArrayFactory().newIntArray(2,1,2)
//        );
//        //@formatter:on
  // }
  //
  // @Override
  // public void test(List<IntArray> actual) {
  // for (int i = 0; i < actual.get(0).size(); i++) {
  // assertEquals(0, actual.get(0).get(i));
  // }
  // }
  // };
  // }
  //
  // @Override
  // protected ArrayTest<IntArray> createAssignTest() {
  // return new ArrayTest<IntArray>() {
  // @Override
  // public List<IntArray> getTestData() {
//        //@formatter:off
//        return Arrays.asList(
//            getArrayFactory().newIntArray(3,3,3),
//            getArrayFactory().newIntVector(10),
//
//            getArrayFactory().newIntArray(1,3,4),
//            getArrayFactory().newIntVector(1,2,3,4)
//        );
//        //@formatter:on
  // }
  //
  // @Override
  // public void test(List<IntArray> actual) {
  // IntArray first = actual.get(0);
  // assertArrayEquals(new int[] {3, 3, 3}, first.getShape());
  // for (int i = 0; i < first.size(); i++) {
  // assertEquals(10, first.get(10));
  // }
  //
  // IntArray second = actual.get(1);
  // assertArrayEquals(new int[] {1, 3, 4}, second.getShape());
  // for (int i = 0; i < second.vectors(2); i++) {
  // IntArray vector = second.getVector(2, i);
  // assertEquals(vector, getArrayFactory().newIntVector(1, 2, 3, 4));
  // }
  // }
  // };
  // }

}

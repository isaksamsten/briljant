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

import org.junit.Test;

/**
 * @author Isak Karlsson
 */
public abstract class AbstractDoubleArrayTest extends AbstractBaseArrayTest<DoubleArray>
    implements ArrayBackendTest {

  protected static final double EPSILON = 1e-10;

  protected static class DoubleArrayTest extends ArrayTest<DoubleArray> {
    @Override
    void assertEqual(DoubleArray actual, DoubleArray expected) {
      ArrayAssert.assertArrayEquals(actual, expected, EPSILON);
    }

  }

  @Override
  protected ArrayTest<DoubleArray> createSetSingleIndexTest() {
    ArrayTest<DoubleArray> test = new DoubleArrayTest();
    //@formatter:off
    test.addTestCase(
        "to", getArrayFactory().newDoubleArray(5),
        "from", getArrayFactory().newDoubleVector(1,2,3,4,5),
        getArrayFactory().newDoubleVector(0,4,0,0,0)
    );

    test.addTestCase(
        "to", getArrayFactory().newDoubleArray(6).reshape(2,3),
        "from", getArrayFactory().newDoubleVector(1,2,3,4,5,6).reshape(3,2),
        getArrayFactory().newDoubleVector(0,4,0,0,0,0).reshape(2,3)
    );
    //@formatter:on

    return test;
  }

  @Override
  protected ArrayTest<DoubleArray> createSetMatrixIndexTest() {
    ArrayTest<DoubleArray> test = new DoubleArrayTest();
    //@formatter:off
    test.addTestCase(
        "to", getArrayFactory().newDoubleArray(4, 2),
        "from", getArrayFactory().newDoubleVector(1,2,3,4,5,6,7,8,9).reshape(3,3),
        getArrayFactory().newDoubleVector(0,0,0,0,0,9,0,0).reshape(4,2)
    );
    //@formatter:off
    return test;
  }

  @Override
  protected ArrayTest<DoubleArray> createAssignTest() {
    ArrayTest<DoubleArray> test = new DoubleArrayTest();
    DoubleArray array_3x2_to = getArrayFactory().newDoubleArray(3,2);
    DoubleArray array_3x2_from = range(3 * 2).reshape(3, 2);
    DoubleArray array_3x2_expected = array_3x2_from.copy();

    DoubleArray array_2x2x4_to = getArrayFactory().newDoubleArray(2, 2, 4);
    DoubleArray array_2x4_from = range(2 * 4).reshape(2, 4);
    DoubleArray array_2x2x4_expected = getArrayFactory().newDoubleVector(
        //@formatter:off
        1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 6, 6, 7, 7, 8, 8
        //@formatter:on
    ).reshape(2, 2, 4);

    DoubleArray array_2x3_to = getArrayFactory().newDoubleArray(3, 2).transpose();
    DoubleArray array_3_from = range(3);
    DoubleArray array_2x3_expected = getArrayFactory().newDoubleVector(
        //@formatter:off
        1,1,2,2,3,3
        //@formatter:on
    ).reshape(2, 3);

    test.addTestCase("to", array_3x2_to, "from", array_3x2_from, array_3x2_expected);
    test.addTestCase("to", array_2x2x4_to, "from", array_2x4_from, array_2x2x4_expected);
    test.addTestCase("to", array_2x3_to, "from", array_3_from, array_2x3_expected);

    return test;
  }

  @Override
  protected ArrayTest<DoubleArray> createForEachTest() {
    ArrayTest<DoubleArray> tests = new DoubleArrayTest();

    TestCase<DoubleArray> test3x3 = new TestCase<>();
    DoubleArray array_3x3 =
        getArrayFactory().newDoubleVector(1, 2, 3, 1, 2, 3, 1, 2, 3).reshape(3, 3);
    DoubleArray array_3x1_expected_along_dim0 = getArrayFactory().newDoubleVector(1, 2, 3);
    test3x3.setActual(array_3x3);
    test3x3.setExpected(array_3x1_expected_along_dim0);
    test3x3.setPayload("dim", 0);
    tests.addTestCase(test3x3);

    return tests;
  }

  @Override
  protected ArrayTest<DoubleArray> createSetColumnTest() {
    ArrayTest<DoubleArray> test = new DoubleArrayTest();

    TestCase<DoubleArray> case_1 = new TestCase<>();
    case_1.setActual("empty", getArrayFactory().newDoubleArray(3, 3));
    case_1.setPayload("columns", java.util.Arrays.asList(0, 1, 2));
    case_1.setActual(0, range(3));
    case_1.setActual(1, range(3));
    case_1.setActual(2, getArrayFactory().newDoubleVector(10));
    case_1
        .setExpected(getArrayFactory().newDoubleVector(1, 2, 3, 1, 2, 3, 10, 10, 10).reshape(3, 3));

    test.addTestCase(case_1);

    return test;
  }

  @Override
  protected ArrayTest<DoubleArray> createGetColumnTest() {
    ArrayTest<DoubleArray> test = new DoubleArrayTest();

    TestCase<DoubleArray> case_1 = new TestCase<>();
    case_1.setActual(getArrayFactory().newDoubleVector(1, 1, 1, 2, 2, 2, 3, 3, 3).reshape(3, 3));
    case_1.setExpected(0, getArrayFactory().newDoubleVector(1, 1, 1).reshape(3, 1));
    case_1.setExpected(1, getArrayFactory().newDoubleVector(2, 2, 2).reshape(3, 1));
    case_1.setExpected(2, getArrayFactory().newDoubleVector(3, 3, 3).reshape(3, 1));
    test.addTestCase(case_1);

    return test;
  }

  @Override
  protected ArrayTest<DoubleArray> createReverseTest() {
    ArrayTest<DoubleArray> test = new DoubleArrayTest();
    TestCase<DoubleArray> vector = new TestCase<>();
    vector.setActual(getArrayFactory().newDoubleVector(1, 2, 3, 4));
    vector.setExpected(getArrayFactory().newDoubleVector(4, 3, 2, 1));
    test.addTestCase(vector);

    TestCase<DoubleArray> matrix = new TestCase<>();
    matrix.setActual(getArrayFactory().newDoubleMatrix(new double[][] {
      //@formatter:off
        {0, 3, 6},
        {1, 4, 7},
        {2, 5, 8}
      //@formatter:on
    }).reshape(3, 3));
    matrix.setExpected(getArrayFactory().newDoubleMatrix(new double[][] {
        //@formatter:off
        {2, 5, 8},
        {1, 4, 7},
        {0, 3, 6}
        //@formatter:on
    }));
    test.addTestCase(matrix);

    return test;
  }

  protected DoubleArray range(int size) {
    DoubleArray array = getArrayFactory().newDoubleArray(size);
    for (int i = 0; i < size; i++) {
      array.set(i, i + 1);
    }
    return array;
  }

  @Test
  public void testMatrixMultiplication() throws Exception {
    DoubleArray a = getArrayFactory().newDoubleMatrix(new double[][] {{1, 2, 3}, {1, 2, 3}});
    DoubleArray b = getArrayFactory().newDoubleMatrix(new double[][] {{1, 1}, {2, 2}, {3, 3}});

    DoubleArray expected =
        getArrayFactory().newDoubleMatrix(new double[][] {{2, 4, 6}, {4, 8, 12}, {6, 12, 18}});

    ArrayAssert.assertArrayEquals(expected, Arrays.dot(b, a), EPSILON);
    ArrayAssert.assertArrayEquals(Arrays.times(expected, 2), Arrays.dot(2, b, a), EPSILON);
    ArrayAssert.assertArrayEquals(expected,
        Arrays.dot(ArrayOperation.TRANSPOSE, ArrayOperation.KEEP, a, a), EPSILON);
    ArrayAssert.assertArrayEquals(Arrays.times(expected, 2),
        Arrays.dot(ArrayOperation.TRANSPOSE, ArrayOperation.KEEP, a, 2, a), EPSILON);
  }

  @Test
  public void testTransposeAndMatrixMultiply() throws Exception {
    DoubleArray a = getArrayFactory().newDoubleMatrix(new double[][] {{1, 2, 3}, {1, 2, 3}});
    DoubleArray expected =
        getArrayFactory().newDoubleMatrix(new double[][] {{2, 4, 6}, {4, 8, 12}, {6, 12, 18}});

    ArrayAssert.assertArrayEquals(expected, Arrays.dot(a.transpose(), a), EPSILON);
    ArrayAssert.assertArrayEquals(expected,
        Arrays.dot(ArrayOperation.TRANSPOSE, ArrayOperation.KEEP, a, a), EPSILON);
  }

  @Test
  public void testGetRowAndColumnVectorMatrixMultiply() throws Exception {
    DoubleArray a =
        getArrayFactory().newDoubleMatrix(new double[][] {{2, 4, 6}, {4, 8, 12}, {6, 12, 18}});

    DoubleArray[] expected = new DoubleArray[] {
        getArrayFactory().newDoubleMatrix(new double[][] {{4, 8, 12}, {8, 16, 24}, {12, 24, 36}}),

        getArrayFactory()
            .newDoubleMatrix(new double[][] {{16, 32, 48}, {32, 64, 96}, {48, 96, 144}}),

        getArrayFactory()
            .newDoubleMatrix(new double[][] {{36, 72, 108}, {72, 144, 216}, {108, 216, 324}})};

    for (int i = 0; i < expected.length; i++) {
      DoubleArray r = a.getRow(i);
      DoubleArray c = a.getColumn(i);
      ArrayAssert.assertArrayEquals(expected[i], Arrays.dot(c, r), EPSILON);
    }
  }

  @Test
  public void testSelectMatrixMultiply() throws Exception {
    DoubleArray a = getArrayFactory().newDoubleArray(2, 3, 2);
    a.select(0).assign(getArrayFactory().newDoubleMatrix(new double[][] {{1, 1}, {2, 2}, {3, 3}}));
    a.select(1)
        .assign(getArrayFactory().newDoubleMatrix(new double[][] {{2, 8}, {3, 2}, {14, 21}}));

    DoubleArray[] expected = new DoubleArray[] {
        // a[0].T * a[0]
        getArrayFactory().newDoubleMatrix(new double[][] {{14, 14}, {14, 14}}),

        // a[0].T * a[1]
        getArrayFactory().newDoubleMatrix(new double[][] {{50, 75}, {50, 75}}),

        // a[1].T * a[0]
        getArrayFactory().newDoubleMatrix(new double[][] {{50, 50}, {75, 75}}),

        // a[1].T * a[1]
        getArrayFactory().newDoubleMatrix(new double[][] {{209, 316}, {316, 509}}),

        // a[0] * a[0].T
        getArrayFactory().newDoubleMatrix(new double[][] {{2, 4, 6}, {4, 8, 12}, {6, 12, 18}}),

        // a[0] * a[1].T
        getArrayFactory()
            .newDoubleMatrix(new double[][] {{10, 5, 35}, {20, 10, 70}, {30, 15, 105}}),

        // a[1] * a[0].T
        getArrayFactory()
            .newDoubleMatrix(new double[][] {{10, 20, 30}, {5, 10, 15}, {35, 70, 105}}),

        // a[1] * a[1].T
        getArrayFactory()
            .newDoubleMatrix(new double[][] {{68, 22, 196}, {22, 13, 84}, {196, 84, 637}})};

    DoubleArray a0 = a.select(0);
    DoubleArray a1 = a.select(1);

    // a[0].T * a[0]
    ArrayAssert.assertArrayEquals(expected[0], Arrays.dot(a0.transpose(), a0), EPSILON);
    ArrayAssert.assertArrayEquals(expected[0],
        Arrays.dot(ArrayOperation.TRANSPOSE, ArrayOperation.KEEP, a0, a0), EPSILON);

    // a[0].T * a[1]
    ArrayAssert.assertArrayEquals(expected[1], Arrays.dot(a0.transpose(), a1), EPSILON);
    ArrayAssert.assertArrayEquals(expected[1],
        Arrays.dot(ArrayOperation.TRANSPOSE, ArrayOperation.KEEP, a0, a1), EPSILON);

    // a[1].T * a[0]
    ArrayAssert.assertArrayEquals(expected[2], Arrays.dot(a1.transpose(), a0), EPSILON);
    ArrayAssert.assertArrayEquals(expected[2],
        Arrays.dot(ArrayOperation.TRANSPOSE, ArrayOperation.KEEP, a1, a0), EPSILON);

    // a[1].T * a[1]
    ArrayAssert.assertArrayEquals(expected[3], Arrays.dot(a1.transpose(), a1), EPSILON);
    ArrayAssert.assertArrayEquals(expected[3],
        Arrays.dot(ArrayOperation.TRANSPOSE, ArrayOperation.KEEP, a1, a1), EPSILON);

    // a[0] * a[0].T
    ArrayAssert.assertArrayEquals(expected[4], Arrays.dot(a0, a0.transpose()), EPSILON);
    ArrayAssert.assertArrayEquals(expected[4],
        Arrays.dot(ArrayOperation.KEEP, ArrayOperation.TRANSPOSE, a0, a0), EPSILON);

    // a[0] * a[1].T
    ArrayAssert.assertArrayEquals(expected[5], Arrays.dot(a0, a1.transpose()), EPSILON);
    ArrayAssert.assertArrayEquals(expected[5],
        Arrays.dot(ArrayOperation.KEEP, ArrayOperation.TRANSPOSE, a0, a1), EPSILON);

    // a[1] * a[0].T
    ArrayAssert.assertArrayEquals(expected[6], Arrays.dot(a1, a0.transpose()), EPSILON);
    ArrayAssert.assertArrayEquals(expected[6],
        Arrays.dot(ArrayOperation.KEEP, ArrayOperation.TRANSPOSE, a1, a0), EPSILON);

    // a[1] * a[1].T
    ArrayAssert.assertArrayEquals(expected[7], Arrays.dot(a1, a1.transpose()), EPSILON);
    ArrayAssert.assertArrayEquals(expected[7],
        Arrays.dot(ArrayOperation.KEEP, ArrayOperation.TRANSPOSE, a1, a1), EPSILON);
  }

}

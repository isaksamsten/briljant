/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Isak Karlsson
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.briljantframework.array.netlib;

import org.briljantframework.array.ArrayAssert;
import org.briljantframework.array.DoubleArray;
import org.briljantframework.array.Op;
import org.briljantframework.array.api.ArrayFactory;
import org.junit.Test;

public class NetlibDoubleArrayTest {

  protected static final double EPSILON = 1e-10;
  NetlibArrayBackend backend = new NetlibArrayBackend();
  ArrayFactory bj = backend.getArrayFactory();

  @Test
  public void testMatrixMultiplication() throws Exception {
    DoubleArray a = bj.array(new double[][]{
        {1, 2, 3},
        {1, 2, 3}
    });
    DoubleArray b = bj.array(new double[][]{
        {1, 1},
        {2, 2},
        {3, 3}
    });

    DoubleArray expected = bj.array(new double[][]{
        {2, 4, 6},
        {4, 8, 12},
        {6, 12, 18}
    });

    ArrayAssert.assertArrayEquals(expected, b.mmul(a), EPSILON);
    ArrayAssert.assertArrayEquals(expected.mul(2), b.mmul(2, a), EPSILON);
    ArrayAssert.assertArrayEquals(expected, a.mmul(Op.TRANSPOSE, a, Op.KEEP), EPSILON);
    ArrayAssert.assertArrayEquals(expected.mul(2), a.mmul(2, Op.TRANSPOSE, a, Op.KEEP), EPSILON);
  }

  @Test
  public void testTransposeAndMatrixMultiply() throws Exception {
    DoubleArray a = bj.array(new double[][]{
        {1, 2, 3},
        {1, 2, 3}
    });
    DoubleArray expected = bj.array(new double[][]{
        {2, 4, 6},
        {4, 8, 12},
        {6, 12, 18}
    });

    ArrayAssert.assertArrayEquals(expected, a.transpose().mmul(a), EPSILON);
    ArrayAssert.assertArrayEquals(expected, a.mmul(Op.TRANSPOSE, a, Op.KEEP), EPSILON);
  }

  @Test
  public void testGetRowAndColumnVectorMatrixMultiply() throws Exception {
    DoubleArray a = bj.array(new double[][]{
        {2, 4, 6},
        {4, 8, 12},
        {6, 12, 18}
    });

    DoubleArray[] expected = new DoubleArray[]{
        bj.array(new double[][]{
            {4, 8, 12},
            {8, 16, 24},
            {12, 24, 36}
        }),

        bj.array(new double[][]{
            {16, 32, 48},
            {32, 64, 96},
            {48, 96, 144}
        }),

        bj.array(new double[][]{
            {36, 72, 108},
            {72, 144, 216},
            {108, 216, 324}
        })
    };

    for (int i = 0; i < expected.length; i++) {
      DoubleArray r = a.getRow(i);
      DoubleArray c = a.getColumn(i);
      ArrayAssert.assertArrayEquals(expected[i], c.mmul(r), EPSILON);
    }
  }

  @Test
  public void testSelectMatrixMultiply() throws Exception {
    DoubleArray a = bj.doubleArray(2, 3, 2);
    a.select(0).assign(bj.array(new double[][]{
        {1, 1},
        {2, 2},
        {3, 3}
    }));
    a.select(1).assign(bj.array(new double[][]{
        {2, 8},
        {3, 2},
        {14, 21}
    }));

    DoubleArray[] expected = new DoubleArray[]{
        // a[0].T * a[0]
        bj.array(new double[][]{
            {14, 14},
            {14, 14}
        }),

        // a[0].T * a[1]
        bj.array(new double[][]{
            {50, 75},
            {50, 75}
        }),

        // a[1].T * a[0]
        bj.array(new double[][]{
            {50, 50},
            {75, 75}
        }),

        // a[1].T * a[1]
        bj.array(new double[][]{
            {209, 316},
            {316, 509}
        }),

        // a[0] * a[0].T
        bj.array(new double[][]{
            {2, 4, 6},
            {4, 8, 12},
            {6, 12, 18}
        }),

        // a[0] * a[1].T
        bj.array(new double[][]{
            {10, 5, 35},
            {20, 10, 70},
            {30, 15, 105}
        }),

        // a[1] * a[0].T
        bj.array(new double[][]{
            {10, 20, 30},
            {5, 10, 15},
            {35, 70, 105}
        }),

        // a[1] * a[1].T
        bj.array(new double[][]{
            {68, 22, 196},
            {22, 13, 84},
            {196, 84, 637}
        })
    };

    DoubleArray a0 = a.select(0);
    DoubleArray a1 = a.select(1);

    // a[0].T * a[0]
    ArrayAssert.assertArrayEquals(expected[0], a0.transpose().mmul(a0), EPSILON);
    ArrayAssert.assertArrayEquals(expected[0], a0.mmul(Op.TRANSPOSE, a0, Op.KEEP), EPSILON);

    // a[0].T * a[1]
    ArrayAssert.assertArrayEquals(expected[1], a0.transpose().mmul(a1), EPSILON);
    ArrayAssert.assertArrayEquals(expected[1], a0.mmul(Op.TRANSPOSE, a1, Op.KEEP), EPSILON);

    // a[1].T * a[0]
    ArrayAssert.assertArrayEquals(expected[2], a1.transpose().mmul(a0), EPSILON);
    ArrayAssert.assertArrayEquals(expected[2], a1.mmul(Op.TRANSPOSE, a0, Op.KEEP), EPSILON);

    // a[1].T * a[1]
    ArrayAssert.assertArrayEquals(expected[3], a1.transpose().mmul(a1), EPSILON);
    ArrayAssert.assertArrayEquals(expected[3], a1.mmul(Op.TRANSPOSE, a1, Op.KEEP), EPSILON);

    // a[0] * a[0].T
    ArrayAssert.assertArrayEquals(expected[4], a0.mmul(a0.transpose()), EPSILON);
    ArrayAssert.assertArrayEquals(expected[4], a0.mmul(Op.KEEP, a0, Op.TRANSPOSE), EPSILON);

    // a[0] * a[1].T
    ArrayAssert.assertArrayEquals(expected[5], a0.mmul(a1.transpose()), EPSILON);
    ArrayAssert.assertArrayEquals(expected[5], a0.mmul(Op.KEEP, a1, Op.TRANSPOSE), EPSILON);

    // a[1] * a[0].T
    ArrayAssert.assertArrayEquals(expected[6], a1.mmul(a0.transpose()), EPSILON);
    ArrayAssert.assertArrayEquals(expected[6], a1.mmul(Op.KEEP, a0, Op.TRANSPOSE), EPSILON);

    // a[1] * a[1].T
    ArrayAssert.assertArrayEquals(expected[7], a1.mmul(a1.transpose()), EPSILON);
    ArrayAssert.assertArrayEquals(expected[7], a1.mmul(Op.KEEP, a1, Op.TRANSPOSE), EPSILON);
  }
}
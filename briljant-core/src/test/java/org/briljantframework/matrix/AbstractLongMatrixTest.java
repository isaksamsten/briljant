package org.briljantframework.matrix;

import org.briljantframework.complex.Complex;
import org.briljantframework.matrix.api.MatrixFactory;
import org.briljantframework.matrix.netlib.NetlibMatrixBackend;
import org.junit.Test;

import java.util.LongSummaryStatistics;

import static java.util.Arrays.asList;
import static org.briljantframework.matrix.MatrixAssert.assertMatrixEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class AbstractLongMatrixTest {

  private final MatrixFactory bj = new NetlibMatrixBackend().getMatrixFactory();

  @Test
  public void testRsub2() throws Exception {
    LongMatrix x = bj.matrix(new long[]{1L, 2, 3});
    LongMatrix y = bj.longMatrix(3, 3).assign(3);
    LongMatrix z = y.rsub(x, Dim.R);
    MatrixAssert.assertValuesEquals(z.getRow(0), bj.matrix(new long[]{-2L, -1, 0}));
    MatrixAssert.assertValuesEquals(z.getRow(1), bj.matrix(new long[]{-2L, -1, 0}));
    MatrixAssert.assertValuesEquals(z.getRow(2), bj.matrix(new long[]{-2L, -1, 0}));

    z = y.rsub(x, Dim.C);
    MatrixAssert.assertValuesEquals(z.getColumn(0), bj.matrix(new long[]{-2L, -1, 0}));
    MatrixAssert.assertValuesEquals(z.getColumn(1), bj.matrix(new long[]{-2L, -1, 0}));
    MatrixAssert.assertValuesEquals(z.getColumn(2), bj.matrix(new long[]{-2L, -1, 0}));
  }

  @Test
  public void testAssign() throws Exception {
    LongMatrix m = bj.longMatrix(3, 3);
    m.assign(3);
    assertMatrixEquals(m, 3);
  }

  @Test
  public void testAssign1() throws Exception {
    LongMatrix m = bj.longMatrix(3, 3);
    m.assign(() -> 3);
    assertMatrixEquals(m, 3);
  }

  @Test
  public void testAssign2() throws Exception {
    LongMatrix m = bj.longMatrix(3, 3);
    m.assign(3).update(x -> x * 2);
    assertMatrixEquals(m, 6);
  }

  @Test
  public void testAssign3() throws Exception {
    DoubleMatrix d = bj.doubleMatrix(3, 3).assign(3);
    LongMatrix i = bj.longMatrix(3, 3).assign(d, x -> (int) x);
    assertMatrixEquals(i, 3);
  }

  @Test
  public void testAssign4() throws Exception {
    ComplexMatrix c = bj.complexMatrix(3, 3).assign(Complex.valueOf(3));
    LongMatrix i = bj.longMatrix(3, 3).assign(c, Complex::intValue);
    assertMatrixEquals(i, 3);
  }

  @Test
  public void testAssign5() throws Exception {
    LongMatrix l = bj.longMatrix(3, 3).assign(3L);
    LongMatrix i = bj.longMatrix(3, 3).assign(l, x -> (int) x);
    assertMatrixEquals(i, 3);
  }

  @Test
  public void testAssign6() throws Exception {
    LongMatrix i = bj.longMatrix(3, 3).assign(bj.longMatrix(3, 3).assign(3));
    assertMatrixEquals(i, 3);
  }

  @Test
  public void testAssign7() throws Exception {
    LongMatrix i = bj.longMatrix(3, 3).assign(bj.longMatrix(3, 3).assign(3), x -> x * 2);
    assertMatrixEquals(i, 6);
  }

  @Test
  public void testAssign8() throws Exception {
    LongMatrix x = bj.longMatrix(3, 3).assign(2);
    LongMatrix d = bj.longMatrix(3, 3).assign(5);
    x.assign(d, Long::sum);
    assertMatrixEquals(x, 7);
  }

  @Test
  public void testMap() throws Exception {
    LongMatrix i = bj.longMatrix(3, 3).assign(3);
    LongMatrix m = i.map(Long::bitCount);
    assertMatrixEquals(m, 2);
  }

  @Test
  public void testMapToInt() throws Exception {
    LongMatrix i = bj.longMatrix(3, 3).assign(Integer.MAX_VALUE + 10L);
    IntMatrix l = i.mapToInt(x -> (int) (x - Integer.MAX_VALUE));
    MatrixAssert.assertMatrixEquals(10, l);
  }

  @Test
  public void testMapToDouble() throws Exception {
    DoubleMatrix i = bj.longMatrix(3, 3).assign(3).mapToDouble(Math::sqrt);
    assertMatrixEquals(Math.sqrt(3), i, 0.0001);
  }

  @Test
  public void testMapToComplex() throws Exception {
    ComplexMatrix i = bj.longMatrix(3, 3).assign(-3).mapToComplex(Complex::sqrt);
    assertMatrixEquals(i, Complex.sqrt(-3));
  }

  @Test
  public void testFilter() throws Exception {
    LongMatrix i = bj.matrix(new long[]{0L, 1, 2, 3, 4, 5, 6}).filter(x -> x > 3);
    MatrixAssert.assertValuesEquals(i, bj.matrix(new long[]{4L, 5, 6}));
  }

  @Test
  public void testSatisfies() throws Exception {
    BitMatrix i = bj.matrix(new long[]{0L, 1, 2, 3, 4, 5}).satisfies(x -> x >= 3);
    MatrixAssert
        .assertValuesEquals(bj.matrix(
            new boolean[]{false, false, false, true, true, true}), i);
  }

  @Test
  public void testSatisfies1() throws Exception {
    LongMatrix x = bj.longMatrix(3, 3);
    LongMatrix y = bj.longMatrix(3, 3).assign(3);
    BitMatrix z = x.satisfies(y, (a, b) -> a < b);
    assertMatrixEquals(z, true);
  }

  @Test
  public void testReduce() throws Exception {
    LongMatrix x = bj.longMatrix(3, 3).assign(3);
    long sum = x.reduce(0, Long::sum);
    assertEquals(3 * 9, sum);
  }

  @Test
  public void testReduce1() throws Exception {
    LongMatrix x = bj.longMatrix(3, 3).assign(3);
    long squaredSum = x.reduce(0, Long::sum, i -> i * 2);
    assertEquals(3 * 2 * 9, squaredSum);
  }

  @Test
  public void testReduceColumns() throws Exception {
    LongMatrix x = bj.longMatrix(3, 4).assign(3).reduceColumns(y -> y.reduce(0, Long::sum));
    assertEquals(4, x.columns());
    assertEquals(1, x.rows());
    assertMatrixEquals(x, 3 * 3);
  }

  @Test
  public void testReduceRows() throws Exception {
    LongMatrix x = bj.longMatrix(4, 3).assign(3).reduceRows(y -> y.reduce(0, Long::sum));
    assertEquals(4, x.rows());
    assertEquals(1, x.columns());
    assertMatrixEquals(x, 3 * 3);
  }

  @Test
  public void testReshape() throws Exception {
    LongMatrix x = bj.matrix(new long[]{1L, 2, 3, 4, 5, 6}).reshape(2, 3);
    assertEquals(2, x.rows());
    assertEquals(3, x.columns());
  }

  @Test
  public void testGet() throws Exception {
    LongMatrix x = bj.matrix(new long[]{0L, 1, 2, 3, 4, 5}).reshape(3, 2);
    assertEquals(0, x.get(0));
    assertEquals(5, x.get(5));
  }

  @Test
  public void testGet1() throws Exception {
    LongMatrix x = bj.matrix(new long[]{0L, 1, 2, 3, 4, 5}).reshape(3, 2);
    assertEquals(0, x.get(0, 0));
    assertEquals(3, x.get(0, 1));
    assertEquals(4, x.get(1, 1));
  }

  @Test
  public void testSet() throws Exception {
    LongMatrix x = bj.longMatrix(3, 3);
    x.set(0, 0, 1);
    x.set(0, 1, 2);
    x.set(1, 1, 3);

    assertEquals(1, x.get(0, 0));
    assertEquals(2, x.get(0, 1));
    assertEquals(3, x.get(1, 1));
  }

  // @Test
  // public void testAddTo() throws Exception {
  // LongMatrix x = newMatrix(1, 1, 1, 1);
  // x.addTo(0, 10);
  // assertEquals(11, x.get(0));
  // }
  //
  // @Test
  // public void testAddTo1() throws Exception {
  // LongMatrix x = newMatrix(1, 1, 1, 1).reshape(2, 2);
  // x.addTo(0, 0, 10);
  // x.addTo(0, 1, 10);
  // assertEquals(11, x.get(0, 0));
  // assertEquals(11, x.get(0, 1));
  // }
  //
  // @Test
  // public void testUpdate() throws Exception {
  // LongMatrix x = newMatrix(1, 1, 1, 1).reshape(2, 2);
  // x.update(0, 0, i -> i * 3);
  // assertEquals(3, x.get(0, 0));
  // }
  //
  // @Test
  // public void testUpdate1() throws Exception {
  // LongMatrix x = newMatrix(1, 1, 1, 1);
  // x.update(0, i -> i * 3);
  // assertEquals(3, x.get(0));
  // }

  @Test
  public void testSet1() throws Exception {
    LongMatrix x = bj.matrix(new long[]{0L, 1, 2, 3});
    assertEquals(0, x.get(0));
    assertEquals(1, x.get(1));
    assertEquals(2, x.get(2));
    assertEquals(3, x.get(3));
  }

  @Test
  public void testGetRowView() throws Exception {
    LongMatrix x = bj.matrix(new long[]{1L, 2, 3, 1, 2, 3, 1, 2, 3}).reshape(3, 3);
    assertMatrixEquals(x.getRow(0), 1);
    assertMatrixEquals(x.getRow(1), 2);
    assertMatrixEquals(x.getRow(2), 3);
  }

  @Test
  public void testGetColumnView() throws Exception {
    LongMatrix x = bj.matrix(new long[]{1L, 1, 1, 2, 2, 2, 3, 3, 3}).reshape(3, 3);
    assertMatrixEquals(x.getColumn(0), 1);
    assertMatrixEquals(x.getColumn(1), 2);
    assertMatrixEquals(x.getColumn(2), 3);
  }

  @Test
  public void testGetDiagonalView() throws Exception {

  }

  @Test
  public void testGetView() throws Exception {
    LongMatrix x = bj.matrix(new long[]{1L, 1, 1, 1, 2, 2}).reshape(2, 3);
    assertMatrixEquals(x.getView(0, 0, 2, 2), 1);
  }

  @Test
  public void testTranspose() throws Exception {
    LongMatrix x = bj.matrix(new long[]{1L, 2, 3, 1, 2, 3}).reshape(2, 3).transpose();
    assertEquals(3, x.rows());
    assertEquals(2, x.columns());
    assertEquals(1, x.get(0, 0));
    assertEquals(3, x.get(2, 1));
  }

  @Test
  public void testCopy() throws Exception {
    LongMatrix x = bj.matrix(new long[]{1L, 1, 1, 1});
    LongMatrix y = x.copy();
    x.set(0, 1000);
    assertEquals(1, y.get(0));
  }

  @Test
  public void testNewEmptyMatrix() throws Exception {
    assertNotNull(bj.longMatrix(2, 2).newEmptyArray(2, 2));
  }

  @Test
  public void testNewEmptyVector() throws Exception {
    LongMatrix x = bj.longMatrix(2, 2).newEmptyArray(2);
    assertNotNull(x);
    assertEquals(2, x.rows());
    assertEquals(1, x.columns());
  }

  @Test
  public void testMmul() throws Exception {
    LongMatrix x = bj.matrix(new long[]{1L, 2, 3, 4, 5, 6}).reshape(3, 2);
    LongMatrix y = bj.matrix(new long[]{1L, 2, 3, 4, 5, 6}).reshape(2, 3);

    LongMatrix z = y.mmul(x);
    LongMatrix za = bj.matrix(new long[]{22L, 28, 49, 64}).reshape(2, 2);
    assertMatrixEquals(za, z);

    z = x.mmul(y);
    za = bj.matrix(new long[]{9L, 12, 15, 19, 26, 33, 29, 40, 51}).reshape(3, 3);
    assertMatrixEquals(za, z);
  }

  @Test
  public void testMmul1() throws Exception {
    LongMatrix x = bj.matrix(new long[]{1L, 2, 3, 4, 5, 6}).reshape(3, 2);
    LongMatrix y = bj.matrix(new long[]{1L, 2, 3, 4, 5, 6}).reshape(2, 3);

    LongMatrix z = y.mmul(2, x);
    LongMatrix za = bj.matrix(new long[]{44L, 56, 98, 128}).reshape(2, 2);
    assertMatrixEquals(za, z);

    z = x.mmul(4, y);
    za = bj.matrix(new long[]{36L, 48, 60, 76, 104, 132, 116, 160, 204}).reshape(3, 3);
    assertMatrixEquals(za, z);
  }

  @Test
  public void testMmul2() throws Exception {
    LongMatrix x = bj.matrix(new long[]{1L, 2, 3, 4, 5, 6}).reshape(3, 2);
    LongMatrix y = bj.matrix(new long[]{1L, 2, 3, 4, 5, 6}).reshape(3, 2);

    LongMatrix z = y.mmul(T.YES, x, T.NO);
    LongMatrix za = bj.matrix(new long[]{14L, 32, 32, 77}).reshape(2, 2);
    assertMatrixEquals(za, z);

    z = x.mmul(T.NO, y, T.YES);
    za = bj.matrix(new long[]{17, 22, 27, 22L, 29, 36, 27, 36, 45}).reshape(3, 3);
    assertMatrixEquals(za, z);
  }

  @Test
  public void testMmul3() throws Exception {
    LongMatrix x = bj.matrix(new long[]{1L, 2, 3, 4, 5, 6}).reshape(3, 2);
    LongMatrix y = bj.matrix(new long[]{1L, 2, 3, 4, 5, 6}).reshape(3, 2);
    LongMatrix z = y.mmul(2, T.YES, x, T.NO);
    LongMatrix za = bj.matrix(new long[]{28L, 64, 64, 154}).reshape(2, 2);
    assertMatrixEquals(za, z);

    z = x.mmul(2, T.NO, y, T.YES);
    za = bj.matrix(new long[]{34L, 44, 54, 44, 58, 72, 54, 72, 90}).reshape(3, 3);
    assertMatrixEquals(za, z);
  }

  @Test
  public void testMul() throws Exception {
    LongMatrix x = bj.longMatrix(3, 3).assign(3);
    LongMatrix z = x.mul(2);
    assertMatrixEquals(z, 6);
  }

  @Test
  public void testMul1() throws Exception {
    LongMatrix x = bj.longMatrix(3, 3).assign(3);
    LongMatrix y = bj.longMatrix(3, 3).assign(2);
    LongMatrix z = x.mul(y);
    assertMatrixEquals(z, 6);
  }

  @Test
  public void testMul2() throws Exception {
    LongMatrix x = bj.longMatrix(3, 3).assign(3);
    LongMatrix y = bj.longMatrix(3, 3).assign(2);
    LongMatrix z = x.mul(-1, y, -1);
    assertMatrixEquals(z, 6);
  }

  @Test
  public void testMul3() throws Exception {
    LongMatrix x = bj.matrix(new long[]{1L, 2, 3});
    LongMatrix y = bj.longMatrix(3, 3).assign(3);
    LongMatrix z = y.mul(x, Dim.R);
    assertMatrixEquals(z.getColumn(0), 3);
    assertMatrixEquals(z.getColumn(1), 6);
    assertMatrixEquals(z.getColumn(2), 9);

    z = y.mul(x, Dim.C);
    assertMatrixEquals(z.getRow(0), 3);
    assertMatrixEquals(z.getRow(1), 6);
    assertMatrixEquals(z.getRow(2), 9);
  }

  @Test
  public void testMul4() throws Exception {
    LongMatrix x = bj.matrix(new long[]{1L, 2, 3});
    LongMatrix y = bj.longMatrix(3, 3).assign(3);
    LongMatrix z = y.mul(1, x, -1, Dim.R);
    assertMatrixEquals(z.getColumn(0), -3);
    assertMatrixEquals(z.getColumn(1), -6);
    assertMatrixEquals(z.getColumn(2), -9);

    z = y.mul(1, x, -1, Dim.C);
    assertMatrixEquals(z.getRow(0), -3);
    assertMatrixEquals(z.getRow(1), -6);
    assertMatrixEquals(z.getRow(2), -9);

  }

  @Test
  public void testAdd() throws Exception {
    LongMatrix x = bj.longMatrix(3, 3).assign(2);
    assertMatrixEquals(x.add(3), 5);
  }

  @Test
  public void testAdd1() throws Exception {
    LongMatrix x = bj.longMatrix(3, 3).assign(2);
    LongMatrix y = bj.longMatrix(3, 3).assign(3);
    assertMatrixEquals(x.add(y), 5);
  }

  @Test
  public void testAdd2() throws Exception {
    LongMatrix x = bj.longMatrix(3, 3).assign(2);
    LongMatrix y = bj.longMatrix(3, 3).assign(3);
    assertMatrixEquals(x.add(1, y, -1), -1);
  }

  @Test
  public void testAdd3() throws Exception {
    LongMatrix x = bj.matrix(new long[]{1L, 2, 3});
    LongMatrix y = bj.longMatrix(3, 3).assign(3);
    LongMatrix z = y.add(x, Dim.R);
    MatrixAssert.assertValuesEquals(z.getRow(0), bj.matrix(new long[]{4L, 5, 6}));
    MatrixAssert.assertValuesEquals(z.getRow(1), bj.matrix(new long[]{4L, 5, 6}));
    MatrixAssert.assertValuesEquals(z.getRow(2), bj.matrix(new long[]{4L, 5, 6}));

    z = y.add(x, Dim.C);
    MatrixAssert.assertValuesEquals(z.getColumn(0), bj.matrix(new long[]{4L, 5, 6}));
    MatrixAssert.assertValuesEquals(z.getColumn(1), bj.matrix(new long[]{4L, 5, 6}));
    MatrixAssert.assertValuesEquals(z.getColumn(2), bj.matrix(new long[]{4L, 5, 6}));
  }

  @Test
  public void testAdd4() throws Exception {
    LongMatrix x = bj.matrix(new long[]{1L, 2, 3});
    LongMatrix y = bj.longMatrix(3, 3).assign(3);
    LongMatrix z = y.add(1, x, -1, Dim.R);
    MatrixAssert.assertValuesEquals(z.getRow(0), bj.matrix(new long[]{2L, 1, 0}));
    MatrixAssert.assertValuesEquals(z.getRow(1), bj.matrix(new long[]{2L, 1, 0}));
    MatrixAssert.assertValuesEquals(z.getRow(2), bj.matrix(new long[]{2L, 1, 0}));

    z = y.add(1, x, -1, Dim.C);
    MatrixAssert.assertValuesEquals(z.getColumn(0), bj.matrix(new long[]{2L, 1, 0}));
    MatrixAssert.assertValuesEquals(z.getColumn(1), bj.matrix(new long[]{2L, 1, 0}));
    MatrixAssert.assertValuesEquals(z.getColumn(2), bj.matrix(new long[]{2L, 1, 0}));
  }

  @Test
  public void testSub() throws Exception {
    LongMatrix x = bj.longMatrix(3, 3).assign(3);
    assertMatrixEquals(x.sub(2), 1);
  }

  @Test
  public void testSub1() throws Exception {
    LongMatrix x = bj.longMatrix(3, 3).assign(3);
    LongMatrix y = bj.longMatrix(3, 3).assign(2);
    assertMatrixEquals(x.sub(y), 1);
  }

  @Test
  public void testSub2() throws Exception {
    LongMatrix x = bj.longMatrix(3, 3).assign(3);
    LongMatrix y = bj.longMatrix(3, 3).assign(2);
    assertMatrixEquals(x.sub(1, y, -1), 5);
  }

  @Test
  public void testSub3() throws Exception {
    LongMatrix x = bj.matrix(new long[]{1L, 2, 3});
    LongMatrix y = bj.longMatrix(3, 3).assign(3);
    LongMatrix z = y.sub(x, Dim.R);
    MatrixAssert.assertValuesEquals(z.getRow(0), bj.matrix(new long[]{2L, 1, 0}));
    MatrixAssert.assertValuesEquals(z.getRow(1), bj.matrix(new long[]{2L, 1, 0}));
    MatrixAssert.assertValuesEquals(z.getRow(2), bj.matrix(new long[]{2L, 1, 0}));

    z = y.sub(x, Dim.C);
    MatrixAssert.assertValuesEquals(z.getColumn(0), bj.matrix(new long[]{2L, 1, 0}));
    MatrixAssert.assertValuesEquals(z.getColumn(1), bj.matrix(new long[]{2L, 1, 0}));
    MatrixAssert.assertValuesEquals(z.getColumn(2), bj.matrix(new long[]{2L, 1, 0}));
  }

  @Test
  public void testSub4() throws Exception {
    LongMatrix x = bj.matrix(new long[]{1L, 2, 3});
    LongMatrix y = bj.longMatrix(3, 3).assign(3);
    LongMatrix z = y.sub(1, x, -1, Dim.R);
    MatrixAssert.assertValuesEquals(z.getRow(0), bj.matrix(new long[]{4L, 5, 6}));
    MatrixAssert.assertValuesEquals(z.getRow(1), bj.matrix(new long[]{4L, 5, 6}));
    MatrixAssert.assertValuesEquals(z.getRow(2), bj.matrix(new long[]{4L, 5, 6}));

    z = y.sub(1, x, -1, Dim.C);
    MatrixAssert.assertValuesEquals(z.getColumn(0), bj.matrix(new long[]{4L, 5, 6}));
    MatrixAssert.assertValuesEquals(z.getColumn(1), bj.matrix(new long[]{4L, 5, 6}));
    MatrixAssert.assertValuesEquals(z.getColumn(2), bj.matrix(new long[]{4L, 5, 6}));
  }

  @Test
  public void testRsub() throws Exception {
    LongMatrix x = bj.longMatrix(3, 3).assign(2);
    LongMatrix y = x.rsub(3);
    assertMatrixEquals(y, 1);
  }

  @Test
  public void testRsub1() throws Exception {
    LongMatrix x = bj.matrix(new long[]{1L, 2, 3});
    LongMatrix y = bj.longMatrix(3, 3).assign(3);
    LongMatrix z = y.rsub(1, x, -1, Dim.R);
    MatrixAssert.assertValuesEquals(z.getRow(0), bj.matrix(new long[]{-4L, -5, -6}));
    MatrixAssert.assertValuesEquals(z.getRow(1), bj.matrix(new long[]{-4L, -5, -6}));
    MatrixAssert.assertValuesEquals(z.getRow(2), bj.matrix(new long[]{-4L, -5, -6}));

    z = y.rsub(1, x, -1, Dim.C);
    MatrixAssert.assertValuesEquals(z.getColumn(0), bj.matrix(new long[]{-4L, -5, -6}));
    MatrixAssert.assertValuesEquals(z.getColumn(1), bj.matrix(new long[]{-4L, -5, -6}));
    MatrixAssert.assertValuesEquals(z.getColumn(2), bj.matrix(new long[]{-4L, -5, -6}));
  }

  @Test
  public void testDiv() throws Exception {
    LongMatrix x = bj.longMatrix(3, 3).assign(4);
    LongMatrix y = x.div(2);
    assertMatrixEquals(y, 2);
  }

  @Test
  public void testDiv1() throws Exception {
    LongMatrix x = bj.longMatrix(3, 3).assign(4);
    LongMatrix y = bj.longMatrix(3, 3).assign(2);
    LongMatrix z = x.div(y);
    assertMatrixEquals(z, 2);
  }

  @Test
  public void testDiv2() throws Exception {
    LongMatrix x = bj.matrix(new long[]{1L, 2, 3});
    LongMatrix y = bj.longMatrix(3, 3).assign(6);
    LongMatrix z = y.div(x, Dim.R);

    MatrixAssert.assertValuesEquals(z.getRow(0), bj.matrix(new long[]{6L, 3, 2}));
    MatrixAssert.assertValuesEquals(z.getRow(1), bj.matrix(new long[]{6L, 3, 2}));
    MatrixAssert.assertValuesEquals(z.getRow(2), bj.matrix(new long[]{6L, 3, 2}));

    z = y.div(x, Dim.C);
    MatrixAssert.assertValuesEquals(z.getColumn(0), bj.matrix(new long[]{6L, 3, 2}));
    MatrixAssert.assertValuesEquals(z.getColumn(1), bj.matrix(new long[]{6L, 3, 2}));
    MatrixAssert.assertValuesEquals(z.getColumn(2), bj.matrix(new long[]{6L, 3, 2}));
  }

  @Test
  public void testDiv3() throws Exception {
    LongMatrix x = bj.matrix(new long[]{1L, 2, 3});
    LongMatrix y = bj.longMatrix(3, 3).assign(6);
    LongMatrix z = y.div(2, x, 1, Dim.R);

    MatrixAssert.assertValuesEquals(z.getRow(0), bj.matrix(new long[]{12L, 6, 4}));
    MatrixAssert.assertValuesEquals(z.getRow(1), bj.matrix(new long[]{12L, 6, 4}));
    MatrixAssert.assertValuesEquals(z.getRow(2), bj.matrix(new long[]{12L, 6, 4}));

    z = y.div(2, x, 1, Dim.C);
    MatrixAssert.assertValuesEquals(z.getColumn(0), bj.matrix(new long[]{12L, 6, 4}));
    MatrixAssert.assertValuesEquals(z.getColumn(1), bj.matrix(new long[]{12L, 6, 4}));
    MatrixAssert.assertValuesEquals(z.getColumn(2), bj.matrix(new long[]{12L, 6, 4}));
  }

  @Test
  public void testRdiv() throws Exception {
    LongMatrix x = bj.longMatrix(3, 3).assign(6);
    LongMatrix y = x.rdiv(12);
    assertMatrixEquals(y, 2);
  }

  @Test
  public void testRdiv1() throws Exception {
    LongMatrix x = bj.matrix(new long[]{12L, 12, 12});
    LongMatrix y = bj.longMatrix(3, 3).assign(6);
    LongMatrix z = y.rdiv(x, Dim.R);

    assertMatrixEquals(z.getRow(0), 2);
    assertMatrixEquals(z.getRow(1), 2);
    assertMatrixEquals(z.getRow(2), 2);

    z = y.rdiv(x, Dim.C);
    assertMatrixEquals(z.getColumn(0), 2);
    assertMatrixEquals(z.getColumn(1), 2);
    assertMatrixEquals(z.getColumn(2), 2);
  }

  @Test
  public void testRdiv2() throws Exception {
    LongMatrix x = bj.matrix(new long[]{6L, 6, 6});
    LongMatrix y = bj.longMatrix(3, 3).assign(6);
    LongMatrix z = y.rdiv(1, x, 2, Dim.R);

    assertMatrixEquals(z.getRow(0), 2);
    assertMatrixEquals(z.getRow(1), 2);
    assertMatrixEquals(z.getRow(2), 2);

    z = y.rdiv(1, x, 2, Dim.C);
    assertMatrixEquals(z.getColumn(0), 2);
    assertMatrixEquals(z.getColumn(1), 2);
    assertMatrixEquals(z.getColumn(2), 2);
  }

  @Test
  public void testNegate() throws Exception {
    LongMatrix x = bj.longMatrix(3, 3).assign(3).negate();
    assertMatrixEquals(x, -3);
  }

  @Test
  public void testSlice1() throws Exception {
    LongMatrix x = bj.matrix(new long[]{1L, 2, 3, 1, 2, 3}).reshape(3, 2);
    LongMatrix slice = x.slice(bj.range(3));
    MatrixAssert.assertValuesEquals(slice, bj.matrix(new long[]{1L, 2, 3}));
  }

  @Test
  public void testSlice2() throws Exception {
    LongMatrix x = bj.matrix(new long[]{1L, 2, 3, 1, 2, 3, 1, 2, 3}).reshape(3, 3);
    LongMatrix slice = x.slice(bj.range(2), Dim.R);
    assertEquals(2, slice.rows());
    MatrixAssert.assertValuesEquals(slice.getRow(0), bj.matrix(new long[]{1L, 1, 1}));
    MatrixAssert.assertValuesEquals(slice.getRow(1), bj.matrix(new long[]{2L, 2, 2}));
  }

  @Test
  public void testSlice3() throws Exception {
    LongMatrix x = bj.matrix(new long[]{1L, 2, 3, 1, 2, 3, 1, 2, 3}).reshape(3, 3);
    LongMatrix s = x.slice(bj.range(2), bj.range(2));
    assertEquals(2, s.rows());
    assertEquals(2, s.columns());
    MatrixAssert.assertValuesEquals(s.getRow(0), bj.matrix(new long[]{1L, 1}));
    MatrixAssert.assertValuesEquals(s.getRow(1), bj.matrix(new long[]{2L, 2}));
  }

  @Test
  public void testSlice4() throws Exception {
    LongMatrix x = bj.matrix(new long[]{1L, 2, 3, 1, 2, 3, 1, 2, 3}).reshape(3, 3);
    LongMatrix s = x.slice(asList(0, 2, 5, 7));
    MatrixAssert.assertValuesEquals(s, bj.matrix(new long[]{1L, 3, 3, 2}));
  }

  @Test
  public void testSlice5() throws Exception {
    LongMatrix x = bj.matrix(new long[]{1L, 2, 3, 1, 2, 3, 1, 2, 3}).reshape(3, 3);
    LongMatrix s = x.slice(asList(0, 2), Dim.R);
    MatrixAssert.assertValuesEquals(s.getRow(0), bj.matrix(new long[]{1L, 1, 1}));
    MatrixAssert.assertValuesEquals(s.getRow(1), bj.matrix(new long[]{3L, 3, 3}));
  }

  @Test
  public void testSlice6() throws Exception {
    LongMatrix x = bj.matrix(new long[]{1, 2, 3, 1, 2, 3, 1, 2, 3L}).reshape(3, 3);
    LongMatrix s = x.slice(asList(0, 1), asList(0, 1));
    MatrixAssert.assertValuesEquals(s.getRow(0), bj.matrix(new long[]{1L, 1}));
    MatrixAssert.assertValuesEquals(s.getRow(1), bj.matrix(new long[]{2L, 2}));
  }

  @Test
  public void testSlice7() throws Exception {
    LongMatrix x = bj.matrix(new long[]{1L, 2, 3, 1L, 2, 3, 1, 2, 3}).reshape(3, 3);
    BitMatrix bits =
        bj.matrix(new boolean[]{
            true, true, true, false, false, false, false, false, false})
            .reshape(3, 3);
    LongMatrix s = x.slice(bits);
    MatrixAssert.assertValuesEquals(s, bj.matrix(new long[]{1, 2, 3L}));
  }

  @Test
  public void testSlice() throws Exception {
    LongMatrix x = bj.matrix(new long[]{1L, 2, 3, 1, 2, 3, 1, 2, 3}).reshape(3, 3);
    LongMatrix s = x.slice(bj.matrix(new boolean[]{true, false, true}), Dim.R);
    MatrixAssert.assertValuesEquals(s.getRow(0), bj.matrix(new long[]{1L, 1, 1}));
    MatrixAssert.assertValuesEquals(s.getRow(1), bj.matrix(new long[]{3L, 3, 3}));
  }

  @Test
  public void testSwap() throws Exception {
    LongMatrix x = bj.matrix(new long[]{1L, 2, 3});
    x.swap(0, 2);
    MatrixAssert.assertValuesEquals(x, bj.matrix(new long[]{3L, 2, 1}));
  }

  @Test
  public void testSetRow() throws Exception {
    LongMatrix x = bj.longMatrix(3, 3);
    x.setRow(0, bj.matrix(new long[]{1L, 2, 3}));
    MatrixAssert.assertValuesEquals(x.getRow(0), bj.matrix(new long[]{1L, 2, 3}));
  }

  @Test
  public void testSetColumn() throws Exception {
    LongMatrix x = bj.longMatrix(3, 3);
    x.setColumn(0, bj.matrix(new long[]{1L, 2, 3}));
    MatrixAssert.assertValuesEquals(x.getColumn(0), bj.matrix(new long[]{1L, 2, 3}));
  }

  @Test
  public void testHashCode() throws Exception {

  }

  @Test
  public void testEquals() throws Exception {

  }

  @Test
  public void testToString() throws Exception {

  }

  @Test
  public void testIterator() throws Exception {
    LongMatrix x = bj.matrix(new long[]{1L, 2, 3, 4, 5, 6});
    int i = 0;
    for (long v : x) {
      assertEquals(x.get(i++), v);
    }
  }

  @Test
  public void testStream() throws Exception {
    LongMatrix m = bj.longMatrix(3, 3).assign(3);
    LongSummaryStatistics s = m.stream().summaryStatistics();
    assertEquals(3 * 3 * 3, s.getSum());
  }
}

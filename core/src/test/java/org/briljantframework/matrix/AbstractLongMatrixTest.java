package org.briljantframework.matrix;

import static java.util.Arrays.asList;
import static org.briljantframework.matrix.Matrices.*;
import static org.briljantframework.matrix.MatrixAssert.assertMatrixEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.LongSummaryStatistics;

import org.briljantframework.complex.Complex;
import org.junit.Test;

public class AbstractLongMatrixTest {

  @Test
  public void testRsub2() throws Exception {
    LongMatrix x = newLongVector(1, 2, 3);
    LongMatrix y = newLongMatrix(3, 3).assign(3);
    LongMatrix z = y.rsub(x, Axis.ROW);
    MatrixAssert.assertValuesEquals(z.getRowView(0), newLongVector(-2, -1, 0));
    MatrixAssert.assertValuesEquals(z.getRowView(1), newLongVector(-2, -1, 0));
    MatrixAssert.assertValuesEquals(z.getRowView(2), newLongVector(-2, -1, 0));

    z = y.rsub(x, Axis.COLUMN);
    MatrixAssert.assertValuesEquals(z.getColumnView(0), newLongVector(-2, -1, 0));
    MatrixAssert.assertValuesEquals(z.getColumnView(1), newLongVector(-2, -1, 0));
    MatrixAssert.assertValuesEquals(z.getColumnView(2), newLongVector(-2, -1, 0));
  }

  @Test
  public void testAssign() throws Exception {
    LongMatrix m = newLongMatrix(3, 3);
    m.assign(3);
    assertMatrixEquals(m, 3);
  }

  @Test
  public void testAssign1() throws Exception {
    LongMatrix m = newLongMatrix(3, 3);
    m.assign(() -> 3);
    assertMatrixEquals(m, 3);
  }

  @Test
  public void testAssign2() throws Exception {
    LongMatrix m = newLongMatrix(3, 3);
    m.assign(3).update(x -> x * 2);
    assertMatrixEquals(m, 6);
  }

  @Test
  public void testAssign3() throws Exception {
    DoubleMatrix d = DoubleMatrix.newMatrix(3, 3).assign(3);
    LongMatrix i = newLongMatrix(3, 3).assign(d, x -> (int) x);
    assertMatrixEquals(i, 3);
  }

  @Test
  public void testAssign4() throws Exception {
    ComplexMatrix c = newComplexMatrix(3, 3).assign(Complex.valueOf(3));
    LongMatrix i = newLongMatrix(3, 3).assign(c, Complex::intValue);
    assertMatrixEquals(i, 3);
  }

  @Test
  public void testAssign5() throws Exception {
    LongMatrix l = newLongMatrix(3, 3).assign(3L);
    LongMatrix i = newLongMatrix(3, 3).assign(l, x -> (int) x);
    assertMatrixEquals(i, 3);
  }

  @Test
  public void testAssign6() throws Exception {
    LongMatrix i = newLongMatrix(3, 3).assign(newLongMatrix(3, 3).assign(3));
    assertMatrixEquals(i, 3);
  }

  @Test
  public void testAssign7() throws Exception {
    LongMatrix i = newLongMatrix(3, 3).assign(newLongMatrix(3, 3).assign(3), x -> x * 2);
    assertMatrixEquals(i, 6);
  }

  @Test
  public void testAssign8() throws Exception {
    LongMatrix x = newLongMatrix(3, 3).assign(2);
    LongMatrix d = newLongMatrix(3, 3).assign(5);
    x.assign(d, Long::sum);
    assertMatrixEquals(x, 7);
  }

  @Test
  public void testMap() throws Exception {
    LongMatrix i = newLongMatrix(3, 3).assign(3);
    LongMatrix m = i.map(Long::bitCount);
    assertMatrixEquals(m, 2);
  }

  @Test
  public void testMapToInt() throws Exception {
    LongMatrix i = newLongMatrix(3, 3).assign(Integer.MAX_VALUE + 10L);
    IntMatrix l = i.mapToInt(x -> (int) (x - Integer.MAX_VALUE));
    MatrixAssert.assertMatrixEquals(10, l);
  }

  @Test
  public void testMapToDouble() throws Exception {
    DoubleMatrix i = newLongMatrix(3, 3).assign(3).mapToDouble(Math::sqrt);
    assertMatrixEquals(Math.sqrt(3), i, 0.0001);
  }

  @Test
  public void testMapToComplex() throws Exception {
    ComplexMatrix i = newLongMatrix(3, 3).assign(-3).mapToComplex(Complex::sqrt);
    assertMatrixEquals(i, Complex.sqrt(-3));
  }

  @Test
  public void testFilter() throws Exception {
    LongMatrix i = newLongVector(0, 1, 2, 3, 4, 5, 6).filter(x -> x > 3);
    MatrixAssert.assertValuesEquals(i, newLongVector(4, 5, 6));
  }

  @Test
  public void testSatisfies() throws Exception {
    BitMatrix i = newLongVector(0, 1, 2, 3, 4, 5).satisfies(x -> x >= 3);
    MatrixAssert.assertValuesEquals(newBitVector(false, false, false, true, true, true), i);
  }

  @Test
  public void testSatisfies1() throws Exception {
    LongMatrix x = newLongMatrix(3, 3);
    LongMatrix y = newLongMatrix(3, 3).assign(3);
    BitMatrix z = x.satisfies(y, (a, b) -> a < b);
    assertMatrixEquals(z, true);
  }

  @Test
  public void testReduce() throws Exception {
    LongMatrix x = newLongMatrix(3, 3).assign(3);
    long sum = x.reduce(0, Long::sum);
    assertEquals(3 * 9, sum);
  }

  @Test
  public void testReduce1() throws Exception {
    LongMatrix x = newLongMatrix(3, 3).assign(3);
    long squaredSum = x.reduce(0, Long::sum, i -> i * 2);
    assertEquals(3 * 2 * 9, squaredSum);
  }

  @Test
  public void testReduceColumns() throws Exception {
    LongMatrix x = newLongMatrix(3, 4).assign(3).reduceColumns(y -> y.reduce(0, Long::sum));
    assertEquals(4, x.columns());
    assertEquals(1, x.rows());
    assertMatrixEquals(x, 3 * 3);
  }

  @Test
  public void testReduceRows() throws Exception {
    LongMatrix x = newLongMatrix(4, 3).assign(3).reduceRows(y -> y.reduce(0, Long::sum));
    assertEquals(4, x.rows());
    assertEquals(1, x.columns());
    assertMatrixEquals(x, 3 * 3);
  }

  @Test
  public void testReshape() throws Exception {
    LongMatrix x = newLongVector(1, 2, 3, 4, 5, 6).reshape(2, 3);
    assertEquals(2, x.rows());
    assertEquals(3, x.columns());
  }

  @Test
  public void testGet() throws Exception {
    LongMatrix x = newLongVector(0, 1, 2, 3, 4, 5).reshape(3, 2);
    assertEquals(0, x.get(0));
    assertEquals(5, x.get(5));
  }

  @Test
  public void testGet1() throws Exception {
    LongMatrix x = newLongVector(0, 1, 2, 3, 4, 5).reshape(3, 2);
    assertEquals(0, x.get(0, 0));
    assertEquals(3, x.get(0, 1));
    assertEquals(4, x.get(1, 1));
  }

  @Test
  public void testSet() throws Exception {
    LongMatrix x = newLongMatrix(3, 3);
    x.set(0, 0, 1);
    x.set(0, 1, 2);
    x.set(1, 1, 3);

    assertEquals(1, x.get(0, 0));
    assertEquals(2, x.get(0, 1));
    assertEquals(3, x.get(1, 1));
  }

  // @Test
  // public void testAddTo() throws Exception {
  // LongMatrix x = newLongMatrix(1, 1, 1, 1);
  // x.addTo(0, 10);
  // assertEquals(11, x.get(0));
  // }
  //
  // @Test
  // public void testAddTo1() throws Exception {
  // LongMatrix x = newLongMatrix(1, 1, 1, 1).reshape(2, 2);
  // x.addTo(0, 0, 10);
  // x.addTo(0, 1, 10);
  // assertEquals(11, x.get(0, 0));
  // assertEquals(11, x.get(0, 1));
  // }
  //
  // @Test
  // public void testUpdate() throws Exception {
  // LongMatrix x = newLongMatrix(1, 1, 1, 1).reshape(2, 2);
  // x.update(0, 0, i -> i * 3);
  // assertEquals(3, x.get(0, 0));
  // }
  //
  // @Test
  // public void testUpdate1() throws Exception {
  // LongMatrix x = newLongMatrix(1, 1, 1, 1);
  // x.update(0, i -> i * 3);
  // assertEquals(3, x.get(0));
  // }

  @Test
  public void testSet1() throws Exception {
    LongMatrix x = newLongVector(0, 1, 2, 3);
    assertEquals(0, x.get(0));
    assertEquals(1, x.get(1));
    assertEquals(2, x.get(2));
    assertEquals(3, x.get(3));
  }

  @Test
  public void testGetRowView() throws Exception {
    LongMatrix x = newLongVector(1, 2, 3, 1, 2, 3, 1, 2, 3).reshape(3, 3);
    assertMatrixEquals(x.getRowView(0), 1);
    assertMatrixEquals(x.getRowView(1), 2);
    assertMatrixEquals(x.getRowView(2), 3);
  }

  @Test
  public void testGetColumnView() throws Exception {
    LongMatrix x = newLongVector(1, 1, 1, 2, 2, 2, 3, 3, 3).reshape(3, 3);
    assertMatrixEquals(x.getColumnView(0), 1);
    assertMatrixEquals(x.getColumnView(1), 2);
    assertMatrixEquals(x.getColumnView(2), 3);
  }

  @Test
  public void testGetDiagonalView() throws Exception {

  }

  @Test
  public void testGetView() throws Exception {
    LongMatrix x = newLongVector(1, 1, 1, 1, 2, 2).reshape(2, 3);
    assertMatrixEquals(x.getView(0, 0, 2, 2), 1);
  }

  @Test
  public void testTranspose() throws Exception {
    LongMatrix x = newLongVector(1, 2, 3, 1, 2, 3).reshape(2, 3).transpose();
    assertEquals(3, x.rows());
    assertEquals(2, x.columns());
    assertEquals(1, x.get(0, 0));
    assertEquals(3, x.get(2, 1));
  }

  @Test
  public void testCopy() throws Exception {
    LongMatrix x = newLongVector(1, 1, 1, 1);
    LongMatrix y = x.copy();
    x.set(0, 1000);
    assertEquals(1, y.get(0));
  }

  @Test
  public void testNewEmptyMatrix() throws Exception {
    assertNotNull(newLongMatrix(2, 2).newEmptyMatrix(2, 2));
  }

  @Test
  public void testNewEmptyVector() throws Exception {
    LongMatrix x = newLongMatrix(2, 2).newEmptyVector(2);
    assertNotNull(x);
    assertEquals(2, x.rows());
    assertEquals(1, x.columns());
  }

  @Test
  public void testMmul() throws Exception {
    LongMatrix x = newLongVector(1, 2, 3, 4, 5, 6).reshape(3, 2);
    LongMatrix y = newLongVector(1, 2, 3, 4, 5, 6).reshape(2, 3);

    LongMatrix z = y.mmul(x);
    LongMatrix za = newLongVector(22, 28, 49, 64).reshape(2, 2);
    assertMatrixEquals(za, z);

    z = x.mmul(y);
    za = newLongVector(9, 12, 15, 19, 26, 33, 29, 40, 51).reshape(3, 3);
    assertMatrixEquals(za, z);
  }

  @Test
  public void testMmul1() throws Exception {
    LongMatrix x = newLongVector(1, 2, 3, 4, 5, 6).reshape(3, 2);
    LongMatrix y = newLongVector(1, 2, 3, 4, 5, 6).reshape(2, 3);

    LongMatrix z = y.mmul(2, x);
    LongMatrix za = newLongVector(44, 56, 98, 128).reshape(2, 2);
    assertMatrixEquals(za, z);

    z = x.mmul(4, y);
    za = newLongVector(36, 48, 60, 76, 104, 132, 116, 160, 204).reshape(3, 3);
    assertMatrixEquals(za, z);
  }

  @Test
  public void testMmul2() throws Exception {
    LongMatrix x = newLongVector(1, 2, 3, 4, 5, 6).reshape(3, 2);
    LongMatrix y = newLongVector(1, 2, 3, 4, 5, 6).reshape(3, 2);

    LongMatrix z = y.mmul(Transpose.YES, x, Transpose.NO);
    LongMatrix za = newLongVector(14, 32, 32, 77).reshape(2, 2);
    assertMatrixEquals(za, z);

    z = x.mmul(Transpose.NO, y, Transpose.YES);
    za = newLongVector(17, 22, 27, 22, 29, 36, 27, 36, 45).reshape(3, 3);
    assertMatrixEquals(za, z);
  }

  @Test
  public void testMmul3() throws Exception {
    LongMatrix x = newLongVector(1, 2, 3, 4, 5, 6).reshape(3, 2);
    LongMatrix y = newLongVector(1, 2, 3, 4, 5, 6).reshape(3, 2);
    LongMatrix z = y.mmul(2, Transpose.YES, x, Transpose.NO);
    LongMatrix za = newLongVector(28, 64, 64, 154).reshape(2, 2);
    assertMatrixEquals(za, z);

    z = x.mmul(2, Transpose.NO, y, Transpose.YES);
    za = newLongVector(34, 44, 54, 44, 58, 72, 54, 72, 90).reshape(3, 3);
    assertMatrixEquals(za, z);
  }

  @Test
  public void testMul() throws Exception {
    LongMatrix x = newLongMatrix(3, 3).assign(3);
    LongMatrix z = x.mul(2);
    assertMatrixEquals(z, 6);
  }

  @Test
  public void testMul1() throws Exception {
    LongMatrix x = newLongMatrix(3, 3).assign(3);
    LongMatrix y = newLongMatrix(3, 3).assign(2);
    LongMatrix z = x.mul(y);
    assertMatrixEquals(z, 6);
  }

  @Test
  public void testMul2() throws Exception {
    LongMatrix x = newLongMatrix(3, 3).assign(3);
    LongMatrix y = newLongMatrix(3, 3).assign(2);
    LongMatrix z = x.mul(-1, y, -1);
    assertMatrixEquals(z, 6);
  }

  @Test
  public void testMul3() throws Exception {
    LongMatrix x = newLongVector(1, 2, 3);
    LongMatrix y = newLongMatrix(3, 3).assign(3);
    LongMatrix z = y.mul(x, Axis.ROW);
    assertMatrixEquals(z.getColumnView(0), 3);
    assertMatrixEquals(z.getColumnView(1), 6);
    assertMatrixEquals(z.getColumnView(2), 9);

    z = y.mul(x, Axis.COLUMN);
    assertMatrixEquals(z.getRowView(0), 3);
    assertMatrixEquals(z.getRowView(1), 6);
    assertMatrixEquals(z.getRowView(2), 9);
  }

  @Test
  public void testMul4() throws Exception {
    LongMatrix x = newLongVector(1, 2, 3);
    LongMatrix y = newLongMatrix(3, 3).assign(3);
    LongMatrix z = y.mul(1, x, -1, Axis.ROW);
    assertMatrixEquals(z.getColumnView(0), -3);
    assertMatrixEquals(z.getColumnView(1), -6);
    assertMatrixEquals(z.getColumnView(2), -9);

    z = y.mul(1, x, -1, Axis.COLUMN);
    assertMatrixEquals(z.getRowView(0), -3);
    assertMatrixEquals(z.getRowView(1), -6);
    assertMatrixEquals(z.getRowView(2), -9);

  }

  @Test
  public void testAdd() throws Exception {
    LongMatrix x = newLongMatrix(3, 3).assign(2);
    assertMatrixEquals(x.add(3), 5);
  }

  @Test
  public void testAdd1() throws Exception {
    LongMatrix x = newLongMatrix(3, 3).assign(2);
    LongMatrix y = newLongMatrix(3, 3).assign(3);
    assertMatrixEquals(x.add(y), 5);
  }

  @Test
  public void testAdd2() throws Exception {
    LongMatrix x = newLongMatrix(3, 3).assign(2);
    LongMatrix y = newLongMatrix(3, 3).assign(3);
    assertMatrixEquals(x.add(1, y, -1), -1);
  }

  @Test
  public void testAdd3() throws Exception {
    LongMatrix x = newLongVector(1, 2, 3);
    LongMatrix y = newLongMatrix(3, 3).assign(3);
    LongMatrix z = y.add(x, Axis.ROW);
    MatrixAssert.assertValuesEquals(z.getRowView(0), newLongVector(4, 5, 6));
    MatrixAssert.assertValuesEquals(z.getRowView(1), newLongVector(4, 5, 6));
    MatrixAssert.assertValuesEquals(z.getRowView(2), newLongVector(4, 5, 6));

    z = y.add(x, Axis.COLUMN);
    MatrixAssert.assertValuesEquals(z.getColumnView(0), newLongVector(4, 5, 6));
    MatrixAssert.assertValuesEquals(z.getColumnView(1), newLongVector(4, 5, 6));
    MatrixAssert.assertValuesEquals(z.getColumnView(2), newLongVector(4, 5, 6));
  }

  @Test
  public void testAdd4() throws Exception {
    LongMatrix x = newLongVector(1, 2, 3);
    LongMatrix y = newLongMatrix(3, 3).assign(3);
    LongMatrix z = y.add(1, x, -1, Axis.ROW);
    MatrixAssert.assertValuesEquals(z.getRowView(0), newLongVector(2, 1, 0));
    MatrixAssert.assertValuesEquals(z.getRowView(1), newLongVector(2, 1, 0));
    MatrixAssert.assertValuesEquals(z.getRowView(2), newLongVector(2, 1, 0));

    z = y.add(1, x, -1, Axis.COLUMN);
    MatrixAssert.assertValuesEquals(z.getColumnView(0), newLongVector(2, 1, 0));
    MatrixAssert.assertValuesEquals(z.getColumnView(1), newLongVector(2, 1, 0));
    MatrixAssert.assertValuesEquals(z.getColumnView(2), newLongVector(2, 1, 0));
  }

  @Test
  public void testSub() throws Exception {
    LongMatrix x = newLongMatrix(3, 3).assign(3);
    assertMatrixEquals(x.sub(2), 1);
  }

  @Test
  public void testSub1() throws Exception {
    LongMatrix x = newLongMatrix(3, 3).assign(3);
    LongMatrix y = newLongMatrix(3, 3).assign(2);
    assertMatrixEquals(x.sub(y), 1);
  }

  @Test
  public void testSub2() throws Exception {
    LongMatrix x = newLongMatrix(3, 3).assign(3);
    LongMatrix y = newLongMatrix(3, 3).assign(2);
    assertMatrixEquals(x.sub(1, y, -1), 5);
  }

  @Test
  public void testSub3() throws Exception {
    LongMatrix x = newLongVector(1, 2, 3);
    LongMatrix y = newLongMatrix(3, 3).assign(3);
    LongMatrix z = y.sub(x, Axis.ROW);
    MatrixAssert.assertValuesEquals(z.getRowView(0), newLongVector(2, 1, 0));
    MatrixAssert.assertValuesEquals(z.getRowView(1), newLongVector(2, 1, 0));
    MatrixAssert.assertValuesEquals(z.getRowView(2), newLongVector(2, 1, 0));

    z = y.sub(x, Axis.COLUMN);
    MatrixAssert.assertValuesEquals(z.getColumnView(0), newLongVector(2, 1, 0));
    MatrixAssert.assertValuesEquals(z.getColumnView(1), newLongVector(2, 1, 0));
    MatrixAssert.assertValuesEquals(z.getColumnView(2), newLongVector(2, 1, 0));
  }

  @Test
  public void testSub4() throws Exception {
    LongMatrix x = newLongVector(1, 2, 3);
    LongMatrix y = newLongMatrix(3, 3).assign(3);
    LongMatrix z = y.sub(1, x, -1, Axis.ROW);
    MatrixAssert.assertValuesEquals(z.getRowView(0), newLongVector(4, 5, 6));
    MatrixAssert.assertValuesEquals(z.getRowView(1), newLongVector(4, 5, 6));
    MatrixAssert.assertValuesEquals(z.getRowView(2), newLongVector(4, 5, 6));

    z = y.sub(1, x, -1, Axis.COLUMN);
    MatrixAssert.assertValuesEquals(z.getColumnView(0), newLongVector(4, 5, 6));
    MatrixAssert.assertValuesEquals(z.getColumnView(1), newLongVector(4, 5, 6));
    MatrixAssert.assertValuesEquals(z.getColumnView(2), newLongVector(4, 5, 6));
  }

  @Test
  public void testRsub() throws Exception {
    LongMatrix x = newLongMatrix(3, 3).assign(2);
    LongMatrix y = x.rsub(3);
    assertMatrixEquals(y, 1);
  }

  @Test
  public void testRsub1() throws Exception {
    LongMatrix x = newLongVector(1, 2, 3);
    LongMatrix y = newLongMatrix(3, 3).assign(3);
    LongMatrix z = y.rsub(1, x, -1, Axis.ROW);
    MatrixAssert.assertValuesEquals(z.getRowView(0), newLongVector(-4, -5, -6));
    MatrixAssert.assertValuesEquals(z.getRowView(1), newLongVector(-4, -5, -6));
    MatrixAssert.assertValuesEquals(z.getRowView(2), newLongVector(-4, -5, -6));

    z = y.rsub(1, x, -1, Axis.COLUMN);
    MatrixAssert.assertValuesEquals(z.getColumnView(0), newLongVector(-4, -5, -6));
    MatrixAssert.assertValuesEquals(z.getColumnView(1), newLongVector(-4, -5, -6));
    MatrixAssert.assertValuesEquals(z.getColumnView(2), newLongVector(-4, -5, -6));
  }

  @Test
  public void testDiv() throws Exception {
    LongMatrix x = newLongMatrix(3, 3).assign(4);
    LongMatrix y = x.div(2);
    assertMatrixEquals(y, 2);
  }

  @Test
  public void testDiv1() throws Exception {
    LongMatrix x = newLongMatrix(3, 3).assign(4);
    LongMatrix y = newLongMatrix(3, 3).assign(2);
    LongMatrix z = x.div(y);
    assertMatrixEquals(z, 2);
  }

  @Test
  public void testDiv2() throws Exception {
    LongMatrix x = newLongVector(1, 2, 3);
    LongMatrix y = newLongMatrix(3, 3).assign(6);
    LongMatrix z = y.div(x, Axis.ROW);

    MatrixAssert.assertValuesEquals(z.getRowView(0), newLongVector(6, 3, 2));
    MatrixAssert.assertValuesEquals(z.getRowView(1), newLongVector(6, 3, 2));
    MatrixAssert.assertValuesEquals(z.getRowView(2), newLongVector(6, 3, 2));

    z = y.div(x, Axis.COLUMN);
    MatrixAssert.assertValuesEquals(z.getColumnView(0), newLongVector(6, 3, 2));
    MatrixAssert.assertValuesEquals(z.getColumnView(1), newLongVector(6, 3, 2));
    MatrixAssert.assertValuesEquals(z.getColumnView(2), newLongVector(6, 3, 2));
  }

  @Test
  public void testDiv3() throws Exception {
    LongMatrix x = newLongVector(1, 2, 3);
    LongMatrix y = newLongMatrix(3, 3).assign(6);
    LongMatrix z = y.div(2, x, 1, Axis.ROW);

    MatrixAssert.assertValuesEquals(z.getRowView(0), newLongVector(12, 6, 4));
    MatrixAssert.assertValuesEquals(z.getRowView(1), newLongVector(12, 6, 4));
    MatrixAssert.assertValuesEquals(z.getRowView(2), newLongVector(12, 6, 4));

    z = y.div(2, x, 1, Axis.COLUMN);
    MatrixAssert.assertValuesEquals(z.getColumnView(0), newLongVector(12, 6, 4));
    MatrixAssert.assertValuesEquals(z.getColumnView(1), newLongVector(12, 6, 4));
    MatrixAssert.assertValuesEquals(z.getColumnView(2), newLongVector(12, 6, 4));
  }

  @Test
  public void testRdiv() throws Exception {
    LongMatrix x = newLongMatrix(3, 3).assign(6);
    LongMatrix y = x.rdiv(12);
    assertMatrixEquals(y, 2);
  }

  @Test
  public void testRdiv1() throws Exception {
    LongMatrix x = newLongVector(12, 12, 12);
    LongMatrix y = newLongMatrix(3, 3).assign(6);
    LongMatrix z = y.rdiv(x, Axis.ROW);

    assertMatrixEquals(z.getRowView(0), 2);
    assertMatrixEquals(z.getRowView(1), 2);
    assertMatrixEquals(z.getRowView(2), 2);

    z = y.rdiv(x, Axis.COLUMN);
    assertMatrixEquals(z.getColumnView(0), 2);
    assertMatrixEquals(z.getColumnView(1), 2);
    assertMatrixEquals(z.getColumnView(2), 2);
  }

  @Test
  public void testRdiv2() throws Exception {
    LongMatrix x = newLongVector(6, 6, 6);
    LongMatrix y = newLongMatrix(3, 3).assign(6);
    LongMatrix z = y.rdiv(1, x, 2, Axis.ROW);

    assertMatrixEquals(z.getRowView(0), 2);
    assertMatrixEquals(z.getRowView(1), 2);
    assertMatrixEquals(z.getRowView(2), 2);

    z = y.rdiv(1, x, 2, Axis.COLUMN);
    assertMatrixEquals(z.getColumnView(0), 2);
    assertMatrixEquals(z.getColumnView(1), 2);
    assertMatrixEquals(z.getColumnView(2), 2);
  }

  @Test
  public void testNegate() throws Exception {
    LongMatrix x = newLongMatrix(3, 3).assign(3).negate();
    assertMatrixEquals(x, -3);
  }

  @Test
  public void testSlice1() throws Exception {
    LongMatrix x = newLongVector(1, 2, 3, 1, 2, 3).reshape(3, 2);
    LongMatrix slice = x.slice(Range.range(3));
    MatrixAssert.assertValuesEquals(slice, newLongVector(1, 2, 3));
  }

  @Test
  public void testSlice2() throws Exception {
    LongMatrix x = newLongVector(1, 2, 3, 1, 2, 3, 1, 2, 3).reshape(3, 3);
    LongMatrix slice = x.slice(Range.range(2), Axis.ROW);
    assertEquals(2, slice.rows());
    MatrixAssert.assertValuesEquals(slice.getRowView(0), newLongVector(1, 1, 1));
    MatrixAssert.assertValuesEquals(slice.getRowView(1), newLongVector(2, 2, 2));
  }

  @Test
  public void testSlice3() throws Exception {
    LongMatrix x = newLongVector(1, 2, 3, 1, 2, 3, 1, 2, 3).reshape(3, 3);
    LongMatrix s = x.slice(Range.range(2), Range.range(2));
    assertEquals(2, s.rows());
    assertEquals(2, s.columns());
    MatrixAssert.assertValuesEquals(s.getRowView(0), newLongVector(1, 1));
    MatrixAssert.assertValuesEquals(s.getRowView(1), newLongVector(2, 2));
  }

  @Test
  public void testSlice4() throws Exception {
    LongMatrix x = newLongVector(1, 2, 3, 1, 2, 3, 1, 2, 3).reshape(3, 3);
    LongMatrix s = x.slice(asList(0, 2, 5, 7));
    MatrixAssert.assertValuesEquals(s, newLongVector(1, 3, 3, 2));
  }

  @Test
  public void testSlice5() throws Exception {
    LongMatrix x = newLongVector(1, 2, 3, 1, 2, 3, 1, 2, 3).reshape(3, 3);
    LongMatrix s = x.slice(asList(0, 2), Axis.ROW);
    MatrixAssert.assertValuesEquals(s.getRowView(0), newLongVector(1, 1, 1));
    MatrixAssert.assertValuesEquals(s.getRowView(1), newLongVector(3, 3, 3));
  }

  @Test
  public void testSlice6() throws Exception {
    LongMatrix x = newLongVector(1, 2, 3, 1, 2, 3, 1, 2, 3).reshape(3, 3);
    LongMatrix s = x.slice(asList(0, 1), asList(0, 1));
    MatrixAssert.assertValuesEquals(s.getRowView(0), newLongVector(1, 1));
    MatrixAssert.assertValuesEquals(s.getRowView(1), newLongVector(2, 2));
  }

  @Test
  public void testSlice7() throws Exception {
    LongMatrix x = newLongVector(1, 2, 3, 1, 2, 3, 1, 2, 3).reshape(3, 3);
    BitMatrix bits =
        newBitVector(true, true, true, false, false, false, false, false, false).reshape(3, 3);
    LongMatrix s = x.slice(bits);
    MatrixAssert.assertValuesEquals(s, newLongVector(1, 2, 3));
  }

  @Test
  public void testSlice() throws Exception {
    LongMatrix x = newLongVector(1, 2, 3, 1, 2, 3, 1, 2, 3).reshape(3, 3);
    LongMatrix s = x.slice(newBitVector(true, false, true), Axis.ROW);
    MatrixAssert.assertValuesEquals(s.getRowView(0), newLongVector(1, 1, 1));
    MatrixAssert.assertValuesEquals(s.getRowView(1), newLongVector(3, 3, 3));
  }

  @Test
  public void testSwap() throws Exception {
    LongMatrix x = newLongVector(1, 2, 3);
    x.swap(0, 2);
    MatrixAssert.assertValuesEquals(x, newLongVector(3, 2, 1));
  }

  @Test
  public void testSetRow() throws Exception {
    LongMatrix x = newLongMatrix(3, 3);
    x.setRow(0, newLongVector(1, 2, 3));
    MatrixAssert.assertValuesEquals(x.getRowView(0), newLongVector(1, 2, 3));
  }

  @Test
  public void testSetColumn() throws Exception {
    LongMatrix x = newLongMatrix(3, 3);
    x.setColumn(0, newLongVector(1, 2, 3));
    MatrixAssert.assertValuesEquals(x.getColumnView(0), newLongVector(1, 2, 3));
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
    LongMatrix x = newLongVector(1, 2, 3, 4, 5, 6);
    int i = 0;
    for (long v : x) {
      assertEquals(x.get(i++), v);
    }
  }

  @Test
  public void testStream() throws Exception {
    LongMatrix m = newLongMatrix(3, 3).assign(3);
    LongSummaryStatistics s = m.stream().summaryStatistics();
    assertEquals(3 * 3 * 3, s.getSum());
  }
}

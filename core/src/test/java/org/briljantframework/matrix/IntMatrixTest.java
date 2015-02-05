package org.briljantframework.matrix;

import static org.briljantframework.matrix.Matrices.*;
import static org.briljantframework.matrix.MatrixAssert.assertMatrixEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.briljantframework.complex.Complex;
import org.junit.Test;

public class IntMatrixTest {

  @Test
  public void testAssign() throws Exception {
    IntMatrix m = newIntMatrix(3, 3);
    m.assign(3);
    assertMatrixEquals(m, 3);
  }

  @Test
  public void testAssign1() throws Exception {
    IntMatrix m = newIntMatrix(3, 3);
    m.assign(() -> 3);
    assertMatrixEquals(m, 3);
  }

  @Test
  public void testAssign2() throws Exception {
    IntMatrix m = newIntMatrix(3, 3);
    m.assign(3).assign(x -> x * 2);
    assertMatrixEquals(m, 6);
  }

  @Test
  public void testAssign3() throws Exception {
    DoubleMatrix d = newDoubleMatrix(3, 3).assign(3);
    IntMatrix i = newIntMatrix(3, 3).assign(d, x -> (int) x);
    assertMatrixEquals(i, 3);
  }

  @Test
  public void testAssign4() throws Exception {
    ComplexMatrix c = newComplexMatrix(3, 3).assign(Complex.valueOf(3));
    IntMatrix i = newIntMatrix(3, 3).assign(c, Complex::intValue);
    assertMatrixEquals(i, 3);
  }

  @Test
  public void testAssign5() throws Exception {
    LongMatrix l = newLongMatrix(3, 3).assign(3L);
    IntMatrix i = newIntMatrix(3, 3).assign(l, x -> (int) x);
    assertMatrixEquals(i, 3);
  }

  @Test
  public void testAssign6() throws Exception {
    IntMatrix i = newIntMatrix(3, 3).assign(newIntMatrix(3, 3).assign(3));
    assertMatrixEquals(i, 3);
  }

  @Test
  public void testAssign7() throws Exception {
    IntMatrix i = newIntMatrix(3, 3).assign(newIntMatrix(3, 3).assign(3), x -> x * 2);
    assertMatrixEquals(i, 6);
  }

  @Test
  public void testAssign8() throws Exception {
    IntMatrix x = newIntMatrix(3, 3).assign(2);
    IntMatrix d = newIntMatrix(3, 3).assign(5);
    x.assign(d, Integer::sum);
    assertMatrixEquals(x, 7);
  }

  @Test
  public void testMap() throws Exception {
    IntMatrix i = newIntMatrix(3, 3).assign(3);
    IntMatrix m = i.map(Integer::bitCount);
    assertMatrixEquals(m, 2);
  }

  @Test
  public void testMapToLong() throws Exception {
    IntMatrix i = newIntMatrix(3, 3).assign(3);
    LongMatrix l = i.mapToLong(x -> Integer.MAX_VALUE + (long) x);
    assertMatrixEquals(l, ((long) Integer.MAX_VALUE) + 3L);
  }

  @Test
  public void testMapToDouble() throws Exception {
    DoubleMatrix i = newIntMatrix(3, 3).assign(3).mapToDouble(Math::sqrt);
    assertMatrixEquals(i, Math.sqrt(3), 0.0001);
  }

  @Test
  public void testMapToComplex() throws Exception {
    ComplexMatrix i = newIntMatrix(3, 3).assign(-3).mapToComplex(Complex::sqrt);
    assertMatrixEquals(i, Complex.sqrt(-3));
  }

  @Test
  public void testFilter() throws Exception {
    IntMatrix i = newIntMatrix(0, 1, 2, 3, 4, 5, 6).filter(x -> x > 3);
    assertMatrixEquals(i, 4, 5, 6);
  }

  @Test
  public void testSatisfies() throws Exception {
    BitMatrix i = newIntMatrix(0, 1, 2, 3, 4, 5).satisfies(x -> x >= 3);
    assertMatrixEquals(i, false, false, false, true, true, true);
  }

  @Test
  public void testSatisfies1() throws Exception {
    IntMatrix x = newIntMatrix(3, 3);
    IntMatrix y = newIntMatrix(3, 3).assign(3);
    BitMatrix z = x.satisfies(y, (a, b) -> a < b);
    assertMatrixEquals(z, true);
  }

  @Test
  public void testReduce() throws Exception {
    IntMatrix x = newIntMatrix(3, 3).assign(3);
    int sum = x.reduce(0, Integer::sum);
    assertEquals(3 * 9, sum);
  }

  @Test
  public void testReduce1() throws Exception {
    IntMatrix x = newIntMatrix(3, 3).assign(3);
    int squaredSum = x.reduce(0, Integer::sum, i -> i * 2);
    assertEquals(3 * 2 * 9, squaredSum);
  }

  @Test
  public void testReduceColumns() throws Exception {
    IntMatrix x = newIntMatrix(3, 4).assign(3).reduceColumns(y -> y.reduce(0, Integer::sum));
    assertEquals(4, x.columns());
    assertEquals(1, x.rows());
    assertMatrixEquals(x, 3 * 3);
  }

  @Test
  public void testReduceRows() throws Exception {
    IntMatrix x = newIntMatrix(4, 3).assign(3).reduceRows(y -> y.reduce(0, Integer::sum));
    assertEquals(4, x.rows());
    assertEquals(1, x.columns());
    assertMatrixEquals(x, 3 * 3);
  }

  @Test
  public void testReshape() throws Exception {
    IntMatrix x = newIntMatrix(1, 2, 3, 4, 5, 6).reshape(2, 3);
    assertEquals(2, x.rows());
    assertEquals(3, x.columns());
  }

  @Test
  public void testGet() throws Exception {
    IntMatrix x = newIntMatrix(0, 1, 2, 3, 4, 5).reshape(3, 2);
    assertEquals(0, x.get(0));
    assertEquals(5, x.get(5));
  }

  @Test
  public void testGet1() throws Exception {
    IntMatrix x = newIntMatrix(0, 1, 2, 3, 4, 5, 6).reshape(3, 2);
    assertEquals(0, x.get(0, 0));
    assertEquals(3, x.get(0, 1));
    assertEquals(4, x.get(1, 1));
  }

  @Test
  public void testSet() throws Exception {
    IntMatrix x = newIntMatrix(3, 3);
    x.set(0, 0, 1);
    x.set(0, 1, 2);
    x.set(1, 1, 3);

    assertEquals(1, x.get(0, 0));
    assertEquals(2, x.get(0, 1));
    assertEquals(3, x.get(1, 1));
  }

  @Test
  public void testSet1() throws Exception {
    IntMatrix x = newIntMatrix(0, 1, 2, 3);
    assertEquals(0, x.get(0));
    assertEquals(1, x.get(1));
    assertEquals(2, x.get(2));
    assertEquals(3, x.get(3));
  }

  @Test
  public void testAddTo() throws Exception {
    IntMatrix x = newIntMatrix(1, 1, 1, 1);
    x.addTo(0, 10);
    assertEquals(11, x.get(0));
  }

  @Test
  public void testAddTo1() throws Exception {
    IntMatrix x = newIntMatrix(1, 1, 1, 1).reshape(2, 2);
    x.addTo(0, 0, 10);
    x.addTo(0, 1, 10);
    assertEquals(11, x.get(0, 0));
    assertEquals(11, x.get(0, 1));
  }

  @Test
  public void testUpdate() throws Exception {
    IntMatrix x = newIntMatrix(1, 1, 1, 1).reshape(2, 2);
    x.update(0, 0, i -> i * 3);
    assertEquals(3, x.get(0, 0));
  }

  @Test
  public void testUpdate1() throws Exception {
    IntMatrix x = newIntMatrix(1, 1, 1, 1);
    x.update(0, i -> i * 3);
    assertEquals(3, x.get(0));
  }

  @Test
  public void testGetRowView() throws Exception {
    IntMatrix x = newIntMatrix(1, 2, 3, 1, 2, 3, 1, 2, 3).reshape(3, 3);
    assertMatrixEquals(x.getRowView(0), 1);
    assertMatrixEquals(x.getRowView(1), 2);
    assertMatrixEquals(x.getRowView(2), 3);
  }

  @Test
  public void testGetColumnView() throws Exception {
    IntMatrix x = newIntMatrix(1, 1, 1, 2, 2, 2, 3, 3, 3).reshape(3, 3);
    assertMatrixEquals(x.getColumnView(0), 1);
    assertMatrixEquals(x.getColumnView(1), 2);
    assertMatrixEquals(x.getColumnView(2), 3);
  }

  @Test
  public void testGetDiagonalView() throws Exception {

  }

  @Test
  public void testGetView() throws Exception {
    IntMatrix x = newIntMatrix(1, 1, 1, 1, 2, 2).reshape(2, 3);
    assertMatrixEquals(x.getView(0, 0, 2, 2), 1);
  }

  @Test
  public void testTranspose() throws Exception {
    IntMatrix x = newIntMatrix(1, 2, 3, 1, 2, 3).reshape(2, 3).transpose();
    assertEquals(3, x.rows());
    assertEquals(2, x.columns());
    assertEquals(1, x.get(0, 0));
    assertEquals(3, x.get(2, 1));
  }

  @Test
  public void testCopy() throws Exception {
    IntMatrix x = newIntMatrix(1, 1, 1, 1);
    IntMatrix y = x.copy();
    x.set(0, 1000);
    assertEquals(1, y.get(0));
  }

  @Test
  public void testNewEmptyMatrix() throws Exception {
    assertNotNull(newIntMatrix(2, 2).newEmptyMatrix(2, 2));
  }

  @Test
  public void testNewEmptyVector() throws Exception {
    IntMatrix x = newIntMatrix(2, 2).newEmptyVector(2);
    assertNotNull(x);
    assertEquals(2, x.rows());
    assertEquals(1, x.columns());
  }

  @Test
  public void testMmul() throws Exception {

  }

  @Test
  public void testMmul1() throws Exception {

  }

  @Test
  public void testMmul2() throws Exception {

  }

  @Test
  public void testMmul3() throws Exception {

  }

  @Test
  public void testMul() throws Exception {

  }

  @Test
  public void testMul1() throws Exception {

  }

  @Test
  public void testMul2() throws Exception {

  }

  @Test
  public void testMul3() throws Exception {

  }

  @Test
  public void testMul4() throws Exception {

  }

  @Test
  public void testMuli() throws Exception {

  }

  @Test
  public void testMuli1() throws Exception {

  }

  @Test
  public void testMuli2() throws Exception {

  }

  @Test
  public void testMuli3() throws Exception {

  }

  @Test
  public void testMuli4() throws Exception {

  }

  @Test
  public void testAdd() throws Exception {

  }

  @Test
  public void testAdd1() throws Exception {

  }

  @Test
  public void testAdd2() throws Exception {

  }

  @Test
  public void testAdd3() throws Exception {

  }

  @Test
  public void testAdd4() throws Exception {

  }

  @Test
  public void testAddi() throws Exception {

  }

  @Test
  public void testAddi1() throws Exception {

  }

  @Test
  public void testAddi2() throws Exception {

  }

  @Test
  public void testAddi3() throws Exception {

  }

  @Test
  public void testAddi4() throws Exception {

  }

  @Test
  public void testSub() throws Exception {

  }

  @Test
  public void testSub1() throws Exception {

  }

  @Test
  public void testSub2() throws Exception {

  }

  @Test
  public void testSub3() throws Exception {

  }

  @Test
  public void testSub4() throws Exception {

  }

  @Test
  public void testSubi() throws Exception {

  }

  @Test
  public void testSubi1() throws Exception {

  }

  @Test
  public void testSubi2() throws Exception {

  }

  @Test
  public void testSubi3() throws Exception {

  }

  @Test
  public void testSubi4() throws Exception {

  }

  @Test
  public void testRsub() throws Exception {

  }

  @Test
  public void testRsub1() throws Exception {

  }

  @Test
  public void testRsub2() throws Exception {

  }

  @Test
  public void testRsubi() throws Exception {

  }

  @Test
  public void testRsubi1() throws Exception {

  }

  @Test
  public void testRsubi2() throws Exception {

  }

  @Test
  public void testDiv() throws Exception {

  }

  @Test
  public void testDiv1() throws Exception {

  }

  @Test
  public void testDiv2() throws Exception {

  }

  @Test
  public void testDiv3() throws Exception {

  }

  @Test
  public void testDivi() throws Exception {

  }

  @Test
  public void testDivi1() throws Exception {

  }

  @Test
  public void testDivi2() throws Exception {

  }

  @Test
  public void testDivi3() throws Exception {

  }

  @Test
  public void testRdiv() throws Exception {

  }

  @Test
  public void testRdiv1() throws Exception {

  }

  @Test
  public void testRdiv2() throws Exception {

  }

  @Test
  public void testRdivi() throws Exception {

  }

  @Test
  public void testRdivi1() throws Exception {

  }

  @Test
  public void testRdivi2() throws Exception {

  }

  @Test
  public void testNegate() throws Exception {

  }
}

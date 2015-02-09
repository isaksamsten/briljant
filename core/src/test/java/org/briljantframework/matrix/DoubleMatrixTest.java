package org.briljantframework.matrix;

import static org.briljantframework.matrix.Matrices.*;
import static org.briljantframework.matrix.MatrixAssert.assertMatrixEquals;
import static org.junit.Assert.assertEquals;

import org.briljantframework.complex.Complex;
import org.junit.Test;

public class DoubleMatrixTest {

  private final double epsilon = 0.00001;

  @Test
  public void testAssign() throws Exception {
    DoubleMatrix x = newDoubleMatrix(3, 3).assign(3);
    assertMatrixEquals(x, 3, epsilon);
  }

  @Test
  public void testAssign1() throws Exception {
    DoubleMatrix x = newDoubleMatrix(3, 3).assign(() -> 3);
    assertMatrixEquals(x, 3, epsilon);
  }

  @Test
  public void testAssign2() throws Exception {
    DoubleMatrix x = newDoubleMatrix(3, 3).assign(newDoubleMatrix(3, 3).assign(3));
    assertMatrixEquals(x, 3, epsilon);
  }

  @Test
  public void testAssign3() throws Exception {
    DoubleMatrix x = newDoubleMatrix(3, 3).assign(newDoubleMatrix(3, 3).assign(3), i -> i * 2);
    assertMatrixEquals(x, 6, epsilon);
  }

  @Test
  public void testAssign4() throws Exception {
    DoubleMatrix x = newDoubleMatrix(3, 3).assign(newDoubleMatrix(3, 3).assign(3), Double::sum);
    assertMatrixEquals(x, 3, epsilon);
  }

  @Test
  public void testAssign5() throws Exception {
    DoubleMatrix x =
        newDoubleMatrix(3, 3).assign(newComplexMatrix(3, 3).assign(Complex.valueOf(3)),
            Complex::real);
    assertMatrixEquals(x, 3, epsilon);
  }

  @Test
  public void testAssign6() throws Exception {
    DoubleMatrix x = newDoubleMatrix(3, 3).assign(newIntMatrix(3, 3).assign(3), Math::sqrt);
    assertMatrixEquals(x, Math.sqrt(3), epsilon);
  }

  @Test
  public void testAssign7() throws Exception {
    DoubleMatrix x = newDoubleMatrix(3, 3).assign(newLongMatrix(3, 3).assign(3), Math::sqrt);
    assertMatrixEquals(x, Math.sqrt(3), epsilon);
  }

  @Test
  public void testAssign8() throws Exception {
    DoubleMatrix x = newDoubleMatrix(3, 3).assign(i -> i + 3);
    assertMatrixEquals(x, 3, epsilon);
  }

  @Test
  public void testMap() throws Exception {
    DoubleMatrix m = newDoubleVector(1, 2, 3, 4).map(Math::sqrt);
    MatrixAssert.assertValueEquals(m,
        newDoubleVector(Math.sqrt(1), Math.sqrt(2), Math.sqrt(3), Math.sqrt(4)), epsilon);
  }

  @Test
  public void testMapToLong() throws Exception {
    LongMatrix m = newDoubleVector(1.2, 2.2, 3.2, 4.2).mapToLong(Math::round);
    MatrixAssert.assertValuesEquals(m, newLongVector(1, 2, 3, 4));
  }

  @Test
  public void testMapToInt() throws Exception {
    IntMatrix m = newDoubleVector(1, 2, 3, 4).mapToInt(x -> (int) x);
    MatrixAssert.assertValuesEquals(newIntVector(1, 2, 3, 4), m);
  }

  @Test
  public void testMapToComplex() throws Exception {
    ComplexMatrix m = newDoubleVector(1, 2, 3, -4).mapToComplex(Complex::sqrt);
    MatrixAssert.assertMatrixEquals(m,
        newComplexVector(Complex.sqrt(1), Complex.sqrt(2), Complex.sqrt(3), Complex.sqrt(-4)));
  }

  @Test
  public void testFilter() throws Exception {
    DoubleMatrix m = newDoubleVector(1, 2, 3, 4, 5, 6).reshape(2, 3).filter(x -> x > 3);
    MatrixAssert.assertValueEquals(m, newDoubleVector(4, 5, 6), epsilon);
  }

  @Test
  public void testSatisfies() throws Exception {
    BitMatrix m = newDoubleVector(0, 1, 2, 3).satisfies(i -> i > 1);
    MatrixAssert.assertValuesEquals(newBitVector(false, false, true, true), m);
  }

  @Test
  public void testSatisfies1() throws Exception {
    BitMatrix m =
        newDoubleVector(1, 2, 3, 4).satisfies(newDoubleVector(1, 3, 3, 3), (a, b) -> a == b);
    MatrixAssert.assertValuesEquals(newBitVector(true, false, true, false), m);
  }

  @Test
  public void testReduce() throws Exception {
    double sum = newDoubleMatrix(3, 3).assign(3).reduce(0, Double::sum);
    assertEquals(3 * 3 * 3, sum, epsilon);
  }

  @Test
  public void testReduce1() throws Exception {
    double squaredSum = newDoubleMatrix(3, 3).assign(3).reduce(0, Double::sum, x -> x * 2);
    assertEquals(3 * 2 * 3 * 3, squaredSum, epsilon);
  }

  @Test
  public void testReduceColumns() throws Exception {
    DoubleMatrix row = newDoubleMatrix(3, 4).assign(3).reduceColumns(x -> x.reduce(0, Double::sum));
    assertEquals(1, row.rows());
    assertEquals(4, row.columns());
    assertMatrixEquals(row, 9, epsilon);
  }

  @Test
  public void testReduceRows() throws Exception {
    DoubleMatrix col = newDoubleMatrix(4, 3).assign(3).reduceRows(x -> x.reduce(0, Double::sum));
    assertEquals(1, col.columns());
    assertEquals(4, col.rows());
    assertMatrixEquals(col, 9, epsilon);
  }

  @Test
  public void testReshape() throws Exception {
    DoubleMatrix x = newDoubleVector(1, 2, 3, 4).reshape(2, 2);
    assertEquals(2, x.rows());
    assertEquals(2, x.rows());
    assertEquals(1, x.get(0, 0), epsilon);
    assertEquals(4, x.get(1, 1), epsilon);
  }

  @Test
  public void testTranspose() throws Exception {
    DoubleMatrix x = newDoubleVector(1, 2, 3, 1, 2, 3).reshape(3, 2).transpose();
    MatrixAssert.assertValueEquals(x, newDoubleVector(1, 1, 2, 2, 3, 3), epsilon);
  }

  @Test
  public void testShuffle() throws Exception {
    DoubleMatrix x = newDoubleVector(1, 2, 3, 4, 5, 6);
    Matrices.shuffle(x);
    System.out.println(x);
  }

  @Test
  public void testSet() throws Exception {

  }

  @Test
  public void testSet1() throws Exception {

  }

  @Test
  public void testGet() throws Exception {

  }

  @Test
  public void testGet1() throws Exception {

  }

  @Test
  public void testGetRowView() throws Exception {

  }

  @Test
  public void testGetColumnView() throws Exception {

  }

  @Test
  public void testGetDiagonalView() throws Exception {

  }

  @Test
  public void testGetView() throws Exception {

  }

  @Test
  public void testSlice() throws Exception {

  }

  @Test
  public void testSlice1() throws Exception {

  }

  @Test
  public void testSlice2() throws Exception {

  }

  @Test
  public void testSlice3() throws Exception {

  }

  @Test
  public void testSlice4() throws Exception {

  }

  @Test
  public void testSlice5() throws Exception {

  }

  @Test
  public void testSlice6() throws Exception {

  }

  @Test
  public void testCopy() throws Exception {

  }

  @Test
  public void testNewEmptyMatrix() throws Exception {

  }

  @Test
  public void testNewEmptyVector() throws Exception {

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
  public void testMmul4() throws Exception {

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
  public void testRsub() throws Exception {

  }

  @Test
  public void testRsub1() throws Exception {

  }

  @Test
  public void testRsub2() throws Exception {

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
  public void testRdiv() throws Exception {

  }

  @Test
  public void testRdiv1() throws Exception {

  }

  @Test
  public void testRdiv2() throws Exception {

  }

  @Test
  public void testNegate() throws Exception {

  }
}

package org.briljantframework.matrix;

import org.briljantframework.complex.Complex;
import org.briljantframework.matrix.api.MatrixFactory;
import org.briljantframework.matrix.netlib.NetlibMatrixFactory;
import org.junit.Test;

import java.util.DoubleSummaryStatistics;

import static java.util.Arrays.asList;
import static org.briljantframework.matrix.MatrixAssert.assertMatrixEquals;
import static org.briljantframework.matrix.MatrixAssert.assertValueEquals;
import static org.briljantframework.matrix.MatrixAssert.assertValuesEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class AbstractDoubleMatrixTest {

  private final double epsilon = 0.00001;

  private final MatrixFactory bj = NetlibMatrixFactory.getInstance();


  @Test
  public void testAssign() throws Exception {
    DoubleMatrix m = bj.doubleMatrix(3, 3);
    m.assign(3);
    MatrixAssert.assertMatrixEquals(3, m, epsilon);
  }

  @Test
  public void testAssign1() throws Exception {
    DoubleMatrix m = bj.doubleMatrix(3, 3);
    m.assign(() -> 3);
    MatrixAssert.assertMatrixEquals(3, m, epsilon);
  }

  @Test
  public void testAssign2() throws Exception {
    DoubleMatrix m = bj.doubleMatrix(3, 3);
    m.assign(3).update(x -> x * 2);
    MatrixAssert.assertMatrixEquals(6, m, epsilon);
  }

  @Test
  public void testAssign3() throws Exception {
    DoubleMatrix d = bj.doubleMatrix(3, 3).assign(3);
    DoubleMatrix i = bj.doubleMatrix(3, 3).assign(d, x -> (int) x);
    MatrixAssert.assertMatrixEquals(3, i, epsilon);
  }

  @Test
  public void testAssign4() throws Exception {
    ComplexMatrix c = bj.complexMatrix(3, 3).assign(Complex.valueOf(3));
    DoubleMatrix i = bj.doubleMatrix(3, 3).assign(c, Complex::intValue);
    MatrixAssert.assertMatrixEquals(3, i, epsilon);
  }

  @Test
  public void testAssign5() throws Exception {
    DoubleMatrix l = bj.doubleMatrix(3, 3).assign(3L);
    DoubleMatrix i = bj.doubleMatrix(3, 3).assign(l, x -> (int) x);
    MatrixAssert.assertMatrixEquals(3, i, epsilon);
  }

  @Test
  public void testAssign6() throws Exception {
    DoubleMatrix i = bj.doubleMatrix(3, 3).assign(bj.doubleMatrix(3, 3).assign(3));
    MatrixAssert.assertMatrixEquals(3, i, epsilon);
  }

  @Test
  public void testAssign7() throws Exception {
    DoubleMatrix
        i =
        bj.doubleMatrix(3, 3).assign(bj.doubleMatrix(3, 3).assign(3), x -> x * 2);
    MatrixAssert.assertMatrixEquals(6, i, epsilon);
  }

  @Test
  public void testAssign8() throws Exception {
    DoubleMatrix x = bj.doubleMatrix(3, 3).assign(2);
    DoubleMatrix d = bj.doubleMatrix(3, 3).assign(5);
    x.assign(d, Double::sum);
    assertMatrixEquals(epsilon, x, 7);
  }

  @Test
  public void testMap() throws Exception {
    DoubleMatrix i = bj.doubleMatrix(3, 3).assign(3);
    DoubleMatrix m = i.map(Math::sqrt);
    MatrixAssert.assertMatrixEquals(Math.sqrt(3), m, epsilon);
  }

  @Test
  public void testMapToInt() throws Exception {
    DoubleMatrix i = bj.doubleMatrix(3, 3).assign(Integer.MAX_VALUE + 10L);
    IntMatrix l = i.mapToInt(x -> (int) (x - Integer.MAX_VALUE));
    MatrixAssert.assertMatrixEquals(10, l);
  }

  @Test
  public void testMapToLong() throws Exception {
    LongMatrix i = bj.doubleMatrix(3, 3).assign(3.3).mapToLong(Math::round);
    assertMatrixEquals(i, 3);
  }

  @Test
  public void testMapToComplex() throws Exception {
    ComplexMatrix i = bj.doubleMatrix(3, 3).assign(-3).mapToComplex(Complex::sqrt);
    assertMatrixEquals(i, Complex.sqrt(-3));
  }

  @Test
  public void testFilter() throws Exception {
    DoubleMatrix i = bj.matrix(new double[]{0.0, 1, 2, 3, 4, 5, 6}).filter(x -> x > 3);
    assertValueEquals(i, bj.matrix(new double[]{4.0, 5, 6}), epsilon);
  }

  @Test
  public void testSatisfies() throws Exception {
    BitMatrix i = bj.matrix(new int[]{0, 1, 2, 3, 4, 5}).satisfies(x -> x >= 3);
    assertValuesEquals(bj.matrix(new boolean[]{false, false, false, true, true, true}), i);
  }

  @Test
  public void testSatisfies1() throws Exception {
    DoubleMatrix x = bj.doubleMatrix(3, 3);
    DoubleMatrix y = bj.doubleMatrix(3, 3).assign(3);
    BitMatrix z = x.satisfies(y, (a, b) -> a < b);
    assertMatrixEquals(z, true);
  }

  @Test
  public void testReduce() throws Exception {
    DoubleMatrix x = bj.doubleMatrix(3, 3).assign(3);
    double sum = x.reduce(0, Double::sum);
    assertEquals(3 * 9, sum, epsilon);
  }

  @Test
  public void testReduce1() throws Exception {
    DoubleMatrix x = bj.doubleMatrix(3, 3).assign(3);
    double squaredSum = x.reduce(0, Double::sum, i -> i * 2);
    assertEquals(3 * 2 * 9, squaredSum, epsilon);
  }

  @Test
  public void testReduceColumns() throws Exception {
    DoubleMatrix
        x =
        bj.doubleMatrix(3, 4).assign(3).reduceColumns(y -> y.reduce(0, Double::sum));
    assertEquals(4, x.columns());
    assertEquals(1, x.rows());
    MatrixAssert.assertMatrixEquals(3 * 3, x, epsilon);
  }

  @Test
  public void testReduceRows() throws Exception {
    DoubleMatrix
        x =
        bj.doubleMatrix(4, 3).assign(3).reduceRows(y -> y.reduce(0, Double::sum));
    assertEquals(4, x.rows());
    assertEquals(1, x.columns());
    MatrixAssert.assertMatrixEquals(3 * 3, x, epsilon);
  }

  @Test
  public void testReshape() throws Exception {
    DoubleMatrix x = bj.matrix(new double[]{1.0, 2, 3, 4, 5, 6}).reshape(2, 3);
    assertEquals(2, x.rows());
    assertEquals(3, x.columns());
  }

  @Test
  public void testGet() throws Exception {
    DoubleMatrix x = bj.matrix(new double[]{0.0, 1, 2, 3, 4, 5}).reshape(3, 2);
    assertEquals(0, x.get(0), epsilon);
    assertEquals(5, x.get(5), epsilon);
  }

  @Test
  public void testGet1() throws Exception {
    DoubleMatrix x = bj.matrix(new double[]{0.0, 1, 2, 3, 4, 5}).reshape(3, 2);
    assertEquals(0, x.get(0, 0), epsilon);
    assertEquals(3, x.get(0, 1), epsilon);
    assertEquals(4, x.get(1, 1), epsilon);
  }

  @Test
  public void testSet() throws Exception {
    DoubleMatrix x = bj.doubleMatrix(3, 3);
    x.set(0, 0, 1);
    x.set(0, 1, 2);
    x.set(1, 1, 3);

    assertEquals(1, x.get(0, 0), epsilon);
    assertEquals(2, x.get(0, 1), epsilon);
    assertEquals(3, x.get(1, 1), epsilon);
  }

  @Test
  public void testSet1() throws Exception {
    DoubleMatrix x = bj.matrix(new double[]{0.0, 1, 2, 3});
    assertEquals(0, x.get(0), epsilon);
    assertEquals(1, x.get(1), epsilon);
    assertEquals(2, x.get(2), epsilon);
    assertEquals(3, x.get(3), epsilon);
  }

  // @Test
  // public void testAddTo() throws Exception {
  // DoubleMatrix x = newMatrix(1, 1, 1, 1);
  // x.addTo(0, 10);
  // assertEquals(11, x.get(0));
  // }
  //
  // @Test
  // public void testAddTo1() throws Exception {
  // DoubleMatrix x = newMatrix(1, 1, 1, 1).reshape(2, 2);
  // x.addTo(0, 0, 10);
  // x.addTo(0, 1, 10);
  // assertEquals(11, x.get(0, 0));
  // assertEquals(11, x.get(0, 1));
  // }
  //
  // @Test
  // public void testUpdate() throws Exception {
  // DoubleMatrix x = newMatrix(1, 1, 1, 1).reshape(2, 2);
  // x.update(0, 0, i -> i * 3);
  // assertEquals(3, x.get(0, 0));
  // }
  //
  // @Test
  // public void testUpdate1() throws Exception {
  // DoubleMatrix x = newMatrix(1, 1, 1, 1);
  // x.update(0, i -> i * 3);
  // assertEquals(3, x.get(0));
  // }

  @Test
  public void testGetRowView() throws Exception {
    DoubleMatrix x = bj.matrix(new double[]{1.0, 2, 3, 1, 2, 3, 1, 2, 3}).reshape(3, 3);
    MatrixAssert.assertMatrixEquals(1, x.getRowView(0), epsilon);
    MatrixAssert.assertMatrixEquals(2, x.getRowView(1), epsilon);
    MatrixAssert.assertMatrixEquals(3, x.getRowView(2), epsilon);
  }

  @Test
  public void testGetColumnView() throws Exception {
    DoubleMatrix x = bj.matrix(new double[]{1.0, 1, 1, 2, 2, 2, 3, 3, 3}).reshape(3, 3);
    MatrixAssert.assertMatrixEquals(1, x.getColumnView(0), epsilon);
    MatrixAssert.assertMatrixEquals(2, x.getColumnView(1), epsilon);
    MatrixAssert.assertMatrixEquals(3, x.getColumnView(2), epsilon);
  }

  @Test
  public void testGetDiagonalView() throws Exception {

  }

  @Test
  public void testGetView() throws Exception {
    DoubleMatrix x = bj.matrix(new double[]{1.0, 1, 1, 1, 2, 2}).reshape(2, 3);
    MatrixAssert.assertMatrixEquals(1, x.getView(0, 0, 2, 2), epsilon);
  }

  @Test
  public void testTranspose() throws Exception {
    DoubleMatrix x = bj.matrix(new double[]{1.0, 2, 3, 1, 2, 3}).reshape(2, 3).transpose();
    assertEquals(3, x.rows());
    assertEquals(2, x.columns());
    assertEquals(1, x.get(0, 0), epsilon);
    assertEquals(3, x.get(2, 1), epsilon);
  }

  @Test
  public void testCopy() throws Exception {
    DoubleMatrix x = bj.matrix(new double[]{1.0, 1, 1, 1});
    DoubleMatrix y = x.copy();
    x.set(0, 1000);
    assertEquals(1, y.get(0), epsilon);
  }

  @Test
  public void testNewEmptyMatrix() throws Exception {
    assertNotNull(bj.doubleMatrix(2, 2).newEmptyMatrix(2, 2));
  }

  @Test
  public void testNewEmptyVector() throws Exception {
    DoubleMatrix x = bj.doubleMatrix(2, 2).newEmptyVector(2);
    assertNotNull(x);
    assertEquals(2, x.rows());
    assertEquals(1, x.columns());
  }

  @Test
  public void testMmul() throws Exception {
    DoubleMatrix x = bj.matrix(new double[]{1.0, 2, 3, 4, 5, 6}).reshape(3, 2);
    DoubleMatrix y = bj.matrix(new double[]{1.0, 2, 3, 4, 5, 6}).reshape(2, 3);

    DoubleMatrix z = y.mmul(x);
    DoubleMatrix za = bj.matrix(new double[]{22.0, 28, 49, 64}).reshape(2, 2);
    assertMatrixEquals(za, z, epsilon);

    z = x.mmul(y);
    za = bj.matrix(new double[]{9.0, 12, 15, 19, 26, 33, 29, 40, 51}).reshape(3, 3);
    assertMatrixEquals(za, z, epsilon);
  }

  @Test
  public void testMmul1() throws Exception {
    DoubleMatrix x = bj.matrix(new double[]{1.0, 2, 3, 4, 5, 6}).reshape(3, 2);
    DoubleMatrix y = bj.matrix(new double[]{1.0, 2, 3, 4, 5, 6}).reshape(2, 3);
    DoubleMatrix z = y.mmul(2, x);
    DoubleMatrix za = bj.matrix(new double[]{44.0, 56, 98, 128}).reshape(2, 2);
    assertMatrixEquals(za, z, epsilon);

    z = x.mmul(4, y);
    za = bj.matrix(new double[]{36.0, 48, 60, 76, 104, 132, 116, 160, 204}).reshape(3, 3);
    assertMatrixEquals(za, z, epsilon);
  }

  @Test
  public void testMmul2() throws Exception {
    DoubleMatrix x = bj.matrix(new double[]{1.0, 2, 3, 4, 5, 6}).reshape(3, 2);
    DoubleMatrix y = bj.matrix(new double[]{1.0, 2, 3, 4, 5, 6}).reshape(3, 2);

    DoubleMatrix z = y.mmul(Transpose.YES, x, Transpose.NO);
    DoubleMatrix za = bj.matrix(new double[]{14.0, 32, 32, 77}).reshape(2, 2);
    assertMatrixEquals(za, z, epsilon);

    z = x.mmul(Transpose.NO, y, Transpose.YES);
    za = bj.matrix(new double[]{17, 22, 27.0, 22, 29, 36, 27, 36, 45}).reshape(3, 3);
    assertMatrixEquals(za, z, epsilon);
  }

  @Test
  public void testMmul3() throws Exception {
    DoubleMatrix x = bj.matrix(new double[]{1, 2.0, 3, 4, 5, 6}).reshape(3, 2);
    DoubleMatrix y = bj.matrix(new double[]{1, 2.0, 3, 4, 5, 6}).reshape(3, 2);
    DoubleMatrix z = y.mmul(2, Transpose.YES, x, Transpose.NO);
    DoubleMatrix za = bj.matrix(new double[]{28.0, 64, 64, 154}).reshape(2, 2);
    assertMatrixEquals(za, z, epsilon);

    z = x.mmul(2, Transpose.NO, y, Transpose.YES);
    za = bj.matrix(new double[]{34.0, 44, 54, 44, 58, 72, 54, 72, 90}).reshape(3, 3);
    assertMatrixEquals(za, z, epsilon);
  }

  @Test
  public void testMul() throws Exception {
    DoubleMatrix x = bj.doubleMatrix(3, 3).assign(3);
    DoubleMatrix z = x.mul(2);
    MatrixAssert.assertMatrixEquals(6, z, epsilon);
  }

  @Test
  public void testMul1() throws Exception {
    DoubleMatrix x = bj.doubleMatrix(3, 3).assign(3);
    DoubleMatrix y = bj.doubleMatrix(3, 3).assign(2);
    DoubleMatrix z = x.mul(y);
    MatrixAssert.assertMatrixEquals(6, z, epsilon);
  }

  @Test
  public void testMul2() throws Exception {
    DoubleMatrix x = bj.doubleMatrix(3, 3).assign(3);
    DoubleMatrix y = bj.doubleMatrix(3, 3).assign(2);
    DoubleMatrix z = x.mul(-1, y, -1);
    MatrixAssert.assertMatrixEquals(6, z, epsilon);
  }

  @Test
  public void testAdd() throws Exception {
    DoubleMatrix x = bj.doubleMatrix(3, 3).assign(2);
    MatrixAssert.assertMatrixEquals(5, x.add(3), epsilon);
  }

  @Test
  public void testAdd1() throws Exception {
    DoubleMatrix x = bj.doubleMatrix(3, 3).assign(2);
    DoubleMatrix y = bj.doubleMatrix(3, 3).assign(3);
    MatrixAssert.assertMatrixEquals(5, x.add(y), epsilon);
  }

  @Test
  public void testAdd2() throws Exception {
    DoubleMatrix x = bj.doubleMatrix(3, 3).assign(2);
    DoubleMatrix y = bj.doubleMatrix(3, 3).assign(3);
    MatrixAssert.assertMatrixEquals(-1, x.add(1, y, -1), epsilon);
  }

  @Test
  public void testSub() throws Exception {
    DoubleMatrix x = bj.doubleMatrix(3, 3).assign(3);
    MatrixAssert.assertMatrixEquals(1, x.sub(2), epsilon);
  }

  @Test
  public void testSub1() throws Exception {
    DoubleMatrix x = bj.doubleMatrix(3, 3).assign(3);
    DoubleMatrix y = bj.doubleMatrix(3, 3).assign(2);
    MatrixAssert.assertMatrixEquals(1, x.sub(y), epsilon);
  }

  @Test
  public void testSub2() throws Exception {
    DoubleMatrix x = bj.doubleMatrix(3, 3).assign(3);
    DoubleMatrix y = bj.doubleMatrix(3, 3).assign(2);
    MatrixAssert.assertMatrixEquals(5, x.sub(1, y, -1), epsilon);
  }

  @Test
  public void testRsub() throws Exception {
    DoubleMatrix x = bj.doubleMatrix(3, 3).assign(2);
    DoubleMatrix y = x.rsub(3);
    MatrixAssert.assertMatrixEquals(1, y, epsilon);
  }

  @Test
  public void testDiv() throws Exception {
    DoubleMatrix x = bj.doubleMatrix(3, 3).assign(4);
    DoubleMatrix y = x.div(2);
    MatrixAssert.assertMatrixEquals(2, y, epsilon);
  }

  @Test
  public void testDiv1() throws Exception {
    DoubleMatrix x = bj.doubleMatrix(3, 3).assign(4);
    DoubleMatrix y = bj.doubleMatrix(3, 3).assign(2);
    DoubleMatrix z = x.div(y);
    MatrixAssert.assertMatrixEquals(2, z, epsilon);
  }

  @Test
  public void testRdiv() throws Exception {
    DoubleMatrix x = bj.doubleMatrix(3, 3).assign(6);
    DoubleMatrix y = x.rdiv(12);
    MatrixAssert.assertMatrixEquals(2, y, epsilon);
  }

  @Test
  public void testNegate() throws Exception {
    DoubleMatrix x = bj.doubleMatrix(3, 3).assign(3).negate();
    MatrixAssert.assertMatrixEquals(-3, x, epsilon);
  }

  @Test
  public void testSlice1() throws Exception {
    DoubleMatrix x = bj.matrix(new double[]{1.0, 2, 3, 1, 2, 3}).reshape(3, 2);
    DoubleMatrix slice = x.slice(bj.range(3));
    assertValueEquals(slice, bj.matrix(new double[]{1.0, 2, 3}), epsilon);
  }

  @Test
  public void testSlice2() throws Exception {
    DoubleMatrix x = bj.matrix(new double[]{1.0, 2, 3, 1, 2, 3, 1, 2, 3}).reshape(3, 3);
    DoubleMatrix slice = x.slice(bj.range(2), Dim.R);
    assertEquals(2, slice.rows());
    assertValueEquals(slice.getRowView(0), bj.matrix(new double[]{1.0, 1, 1}), epsilon);
    assertValueEquals(slice.getRowView(1), bj.matrix(new double[]{2.0, 2, 2}), epsilon);
  }

  @Test
  public void testSlice3() throws Exception {
    DoubleMatrix x = bj.matrix(new double[]{1.0, 2, 3, 1, 2, 3, 1, 2, 3}).reshape(3, 3);
    DoubleMatrix s = x.slice(bj.range(2), bj.range(2));
    assertEquals(2, s.rows());
    assertEquals(2, s.columns());
    assertValueEquals(s.getRowView(0), bj.matrix(new double[]{1.0, 1}), epsilon);
    assertValueEquals(s.getRowView(1), bj.matrix(new double[]{2.0, 2}), epsilon);
  }

  @Test
  public void testSlice4() throws Exception {
    DoubleMatrix x = bj.matrix(new double[]{1, 2, 3, 1, 2, 3.0, 1, 2, 3}).reshape(3, 3);
    DoubleMatrix s = x.slice(asList(0, 2, 5, 7));
    assertValueEquals(s, bj.matrix(new double[]{1, 3, 3, 2.0}), epsilon);
  }

  @Test
  public void testSlice5() throws Exception {
    DoubleMatrix x = bj.matrix(new double[]{1, 2, 3, 1, 2, 3, 1, 2, 3.0}).reshape(3, 3);
    DoubleMatrix s = x.slice(asList(0, 2), Dim.R);
    assertValueEquals(s.getRowView(0), bj.matrix(new double[]{1, 1, 1.0}), epsilon);
    assertValueEquals(s.getRowView(1), bj.matrix(new double[]{3, 3, 3.0}), epsilon);
  }

  @Test
  public void testSlice6() throws Exception {
    DoubleMatrix x = bj.matrix(new double[]{1, 2, 3, 1, 2, 3, 1, 2, 3.0}).reshape(3, 3);
    DoubleMatrix s = x.slice(asList(0, 1), asList(0, 1));
    assertMatrixEquals(1, s.getRowView(0), 1);
    assertMatrixEquals(2, s.getRowView(1), 2);
  }

  @Test
  public void testSlice7() throws Exception {
    DoubleMatrix x = bj.matrix(new double[]{1, 2, 3, 1, 2, 3, 1, 2, 3.0}).reshape(3, 3);
    BitMatrix bits =
        bj.matrix(new boolean[]{true, true, true, false, false, false, false, false, false})
            .reshape(3, 3);
    DoubleMatrix s = x.slice(bits);
    assertValueEquals(s, bj.matrix(new double[]{1, 2, 3.0}), epsilon);
  }

  @Test
  public void testSlice() throws Exception {
    DoubleMatrix x = bj.matrix(new double[]{1, 2, 3, 1, 2, 3, 1, 2, 3.0}).reshape(3, 3);
    DoubleMatrix s = x.slice(bj.matrix(new boolean[]{true, false, true}), Dim.R);
    assertValueEquals(s.getRowView(0), bj.matrix(new double[]{1, 1, 1.0}), epsilon);
    assertValueEquals(s.getRowView(1), bj.matrix(new double[]{3, 3, 3.0}), epsilon);
  }

  @Test
  public void testSwap() throws Exception {
    DoubleMatrix x = bj.matrix(new double[]{1, 2, 3.0});
    x.swap(0, 2);
    assertValueEquals(x, bj.matrix(new double[]{3, 2, 1.0}), epsilon);
  }

  @Test
  public void testSetRow() throws Exception {
    DoubleMatrix x = bj.doubleMatrix(3, 3);
    x.setRow(0, bj.matrix(new double[]{1, 2, 3.0}));
    assertValueEquals(x.getRowView(0), bj.matrix(new double[]{1, 2, 3.0}), epsilon);
  }

  @Test
  public void testSetColumn() throws Exception {
    DoubleMatrix x = bj.doubleMatrix(3, 3);
    x.setColumn(0, bj.matrix(new double[]{1, 2, 3.0}));
    assertValueEquals(x.getColumnView(0), bj.matrix(new double[]{1, 2, 3.0}), epsilon);
  }

  @Test
  public void testSliceReshape() throws Exception {
    DoubleMatrix a = Matrices.linspace(0, 1, 9).reshape(3, 3);
    System.out.println(a);
    DoubleMatrix b = a.slice(bj.range(2), bj.range(2));
    System.out.println(b);
    System.out.println(b.reshape(4, 1));
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
  public void testReduceRows3() throws Exception {
    DoubleMatrix x = bj.doubleMatrix(3, 3).assign(3);
    DoubleMatrix rowSums = x.reduceRows(Matrices::std);
    assertMatrixEquals(0, rowSums, epsilon);
  }

  @Test
  public void testIterator() throws Exception {
    DoubleMatrix x = bj.matrix(new double[]{1, 2, 3, 4, 5, 6.0});
    int i = 0;
    for (double v : x.flat()) {
      assertEquals(x.get(i++), v, epsilon);
    }
  }

  @Test
  public void testStream() throws Exception {
    DoubleMatrix m = bj.doubleMatrix(3, 3).assign(3);
    DoubleSummaryStatistics s = m.stream().summaryStatistics();
    assertEquals(3 * 3 * 3, s.getSum(), epsilon);
  }
}

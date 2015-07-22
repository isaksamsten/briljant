package org.briljantframework.array;

import org.briljantframework.array.api.ArrayFactory;
import org.briljantframework.array.netlib.NetlibArrayBackend;
import org.briljantframework.complex.Complex;
import org.junit.Test;

import static java.util.Arrays.asList;
import static org.briljantframework.array.MatrixAssert.assertMatrixEquals;
import static org.briljantframework.array.MatrixAssert.assertValuesEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class AbstractComplexArrayTest {

  private final ArrayFactory bj = new NetlibArrayBackend().getArrayFactory();


  @Test
  public void testRsub2() throws Exception {
    // ComplexMatrix x = Matrices.newComplexVector(1, 2, 3);
    // ComplexMatrix y = bj.complexMatrix(3, 3).assign(3);
    // ComplexMatrix z = y.rsub(x, Axis.ROW);
    // MatrixAssert.assertValuesEquals(z.getRowView(0), newComplexVector(-2, -1, 0));
    // MatrixAssert.assertValuesEquals(z.getRowView(1), newComplexVector(-2, -1, 0));
    // MatrixAssert.assertValuesEquals(z.getRowView(2), newComplexVector(-2, -1, 0));
    //
    // z = y.rsub(x, Axis.COLUMN);
    // MatrixAssert.assertValuesEquals(z.getColumnView(0), newComplexVector(-2, -1, 0));
    // MatrixAssert.assertValuesEquals(z.getColumnView(1), newComplexVector(-2, -1, 0));
    // MatrixAssert.assertValuesEquals(z.getColumnView(2), newComplexVector(-2, -1, 0));
  }

  @Test
  public void testAssign() throws Exception {
    ComplexArray m = bj.complexArray();
    m.assign(3);
    assertMatrixEquals(3, m);
  }

  @Test
  public void testAssign1() throws Exception {
    ComplexArray m = bj.complexArray();
    m.assign(() -> Complex.valueOf(3));
    assertMatrixEquals(3, m);
  }

  @Test
  public void testAssign2() throws Exception {
    ComplexArray m = bj.complexArray();
    m.assign(3).update(x -> x.multiply(2));
    assertMatrixEquals(6, m);
  }

  @Test
  public void testAssign3() throws Exception {
    DoubleArray d = bj.doubleArray(3, 3).assign(3);
    ComplexArray i = bj.complexArray().assign(d, Complex::valueOf);
    assertMatrixEquals(3, i);
  }

  @Test
  public void testAssign4() throws Exception {
    // ComplexMatrix c = bj.complexMatrix(3, 3).assign(Complex.valueOf(3));
    // ComplexMatrix i = bj.complexMatrix(3, 3).assign(c, Complex::intValue);
    // assertMatrixEquals(i, 3);
  }

  @Test
  public void testAssign5() throws Exception {
    // ComplexMatrix l = bj.complexMatrix(3, 3).assign(3L);
    // ComplexMatrix i = bj.complexMatrix(3, 3).assign(l, x -> (int) x);
    // assertMatrixEquals(i, 3);
  }

  @Test
  public void testAssign6() throws Exception {
    ComplexArray i = bj.complexArray().assign(bj.complexArray().assign(3));
    assertMatrixEquals(3, i);
  }

  @Test
  public void testAssign7() throws Exception {
    ComplexArray i =
        bj.complexArray().assign(bj.complexArray().assign(3), x -> x.multiply(2));
    assertMatrixEquals(6, i);
  }

  @Test
  public void testAssign8() throws Exception {
    ComplexArray x = bj.complexArray().assign(2);
    ComplexArray d = bj.complexArray().assign(5);
    x.assign(d, (a, b) -> a.plus(b));
    assertMatrixEquals(7, x);
  }

  @Test
  public void testMap() throws Exception {
    ComplexArray i = bj.complexArray().assign(3);
    ComplexArray m = i.map(Complex::sqrt);
    assertMatrixEquals(m, Complex.sqrt(3));
  }

  @Test
  public void testMapToInt() throws Exception {
    ComplexArray i = bj.complexArray().assign(10);
    IntArray l = i.mapToInt(x -> (int) (x.real()));
    assertMatrixEquals(10, l);
  }

  @Test
  public void testMapToDouble() throws Exception {
    DoubleArray i =
        bj.complexArray().assign(3).mapToDouble((complex) -> complex.sqrt().real());
    assertMatrixEquals(Math.sqrt(3), i, 0.0001);
  }

  public void testMapToLong() throws Exception {
    LongArray i = bj.complexArray().assign(-3).mapToLong(x -> (long) x.real());
    assertMatrixEquals(i, -3);
  }

  @Test
  public void testFilter() throws Exception {
    ComplexArray i = bj.complexArray(new double[]{0, 1, 2, 3, 4, 5, 6}).filter(x -> x.real() > 3);
    assertValuesEquals(bj.complexArray(new double[]{4, 5, 6}), i);
  }

  @Test
  public void testSatisfies() throws Exception {
    BitArray i = bj.complexArray(new double[]{0, 1, 2, 3, 4, 5}).satisfies(x -> x.real() >= 3);
    assertValuesEquals(bj.array(new boolean[]{false, false, false, true, true, true}), i);
  }

  @Test
  public void testSatisfies1() throws Exception {
    ComplexArray x = bj.complexArray();
    ComplexArray y = bj.complexArray().assign(3);
    BitArray z = x.satisfies(y, (a, b) -> a.real() < b.real());
    assertMatrixEquals(z, true);
  }

  @Test
  public void testReduce() throws Exception {
    ComplexArray x = bj.complexArray().assign(3);
    Complex sum = x.reduce(Complex.ZERO, Complex::plus);
    assertEquals(Complex.valueOf(3 * 9), sum);
  }

  @Test
  public void testReduce1() throws Exception {
    ComplexArray x = bj.complexArray().assign(3);
    Complex squaredSum = x.reduce(Complex.ZERO, Complex::plus, i -> i.multiply(2));
    assertEquals(Complex.valueOf(3 * 2 * 9), squaredSum);
  }

  @Test
  public void testReduceColumns() throws Exception {
    ComplexArray x =
        bj.complexArray().assign(3).reduceColumns(y -> y.reduce(Complex.ZERO, Complex::plus));
    assertEquals(4, x.columns());
    assertEquals(1, x.rows());
    assertMatrixEquals(3 * 3, x);
  }

  @Test
  public void testReduceRows() throws Exception {
    ComplexArray x =
        bj.complexArray().assign(3).reduceRows(y -> y.reduce(Complex.ZERO, Complex::plus));
    assertEquals(4, x.rows());
    assertEquals(1, x.columns());
    assertMatrixEquals(3 * 3, x);
  }

  @Test
  public void testReshape() throws Exception {
    ComplexArray x = bj.complexArray(new double[]{1, 2, 3, 4, 5, 6}).reshape(2, 3);
    assertEquals(2, x.rows());
    assertEquals(3, x.columns());
  }

  @Test
  public void testGet() throws Exception {
    ComplexArray x = bj.complexArray(new double[]{0, 1, 2, 3, 4, 5}).reshape(3, 2);
    assertEquals(Complex.valueOf(0), x.get(0));
    assertEquals(Complex.valueOf(5), x.get(5));
  }

  @Test
  public void testGet1() throws Exception {
    ComplexArray x = bj.complexArray(new double[]{0, 1, 2, 3, 4, 5}).reshape(3, 2);
    assertEquals(Complex.valueOf(0), x.get(0, 0));
    assertEquals(Complex.valueOf(3), x.get(0, 1));
    assertEquals(Complex.valueOf(4), x.get(1, 1));
  }

  @Test
  public void testSet() throws Exception {
    ComplexArray x = bj.complexArray();
    x.set(0, 0, Complex.valueOf(1));
    x.set(0, 1, Complex.valueOf(2));
    x.set(1, 1, Complex.valueOf(3));

    assertEquals(Complex.valueOf(1), x.get(0, 0));
    assertEquals(Complex.valueOf(2), x.get(0, 1));
    assertEquals(Complex.valueOf(3), x.get(1, 1));
  }

  // @Test
  // public void testAddTo() throws Exception {
  // ComplexMatrix x = bj.complexMatrix(1, 1, 1, 1);
  // x.addTo(0, 10);
  // assertEquals(11, x.get(0));
  // }
  //
  // @Test
  // public void testAddTo1() throws Exception {
  // ComplexMatrix x = bj.complexMatrix(1, 1, 1, 1).reshape(2, 2);
  // x.addTo(0, 0, 10);
  // x.addTo(0, 1, 10);
  // assertEquals(11, x.get(0, 0));
  // assertEquals(11, x.get(0, 1));
  // }
  //
  // @Test
  // public void testUpdate() throws Exception {
  // ComplexMatrix x = bj.complexMatrix(1, 1, 1, 1).reshape(2, 2);
  // x.update(0, 0, i -> i * 3);
  // assertEquals(3, x.get(0, 0));
  // }
  //
  // @Test
  // public void testUpdate1() throws Exception {
  // ComplexMatrix x = bj.complexMatrix(1, 1, 1, 1);
  // x.update(0, i -> i * 3);
  // assertEquals(3, x.get(0));
  // }

  @Test
  public void testSet1() throws Exception {
    ComplexArray x = bj.complexArray(new double[]{0, 1, 2, 3});
    assertEquals(Complex.valueOf(0), x.get(0));
    assertEquals(Complex.valueOf(1), x.get(1));
    assertEquals(Complex.valueOf(2), x.get(2));
    assertEquals(Complex.valueOf(3), x.get(3));
  }

  @Test
  public void testGetRowView() throws Exception {
    ComplexArray x = bj.complexArray(new double[]{1, 2, 3, 1, 2, 3, 1, 2, 3}).reshape(3, 3);
    assertMatrixEquals(1, x.getRow(0));
    assertMatrixEquals(2, x.getRow(1));
    assertMatrixEquals(3, x.getRow(2));
  }

  @Test
  public void testGetColumnView() throws Exception {
    ComplexArray x = bj.complexArray(new double[]{1, 1, 1, 2, 2, 2, 3, 3, 3}).reshape(3, 3);
    assertMatrixEquals(1, x.getColumn(0));
    assertMatrixEquals(2, x.getColumn(1));
    assertMatrixEquals(3, x.getColumn(2));
  }

  @Test
  public void testGetDiagonalView() throws Exception {

  }

  @Test
  public void testGetView() throws Exception {
    ComplexArray x = bj.complexArray(new double[]{1, 1, 1, 1, 2, 2}).reshape(2, 3);
    assertMatrixEquals(1, x.getView(0, 0, 2, 2));
  }

  @Test
  public void testTranspose() throws Exception {
    ComplexArray x = bj.complexArray(new double[]{1, 2, 3, 1, 2, 3}).reshape(2, 3).transpose();
    assertEquals(3, x.rows());
    assertEquals(2, x.columns());
    assertEquals(Complex.valueOf(1), x.get(0, 0));
    assertEquals(Complex.valueOf(3), x.get(2, 1));
  }

  @Test
  public void testCopy() throws Exception {
    ComplexArray x = bj.complexArray(new double[]{1, 1, 1, 1});
    ComplexArray y = x.copy();
    x.set(0, Complex.valueOf(1000));
    assertEquals(Complex.valueOf(1), y.get(0));
  }

  @Test
  public void testNewEmptyMatrix() throws Exception {
    assertNotNull(bj.complexArray().newEmptyArray(2, 2));
  }

  @Test
  public void testNewEmptyVector() throws Exception {
    ComplexArray x = bj.complexArray().newEmptyArray(new int[]{2});
    assertNotNull(x);
    assertEquals(2, x.rows());
    assertEquals(1, x.columns());
  }

  @Test
  public void testMmul() throws Exception {
    ComplexArray x = bj.complexArray(new double[]{1, 2, 3, 4, 5, 6}).reshape(3, 2);
    ComplexArray y = bj.complexArray(new double[]{1, 2, 3, 4, 5, 6}).reshape(2, 3);

    ComplexArray z = y.mmul(x);
    ComplexArray za = bj.complexArray(new double[]{22, 28, 49, 64}).reshape(2, 2);
    assertMatrixEquals(z, za);

    z = x.mmul(y);
    za = bj.complexArray(new double[]{9, 12, 15, 19, 26, 33, 29, 40, 51}).reshape(3, 3);
    assertMatrixEquals(z, za);
  }

  @Test
  public void testMmul1() throws Exception {
    ComplexArray x = bj.complexArray(new double[]{1, 2, 3, 4, 5, 6}).reshape(3, 2);
    ComplexArray y = bj.complexArray(new double[]{1, 2, 3, 4, 5, 6}).reshape(2, 3);

    ComplexArray z = y.mmul(Complex.valueOf(2), x);
    ComplexArray za = bj.complexArray(new double[]{44, 56, 98, 128}).reshape(2, 2);
    assertMatrixEquals(z, za);

    z = x.mmul(Complex.valueOf(4), y);
    za = bj.complexArray(new double[]{36, 48, 60, 76, 104, 132, 116, 160, 204}).reshape(3, 3);
    assertMatrixEquals(z, za);
  }

  @Test
  public void testMmul2() throws Exception {
    ComplexArray x = bj.complexArray(new double[]{1, 2, 3, 4, 5, 6}).reshape(3, 2);
    ComplexArray y = bj.complexArray(new double[]{1, 2, 3, 4, 5, 6}).reshape(3, 2);

    ComplexArray z = y.mmul(Op.TRANSPOSE, x, Op.KEEP);
    ComplexArray za = bj.complexArray(new double[]{14, 32, 32, 77}).reshape(2, 2);
    assertMatrixEquals(z, za);

    z = x.mmul(Op.KEEP, y, Op.TRANSPOSE);
    za = bj.complexArray(new double[]{17, 22, 27, 22, 29, 36, 27, 36, 45}).reshape(3, 3);
    assertMatrixEquals(z, za);
  }

  @Test
  public void testMmul3() throws Exception {
    ComplexArray x = bj.complexArray(new double[]{1, 2, 3, 4, 5, 6}).reshape(3, 2);
    ComplexArray y = bj.complexArray(new double[]{1, 2, 3, 4, 5, 6}).reshape(3, 2);
    ComplexArray z = y.mmul(Complex.valueOf(2), Op.TRANSPOSE, x, Op.KEEP);
    ComplexArray za = bj.complexArray(new double[]{28, 64, 64, 154}).reshape(2, 2);
    assertMatrixEquals(z, za);

    z = x.mmul(Complex.valueOf(2), Op.KEEP, y, Op.TRANSPOSE);
    za = bj.complexArray(new double[]{34, 44, 54, 44, 58, 72, 54, 72, 90}).reshape(3, 3);
    assertMatrixEquals(z, za);
  }

  @Test
  public void testMul() throws Exception {
    ComplexArray x = bj.complexArray().assign(3);
    ComplexArray z = x.mul(Complex.valueOf(2));
    assertMatrixEquals(6, z);
  }

  @Test
  public void testMul1() throws Exception {
    ComplexArray x = bj.complexArray().assign(3);
    ComplexArray y = bj.complexArray().assign(2);
    ComplexArray z = x.mul(y);
    assertMatrixEquals(6, z);
  }

  @Test
  public void testMul2() throws Exception {
    ComplexArray x = bj.complexArray().assign(3);
    ComplexArray y = bj.complexArray().assign(2);
    ComplexArray z = x.mul(Complex.valueOf(-1), y, Complex.valueOf(-1));
    assertMatrixEquals(6, z);
  }

  @Test
  public void testMul3() throws Exception {
    // ComplexMatrix x = newComplexVector(1, 2, 3);
    // ComplexMatrix y = bj.complexMatrix(3, 3).assign(3);
    // ComplexMatrix z = y.mul(x, Axis.ROW);
    // assertMatrixEquals(z.getColumnView(0), 3);
    // assertMatrixEquals(z.getColumnView(1), 6);
    // assertMatrixEquals(z.getColumnView(2), 9);
    //
    // z = y.mul(x, Axis.COLUMN);
    // assertMatrixEquals(z.getRowView(0), 3);
    // assertMatrixEquals(z.getRowView(1), 6);
    // assertMatrixEquals(z.getRowView(2), 9);
  }

  @Test
  public void testMul4() throws Exception {
    // ComplexMatrix x = newComplexVector(1, 2, 3);
    // ComplexMatrix y = bj.complexMatrix(3, 3).assign(3);
    // ComplexMatrix z = y.mul(1, x, -1, Axis.ROW);
    // assertMatrixEquals(z.getColumnView(0), -3);
    // assertMatrixEquals(z.getColumnView(1), -6);
    // assertMatrixEquals(z.getColumnView(2), -9);
    //
    // z = y.mul(1, x, -1, Axis.COLUMN);
    // assertMatrixEquals(z.getRowView(0), -3);
    // assertMatrixEquals(z.getRowView(1), -6);
    // assertMatrixEquals(z.getRowView(2), -9);
  }

  @Test
  public void testAdd() throws Exception {
    ComplexArray x = bj.complexArray().assign(2);
    assertMatrixEquals(5, x.add(Complex.valueOf(3)));
  }

  @Test
  public void testAdd1() throws Exception {
    ComplexArray x = bj.complexArray().assign(2);
    ComplexArray y = bj.complexArray().assign(3);
    assertMatrixEquals(5, x.add(y));
  }

  @Test
  public void testAdd2() throws Exception {
    ComplexArray x = bj.complexArray().assign(2);
    ComplexArray y = bj.complexArray().assign(3);
    assertMatrixEquals(-1, x.add(Complex.ONE, y, Complex.valueOf(-1)));
  }

  @Test
  public void testAdd3() throws Exception {
    // ComplexMatrix x = newComplexVector(1, 2, 3);
    // ComplexMatrix y = bj.complexMatrix(3, 3).assign(3);
    // ComplexMatrix z = y.add(x, Axis.ROW);
    // MatrixAssert.assertValuesEquals(z.getRowView(0), newComplexVector(4, 5, 6));
    // MatrixAssert.assertValuesEquals(z.getRowView(1), newComplexVector(4, 5, 6));
    // MatrixAssert.assertValuesEquals(z.getRowView(2), newComplexVector(4, 5, 6));
    //
    // z = y.add(x, Axis.COLUMN);
    // MatrixAssert.assertValuesEquals(z.getColumnView(0), newComplexVector(4, 5, 6));
    // MatrixAssert.assertValuesEquals(z.getColumnView(1), newComplexVector(4, 5, 6));
    // MatrixAssert.assertValuesEquals(z.getColumnView(2), newComplexVector(4, 5, 6));
  }

  @Test
  public void testAdd4() throws Exception {
    // ComplexMatrix x = newComplexVector(1, 2, 3);
    // ComplexMatrix y = bj.complexMatrix(3, 3).assign(3);
    // ComplexMatrix z = y.add(1, x, -1, Axis.ROW);
    // MatrixAssert.assertValuesEquals(z.getRowView(0), newComplexVector(2, 1, 0));
    // MatrixAssert.assertValuesEquals(z.getRowView(1), newComplexVector(2, 1, 0));
    // MatrixAssert.assertValuesEquals(z.getRowView(2), newComplexVector(2, 1, 0));
    //
    // z = y.add(1, x, -1, Axis.COLUMN);
    // MatrixAssert.assertValuesEquals(z.getColumnView(0), newComplexVector(2, 1, 0));
    // MatrixAssert.assertValuesEquals(z.getColumnView(1), newComplexVector(2, 1, 0));
    // MatrixAssert.assertValuesEquals(z.getColumnView(2), newComplexVector(2, 1, 0));
  }

  @Test
  public void testSub() throws Exception {
    ComplexArray x = bj.complexArray().assign(3);
    assertMatrixEquals(1, x.sub(Complex.valueOf(2)));
  }

  @Test
  public void testSub1() throws Exception {
    ComplexArray x = bj.complexArray().assign(3);
    ComplexArray y = bj.complexArray().assign(2);
    assertMatrixEquals(1, x.sub(y));
  }

  @Test
  public void testSub2() throws Exception {
    ComplexArray x = bj.complexArray().assign(3);
    ComplexArray y = bj.complexArray().assign(2);
    assertMatrixEquals(5, x.sub(Complex.valueOf(1), y, Complex.valueOf(-1)));
  }

  @Test
  public void testSub3() throws Exception {
    // ComplexMatrix x = newComplexVector(1, 2, 3);
    // ComplexMatrix y = bj.complexMatrix(3, 3).assign(3);
    // ComplexMatrix z = y.sub(x, Axis.ROW);
    // MatrixAssert.assertValuesEquals(z.getRowView(0), newComplexVector(2, 1, 0));
    // MatrixAssert.assertValuesEquals(z.getRowView(1), newComplexVector(2, 1, 0));
    // MatrixAssert.assertValuesEquals(z.getRowView(2), newComplexVector(2, 1, 0));
    //
    // z = y.sub(x, Axis.COLUMN);
    // MatrixAssert.assertValuesEquals(z.getColumnView(0), newComplexVector(2, 1, 0));
    // MatrixAssert.assertValuesEquals(z.getColumnView(1), newComplexVector(2, 1, 0));
    // MatrixAssert.assertValuesEquals(z.getColumnView(2), newComplexVector(2, 1, 0));
  }

  @Test
  public void testSub4() throws Exception {
    // ComplexMatrix x = newComplexVector(1, 2, 3);
    // ComplexMatrix y = bj.complexMatrix(3, 3).assign(3);
    // ComplexMatrix z = y.sub(1, x, -1, Axis.ROW);
    // MatrixAssert.assertValuesEquals(z.getRowView(0), newComplexVector(4, 5, 6));
    // MatrixAssert.assertValuesEquals(z.getRowView(1), newComplexVector(4, 5, 6));
    // MatrixAssert.assertValuesEquals(z.getRowView(2), newComplexVector(4, 5, 6));
    //
    // z = y.sub(1, x, -1, Axis.COLUMN);
    // MatrixAssert.assertValuesEquals(z.getColumnView(0), newComplexVector(4, 5, 6));
    // MatrixAssert.assertValuesEquals(z.getColumnView(1), newComplexVector(4, 5, 6));
    // MatrixAssert.assertValuesEquals(z.getColumnView(2), newComplexVector(4, 5, 6));
  }

  @Test
  public void testRsub() throws Exception {
    ComplexArray x = bj.complexArray().assign(2);
    ComplexArray y = x.rsub(Complex.valueOf(3));
    assertMatrixEquals(1, y);
  }

  @Test
  public void testRsub1() throws Exception {
    // ComplexMatrix x = newComplexVector(1, 2, 3);
    // ComplexMatrix y = bj.complexMatrix(3, 3).assign(3);
    // ComplexMatrix z = y.rsub(1, x, -1, Axis.ROW);
    // MatrixAssert.assertValuesEquals(z.getRowView(0), newComplexVector(-4, -5, -6));
    // MatrixAssert.assertValuesEquals(z.getRowView(1), newComplexVector(-4, -5, -6));
    // MatrixAssert.assertValuesEquals(z.getRowView(2), newComplexVector(-4, -5, -6));
    //
    // z = y.rsub(1, x, -1, Axis.COLUMN);
    // MatrixAssert.assertValuesEquals(z.getColumnView(0), newComplexVector(-4, -5, -6));
    // MatrixAssert.assertValuesEquals(z.getColumnView(1), newComplexVector(-4, -5, -6));
    // MatrixAssert.assertValuesEquals(z.getColumnView(2), newComplexVector(-4, -5, -6));
  }

  @Test
  public void testDiv() throws Exception {
    ComplexArray x = bj.complexArray().assign(4);
    ComplexArray y = x.div(Complex.valueOf(2));
    assertMatrixEquals(2, y);
  }

  @Test
  public void testDiv1() throws Exception {
    ComplexArray x = bj.complexArray().assign(4);
    ComplexArray y = bj.complexArray().assign(2);
    ComplexArray z = x.div(y);
    assertMatrixEquals(2, z);
  }

  @Test
  public void testDiv2() throws Exception {
    // ComplexMatrix x = newComplexVector(1, 2, 3);
    // ComplexMatrix y = bj.complexMatrix(3, 3).assign(6);
    // ComplexMatrix z = y.div(x, Axis.ROW);
    //
    // MatrixAssert.assertValuesEquals(z.getRowView(0), newComplexVector(6, 3, 2));
    // MatrixAssert.assertValuesEquals(z.getRowView(1), newComplexVector(6, 3, 2));
    // MatrixAssert.assertValuesEquals(z.getRowView(2), newComplexVector(6, 3, 2));
    //
    // z = y.div(x, Axis.COLUMN);
    // MatrixAssert.assertValuesEquals(z.getColumnView(0), newComplexVector(6, 3, 2));
    // MatrixAssert.assertValuesEquals(z.getColumnView(1), newComplexVector(6, 3, 2));
    // MatrixAssert.assertValuesEquals(z.getColumnView(2), newComplexVector(6, 3, 2));
  }

  @Test
  public void testDiv3() throws Exception {
    // ComplexMatrix x = newComplexVector(1, 2, 3);
    // ComplexMatrix y = bj.complexMatrix(3, 3).assign(6);
    // ComplexMatrix z = y.div(2, x, 1, Axis.ROW);
    //
    // MatrixAssert.assertValuesEquals(z.getRowView(0), newComplexVector(12, 6, 4));
    // MatrixAssert.assertValuesEquals(z.getRowView(1), newComplexVector(12, 6, 4));
    // MatrixAssert.assertValuesEquals(z.getRowView(2), newComplexVector(12, 6, 4));
    //
    // z = y.div(2, x, 1, Axis.COLUMN);
    // MatrixAssert.assertValuesEquals(z.getColumnView(0), newComplexVector(12, 6, 4));
    // MatrixAssert.assertValuesEquals(z.getColumnView(1), newComplexVector(12, 6, 4));
    // MatrixAssert.assertValuesEquals(z.getColumnView(2), newComplexVector(12, 6, 4));
  }

  @Test
  public void testRdiv() throws Exception {
    ComplexArray x = bj.complexArray().assign(6);
    ComplexArray y = x.rdiv(Complex.valueOf(12));
    assertMatrixEquals(2, y);
  }

  @Test
  public void testRdiv1() throws Exception {
    // ComplexMatrix x = newComplexVector(12, 12, 12);
    // ComplexMatrix y = bj.complexMatrix(3, 3).assign(6);
    // ComplexMatrix z = y.rdiv(x, Axis.ROW);
    //
    // assertMatrixEquals(z.getRowView(0), 2);
    // assertMatrixEquals(z.getRowView(1), 2);
    // assertMatrixEquals(z.getRowView(2), 2);
    //
    // z = y.rdiv(x, Axis.COLUMN);
    // assertMatrixEquals(z.getColumnView(0), 2);
    // assertMatrixEquals(z.getColumnView(1), 2);
    // assertMatrixEquals(z.getColumnView(2), 2);
  }

  @Test
  public void testRdiv2() throws Exception {
    // ComplexMatrix x = newComplexVector(6, 6, 6);
    // ComplexMatrix y = bj.complexMatrix(3, 3).assign(6);
    // ComplexMatrix z = y.rdiv(1, x, 2, Axis.ROW);
    //
    // assertMatrixEquals(z.getRowView(0), 2);
    // assertMatrixEquals(z.getRowView(1), 2);
    // assertMatrixEquals(z.getRowView(2), 2);
    //
    // z = y.rdiv(1, x, 2, Axis.COLUMN);
    // assertMatrixEquals(z.getColumnView(0), 2);
    // assertMatrixEquals(z.getColumnView(1), 2);
    // assertMatrixEquals(z.getColumnView(2), 2);
  }

  @Test
  public void testNegate() throws Exception {
    ComplexArray x = bj.complexArray().assign(3).negate();
    assertMatrixEquals(x, Complex.valueOf(3).negate());
  }

  @Test
  public void testSlice1() throws Exception {
    ComplexArray x = bj.complexArray(new double[]{1, 2, 3, 1, 2, 3}).reshape(3, 2);
    ComplexArray slice = x.get(bj.range(3));
    assertValuesEquals(bj.complexArray(new double[]{1, 2, 3}), slice);
  }

  @Test
  public void testSlice3() throws Exception {
    ComplexArray x = bj.complexArray(new double[]{1, 2, 3, 1, 2, 3, 1, 2, 3}).reshape(3, 3);
    ComplexArray s = x.get(bj.range(2), bj.range(2));
    assertEquals(2, s.rows());
    assertEquals(2, s.columns());
    assertValuesEquals(bj.complexArray(), s.getRow(0));
    assertValuesEquals(bj.complexArray(), s.getRow(1));
  }

  @Test
  public void testSlice4() throws Exception {
    ComplexArray x = bj.complexArray(new double[]{1, 2, 3, 1, 2, 3, 1, 2, 3}).reshape(3, 3);
    ComplexArray s = x.slice(asList(0, 2, 5, 7));
    assertValuesEquals(bj.complexArray(new double[]{1, 3, 3, 2}), s);
  }

  @Test
  public void testSlice6() throws Exception {
    ComplexArray x = bj.complexArray(new double[]{1, 2, 3, 1, 2, 3, 1, 2, 3}).reshape(3, 3);
    ComplexArray s = x.slice(asList(0, 1), asList(0, 1));
    assertValuesEquals(bj.complexArray(), s.getRow(0));
    assertValuesEquals(bj.complexArray(), s.getRow(1));
  }

  @Test
  public void testSlice7() throws Exception {
    ComplexArray x = bj.complexArray(new double[]{1, 2, 3, 1, 2, 3, 1, 2, 3}).reshape(3, 3);
    BitArray bits = bj.array(
        new boolean[]{true, true, true, false, false, false, false, false, false})
        .reshape(3, 3);
    ComplexArray s = x.slice(bits);
    assertValuesEquals(bj.complexArray(new double[]{1, 2, 3}), s);
  }

  @Test
  public void testSwap() throws Exception {
    ComplexArray x = bj.complexArray(new double[]{1, 2, 3});
    x.swap(0, 2);
    assertValuesEquals(bj.complexArray(new double[]{3, 2, 1}), x);
  }

  @Test
  public void testSetRow() throws Exception {
    ComplexArray x = bj.complexArray();
    x.setRow(0, bj.complexArray(new double[]{1, 2, 3}));
    assertValuesEquals(bj.complexArray(new double[]{1, 2, 3}), x.getRow(0));
  }

  @Test
  public void testSetColumn() throws Exception {
    ComplexArray x = bj.complexArray();
    x.setColumn(0, bj.complexArray(new double[]{1, 2, 3}));
    assertValuesEquals(bj.complexArray(new double[]{1, 2, 3}), x.getColumn(0));
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
    ComplexArray x = bj.complexArray(new double[]{1, 2, 3, 4, 5, 6});
    int i = 0;
    for (Complex v : x) {
      assertEquals(x.get(i++), v);
    }
  }

  @Test
  public void testStream() throws Exception {
    ComplexArray m = bj.complexArray().assign(3);
    Complex sum = m.stream().reduce(Complex.ZERO, Complex::plus);
    assertEquals(Complex.valueOf(3 * 3 * 3), sum);
  }
}

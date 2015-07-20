package org.briljantframework.matrix;

import org.briljantframework.complex.Complex;
import org.briljantframework.matrix.api.ArrayFactory;
import org.briljantframework.matrix.netlib.NetlibArrayBackend;
import org.junit.Test;

import java.util.LongSummaryStatistics;

import static java.util.Arrays.asList;
import static org.briljantframework.matrix.MatrixAssert.assertMatrixEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class AbstractLongArrayTest {

  private final ArrayFactory bj = new NetlibArrayBackend().getArrayFactory();

  @Test
  public void testAssign() throws Exception {
    LongArray m = bj.longArray(3, 3);
    m.assign(3);
    assertMatrixEquals(m, 3);
  }

  @Test
  public void testAssign1() throws Exception {
    LongArray m = bj.longArray(3, 3);
    m.assign(() -> 3);
    assertMatrixEquals(m, 3);
  }

  @Test
  public void testAssign2() throws Exception {
    LongArray m = bj.longArray(3, 3);
    m.assign(3).update(x -> x * 2);
    assertMatrixEquals(m, 6);
  }

  @Test
  public void testAssign3() throws Exception {
    DoubleArray d = bj.doubleArray(3, 3).assign(3);
    LongArray i = bj.longArray(3, 3).assign(d, x -> (int) x);
    assertMatrixEquals(i, 3);
  }

  @Test
  public void testAssign4() throws Exception {
    ComplexArray c = bj.complexArray().assign(Complex.valueOf(3));
    LongArray i = bj.longArray(3, 3).assign(c, Complex::intValue);
    assertMatrixEquals(i, 3);
  }

  @Test
  public void testAssign5() throws Exception {
    LongArray l = bj.longArray(3, 3).assign(3L);
    LongArray i = bj.longArray(3, 3).assign(l, x -> (int) x);
    assertMatrixEquals(i, 3);
  }

  @Test
  public void testAssign6() throws Exception {
    LongArray i = bj.longArray(3, 3).assign(bj.longArray(3, 3).assign(3));
    assertMatrixEquals(i, 3);
  }

  @Test
  public void testAssign7() throws Exception {
    LongArray i = bj.longArray(3, 3).assign(bj.longArray(3, 3).assign(3), x -> x * 2);
    assertMatrixEquals(i, 6);
  }

  @Test
  public void testAssign8() throws Exception {
    LongArray x = bj.longArray(3, 3).assign(2);
    LongArray d = bj.longArray(3, 3).assign(5);
    x.assign(d, Long::sum);
    assertMatrixEquals(x, 7);
  }

  @Test
  public void testMap() throws Exception {
    LongArray i = bj.longArray(3, 3).assign(3);
    LongArray m = i.map(Long::bitCount);
    assertMatrixEquals(m, 2);
  }

  @Test
  public void testMapToInt() throws Exception {
    LongArray i = bj.longArray(3, 3).assign(Integer.MAX_VALUE + 10L);
    IntArray l = i.mapToInt(x -> (int) (x - Integer.MAX_VALUE));
    MatrixAssert.assertMatrixEquals(10, l);
  }

  @Test
  public void testMapToDouble() throws Exception {
    DoubleArray i = bj.longArray(3, 3).assign(3).mapToDouble(Math::sqrt);
    assertMatrixEquals(Math.sqrt(3), i, 0.0001);
  }

  @Test
  public void testMapToComplex() throws Exception {
    ComplexArray i = bj.longArray(3, 3).assign(-3).mapToComplex(Complex::sqrt);
    assertMatrixEquals(i, Complex.sqrt(-3));
  }

  @Test
  public void testFilter() throws Exception {
    LongArray i = bj.array(new long[]{0L, 1, 2, 3, 4, 5, 6}).filter(x -> x > 3);
    MatrixAssert.assertValuesEquals(i, bj.array(new long[]{4L, 5, 6}));
  }

  @Test
  public void testSatisfies() throws Exception {
    BitArray i = bj.array(new long[]{0L, 1, 2, 3, 4, 5}).satisfies(x -> x >= 3);
    MatrixAssert
        .assertValuesEquals(bj.array(
            new boolean[]{false, false, false, true, true, true}), i);
  }

  @Test
  public void testSatisfies1() throws Exception {
    LongArray x = bj.longArray(3, 3);
    LongArray y = bj.longArray(3, 3).assign(3);
    BitArray z = x.satisfies(y, (a, b) -> a < b);
    assertMatrixEquals(z, true);
  }

  @Test
  public void testReduce() throws Exception {
    LongArray x = bj.longArray(3, 3).assign(3);
    long sum = x.reduce(0, Long::sum);
    assertEquals(3 * 9, sum);
  }

  @Test
  public void testReduce1() throws Exception {
    LongArray x = bj.longArray(3, 3).assign(3);
    long squaredSum = x.reduce(0, Long::sum, i -> i * 2);
    assertEquals(3 * 2 * 9, squaredSum);
  }

  @Test
  public void testReduceColumns() throws Exception {
    LongArray x = bj.longArray(3, 3).assign(3).reduceColumns(y -> y.reduce(0, Long::sum));
    assertEquals(4, x.columns());
    assertEquals(1, x.rows());
    assertMatrixEquals(x, 3 * 3);
  }

  @Test
  public void testReduceRows() throws Exception {
    LongArray x = bj.longArray(3, 3).assign(3).reduceRows(y -> y.reduce(0, Long::sum));
    assertEquals(4, x.rows());
    assertEquals(1, x.columns());
    assertMatrixEquals(x, 3 * 3);
  }

  @Test
  public void testReshape() throws Exception {
    LongArray x = bj.array(new long[]{1L, 2, 3, 4, 5, 6}).reshape(2, 3);
    assertEquals(2, x.rows());
    assertEquals(3, x.columns());
  }

  @Test
  public void testGet() throws Exception {
    LongArray x = bj.array(new long[]{0L, 1, 2, 3, 4, 5}).reshape(3, 2);
    assertEquals(0, x.get(0));
    assertEquals(5, x.get(5));
  }

  @Test
  public void testGet1() throws Exception {
    LongArray x = bj.array(new long[]{0L, 1, 2, 3, 4, 5}).reshape(3, 2);
    assertEquals(0, x.get(0, 0));
    assertEquals(3, x.get(0, 1));
    assertEquals(4, x.get(1, 1));
  }

  @Test
  public void testSet() throws Exception {
    LongArray x = bj.longArray(3, 3);
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
    LongArray x = bj.array(new long[]{0L, 1, 2, 3});
    assertEquals(0, x.get(0));
    assertEquals(1, x.get(1));
    assertEquals(2, x.get(2));
    assertEquals(3, x.get(3));
  }

  @Test
  public void testGetRowView() throws Exception {
    LongArray x = bj.array(new long[]{1L, 2, 3, 1, 2, 3, 1, 2, 3}).reshape(3, 3);
    assertMatrixEquals(x.getRow(0), 1);
    assertMatrixEquals(x.getRow(1), 2);
    assertMatrixEquals(x.getRow(2), 3);
  }

  @Test
  public void testGetColumnView() throws Exception {
    LongArray x = bj.array(new long[]{1L, 1, 1, 2, 2, 2, 3, 3, 3}).reshape(3, 3);
    assertMatrixEquals(x.getColumn(0), 1);
    assertMatrixEquals(x.getColumn(1), 2);
    assertMatrixEquals(x.getColumn(2), 3);
  }

  @Test
  public void testGetDiagonalView() throws Exception {

  }

  @Test
  public void testGetView() throws Exception {
    LongArray x = bj.array(new long[]{1L, 1, 1, 1, 2, 2}).reshape(2, 3);
    assertMatrixEquals(x.getView(0, 0, 2, 2), 1);
  }

  @Test
  public void testTranspose() throws Exception {
    LongArray x = bj.array(new long[]{1L, 2, 3, 1, 2, 3}).reshape(2, 3).transpose();
    assertEquals(3, x.rows());
    assertEquals(2, x.columns());
    assertEquals(1, x.get(0, 0));
    assertEquals(3, x.get(2, 1));
  }

  @Test
  public void testCopy() throws Exception {
    LongArray x = bj.array(new long[]{1L, 1, 1, 1});
    LongArray y = x.copy();
    x.set(0, 1000);
    assertEquals(1, y.get(0));
  }

  @Test
  public void testNewEmptyMatrix() throws Exception {
    assertNotNull(bj.longArray(3, 3).newEmptyArray(2, 2));
  }

  @Test
  public void testNewEmptyVector() throws Exception {
    LongArray x = bj.longArray(3, 3).newEmptyArray(2);
    assertNotNull(x);
    assertEquals(2, x.rows());
    assertEquals(1, x.columns());
  }

  @Test
  public void testMmul() throws Exception {
    LongArray x = bj.array(new long[]{1L, 2, 3, 4, 5, 6}).reshape(3, 2);
    LongArray y = bj.array(new long[]{1L, 2, 3, 4, 5, 6}).reshape(2, 3);

    LongArray z = y.mmul(x);
    LongArray za = bj.array(new long[]{22L, 28, 49, 64}).reshape(2, 2);
    assertMatrixEquals(za, z);

    z = x.mmul(y);
    za = bj.array(new long[]{9L, 12, 15, 19, 26, 33, 29, 40, 51}).reshape(3, 3);
    assertMatrixEquals(za, z);
  }

  @Test
  public void testMmul1() throws Exception {
    LongArray x = bj.array(new long[]{1L, 2, 3, 4, 5, 6}).reshape(3, 2);
    LongArray y = bj.array(new long[]{1L, 2, 3, 4, 5, 6}).reshape(2, 3);

    LongArray z = y.mmul(2, x);
    LongArray za = bj.array(new long[]{44L, 56, 98, 128}).reshape(2, 2);
    assertMatrixEquals(za, z);

    z = x.mmul(4, y);
    za = bj.array(new long[]{36L, 48, 60, 76, 104, 132, 116, 160, 204}).reshape(3, 3);
    assertMatrixEquals(za, z);
  }

  @Test
  public void testMmul2() throws Exception {
    LongArray x = bj.array(new long[]{1L, 2, 3, 4, 5, 6}).reshape(3, 2);
    LongArray y = bj.array(new long[]{1L, 2, 3, 4, 5, 6}).reshape(3, 2);

    LongArray z = y.mmul(Op.TRANSPOSE, x, Op.KEEP);
    LongArray za = bj.array(new long[]{14L, 32, 32, 77}).reshape(2, 2);
    assertMatrixEquals(za, z);

    z = x.mmul(Op.KEEP, y, Op.TRANSPOSE);
    za = bj.array(new long[]{17, 22, 27, 22L, 29, 36, 27, 36, 45}).reshape(3, 3);
    assertMatrixEquals(za, z);
  }

  @Test
  public void testMmul3() throws Exception {
    LongArray x = bj.array(new long[]{1L, 2, 3, 4, 5, 6}).reshape(3, 2);
    LongArray y = bj.array(new long[]{1L, 2, 3, 4, 5, 6}).reshape(3, 2);
    LongArray z = y.mmul(2, Op.TRANSPOSE, x, Op.KEEP);
    LongArray za = bj.array(new long[]{28L, 64, 64, 154}).reshape(2, 2);
    assertMatrixEquals(za, z);

    z = x.mmul(2, Op.KEEP, y, Op.TRANSPOSE);
    za = bj.array(new long[]{34L, 44, 54, 44, 58, 72, 54, 72, 90}).reshape(3, 3);
    assertMatrixEquals(za, z);
  }

  @Test
  public void testMul() throws Exception {
    LongArray x = bj.longArray(3, 3).assign(3);
    LongArray z = x.mul(2);
    assertMatrixEquals(z, 6);
  }

  @Test
  public void testMul1() throws Exception {
    LongArray x = bj.longArray(3, 3).assign(3);
    LongArray y = bj.longArray(3, 3).assign(2);
    LongArray z = x.mul(y);
    assertMatrixEquals(z, 6);
  }

  @Test
  public void testMul2() throws Exception {
    LongArray x = bj.longArray(3, 3).assign(3);
    LongArray y = bj.longArray(3, 3).assign(2);
    LongArray z = x.mul(-1, y, -1);
    assertMatrixEquals(z, 6);
  }

  @Test
  public void testAdd() throws Exception {
    LongArray x = bj.longArray(3, 3).assign(2);
    assertMatrixEquals(x.add(3), 5);
  }

  @Test
  public void testAdd1() throws Exception {
    LongArray x = bj.longArray(3, 3).assign(2);
    LongArray y = bj.longArray(3, 3).assign(3);
    assertMatrixEquals(x.add(y), 5);
  }

  @Test
  public void testAdd2() throws Exception {
    LongArray x = bj.longArray(3, 3).assign(2);
    LongArray y = bj.longArray(3, 3).assign(3);
    assertMatrixEquals(x.add(1, y, -1), -1);
  }

  @Test
  public void testSub() throws Exception {
    LongArray x = bj.longArray(3, 3).assign(3);
    assertMatrixEquals(x.sub(2), 1);
  }

  @Test
  public void testSub1() throws Exception {
    LongArray x = bj.longArray(3, 3).assign(3);
    LongArray y = bj.longArray(3, 3).assign(2);
    assertMatrixEquals(x.sub(y), 1);
  }

  @Test
  public void testSub2() throws Exception {
    LongArray x = bj.longArray(3, 3).assign(3);
    LongArray y = bj.longArray(3, 3).assign(2);
    assertMatrixEquals(x.sub(1, y, -1), 5);
  }

  @Test
  public void testRsub() throws Exception {
    LongArray x = bj.longArray(3, 3).assign(2);
    LongArray y = x.rsub(3);
    assertMatrixEquals(y, 1);
  }

  @Test
  public void testDiv() throws Exception {
    LongArray x = bj.longArray(3, 3).assign(4);
    LongArray y = x.div(2);
    assertMatrixEquals(y, 2);
  }

  @Test
  public void testDiv1() throws Exception {
    LongArray x = bj.longArray(3, 3).assign(4);
    LongArray y = bj.longArray(3, 3).assign(2);
    LongArray z = x.div(y);
    assertMatrixEquals(z, 2);
  }

  @Test
  public void testRdiv() throws Exception {
    LongArray x = bj.longArray(3, 3).assign(6);
    LongArray y = x.rdiv(12);
    assertMatrixEquals(y, 2);
  }

  @Test
  public void testNegate() throws Exception {
    LongArray x = bj.longArray(3, 3).assign(3).negate();
    assertMatrixEquals(x, -3);
  }

  @Test
  public void testSlice1() throws Exception {
    LongArray x = bj.array(new long[]{1L, 2, 3, 1, 2, 3}).reshape(3, 2);
    LongArray slice = x.get(bj.range(3));
    MatrixAssert.assertValuesEquals(slice, bj.array(new long[]{1L, 2, 3}));
  }

  @Test
  public void testSlice3() throws Exception {
    LongArray x = bj.array(new long[]{1L, 2, 3, 1, 2, 3, 1, 2, 3}).reshape(3, 3);
    LongArray s = x.get(bj.range(0, 3), bj.range(0, 3));
    assertEquals(2, s.rows());
    assertEquals(2, s.columns());
    MatrixAssert.assertValuesEquals(s.getRow(0), bj.array(new long[]{1L, 1}));
    MatrixAssert.assertValuesEquals(s.getRow(1), bj.array(new long[]{2L, 2}));
  }

  @Test
  public void testSlice4() throws Exception {
    LongArray x = bj.array(new long[]{1L, 2, 3, 1, 2, 3, 1, 2, 3}).reshape(3, 3);
    LongArray s = x.slice(asList(0, 2, 5, 7));
    MatrixAssert.assertValuesEquals(s, bj.array(new long[]{1L, 3, 3, 2}));
  }

  @Test
  public void testSlice6() throws Exception {
    LongArray x = bj.array(new long[]{1, 2, 3, 1, 2, 3, 1, 2, 3L}).reshape(3, 3);
    LongArray s = x.slice(asList(0, 1), asList(0, 1));
    MatrixAssert.assertValuesEquals(s.getRow(0), bj.array(new long[]{1L, 1}));
    MatrixAssert.assertValuesEquals(s.getRow(1), bj.array(new long[]{2L, 2}));
  }

  @Test
  public void testSlice7() throws Exception {
    LongArray x = bj.array(new long[]{1L, 2, 3, 1L, 2, 3, 1, 2, 3}).reshape(3, 3);
    BitArray bits =
        bj.array(new boolean[]{
            true, true, true, false, false, false, false, false, false})
            .reshape(3, 3);
    LongArray s = x.slice(bits);
    MatrixAssert.assertValuesEquals(s, bj.array(new long[]{1, 2, 3L}));
  }

  @Test
  public void testSwap() throws Exception {
    LongArray x = bj.array(new long[]{1L, 2, 3});
    x.swap(0, 2);
    MatrixAssert.assertValuesEquals(x, bj.array(new long[]{3L, 2, 1}));
  }

  @Test
  public void testSetRow() throws Exception {
    LongArray x = bj.longArray(3, 3);
    x.setRow(0, bj.array(new long[]{1L, 2, 3}));
    MatrixAssert.assertValuesEquals(x.getRow(0), bj.array(new long[]{1L, 2, 3}));
  }

  @Test
  public void testSetColumn() throws Exception {
    LongArray x = bj.longArray(3, 3);
    x.setColumn(0, bj.array(new long[]{1L, 2, 3}));
    MatrixAssert.assertValuesEquals(x.getColumn(0), bj.array(new long[]{1L, 2, 3}));
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
    LongArray x = bj.array(new long[]{1L, 2, 3, 4, 5, 6});
    int i = 0;
    for (long v : x) {
      assertEquals(x.get(i++), v);
    }
  }

  @Test
  public void testStream() throws Exception {
    LongArray m = bj.longArray(3, 3).assign(3);
    LongSummaryStatistics s = m.stream().summaryStatistics();
    assertEquals(3 * 3 * 3, s.getSum());
  }
}

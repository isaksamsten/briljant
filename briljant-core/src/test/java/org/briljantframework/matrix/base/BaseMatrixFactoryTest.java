package org.briljantframework.matrix.base;

import org.briljantframework.matrix.DoubleMatrix;
import org.briljantframework.matrix.IntMatrix;
import org.briljantframework.matrix.T;
import org.briljantframework.matrix.api.MatrixFactory;
import org.briljantframework.matrix.api.MatrixRoutines;
import org.briljantframework.matrix.netlib.NetlibMatrixBackend;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class BaseMatrixFactoryTest {

  private final NetlibMatrixBackend b = new NetlibMatrixBackend();
  private final MatrixFactory bj = b.getMatrixFactory();
  private final MatrixRoutines bjr = b.getMatrixRoutines();

  @Test
  public void testCreateIntMatrixFrom2DArray() throws Exception {
    IntMatrix x = bj.matrix(new int[][]{
        new int[]{1, 2, 3},
        new int[]{1, 2, 3}
    });
    assertEquals(1, x.get(0, 0));
  }

  @Test
  public void testCreateIntMatrixFromArray() throws Exception {
    IntMatrix x = bj.matrix(new int[]{1, 2, 3, 4}).reshape(2, 2);
    assertEquals(1, x.get(0, 0));
  }

  @Test
  public void testMatrix2() throws Exception {
    DoubleMatrix a = bj.matrix(new double[]{1, 2, 3, 1, 2, 3, 1, 2, 3}).reshape(3, 3);
    DoubleMatrix b = bj.matrix(new double[]{1, 2, 3, 1, 2, 3}).reshape(3, 2);
    DoubleMatrix x = bj.matrix(new double[]{1, 2, 3}).reshape(3, 1);
    DoubleMatrix y = bj.doubleVector(3).reshape(3, 1).assign(2).getView(0, 0, 3, 1);
    System.out.println(a);
    System.out.println(b);

    DoubleMatrix c = bj.doubleMatrix(3, 2).assign(2).getView(0, 0, 3, 2);
    bjr.gemm(T.YES, T.NO, 1, a, b, 2, c);
    System.out.println(c);

    System.out.println(x);
    System.out.println(y);

    bjr.gemv(T.NO, 1, a, x, 4, y);
    System.out.println(y);

    DoubleMatrix z = bj.matrix(new double[]{1, 2, 3});
    bjr.axpy(1, x.getView(0, 0, 2, 1), z.getView(0, 0, 2, 1));
    System.out.println(z);

    assertSame(z.getStorage(), z.getStorage());
  }

  @Test
  public void testMatrix3() throws Exception {

  }

  @Test
  public void testMatrix4() throws Exception {

  }

  @Test
  public void testMatrix5() throws Exception {

  }

  @Test
  public void testDiag() throws Exception {

  }

  @Test
  public void testSquareDiag() throws Exception {
//    System.out.println(d);
  }

  @Test
  public void testDiag2() throws Exception {

  }

  @Test
  public void testMatrix6() throws Exception {

  }

  @Test
  public void testMatrix7() throws Exception {

  }

  @Test
  public void testComplexMatrix() throws Exception {

  }

  @Test
  public void testMatrix8() throws Exception {

  }

  @Test
  public void testMatrix9() throws Exception {

  }

  @Test
  public void testIntMatrix() throws Exception {

  }

  @Test
  public void testIntVector() throws Exception {

  }

  @Test
  public void testIntVector1() throws Exception {

  }

  @Test
  public void testLongMatrix() throws Exception {

  }

  @Test
  public void testLongVector() throws Exception {

  }

  @Test
  public void testLongVector1() throws Exception {

  }

  @Test
  public void testDoubleMatrix() throws Exception {

  }

  @Test
  public void testDoubleVector() throws Exception {

  }

  @Test
  public void testDoubleVector1() throws Exception {

  }

  @Test
  public void testComplexMatrix1() throws Exception {

  }

  @Test
  public void testComplexVector() throws Exception {

  }

  @Test
  public void testComplexVector1() throws Exception {

  }

  @Test
  public void testBooleanMatrix() throws Exception {

  }

  @Test
  public void testBooleanVector() throws Exception {

  }

  @Test
  public void testBooleanVector1() throws Exception {

  }

  @Test
  public void testRange() throws Exception {

  }

  @Test
  public void testRange1() throws Exception {

  }

  @Test
  public void testRange2() throws Exception {

  }

  @Test
  public void testLinspace() throws Exception {

  }

  @Test
  public void testEye() throws Exception {

  }

  @Test
  public void testGetMatrixRoutines() throws Exception {

  }

  @Test
  public void testGetLinearAlgebraRoutines() throws Exception {

  }

  @Test
  public void testMatrix() throws Exception {

  }

  @Test
  public void testMatrix1() throws Exception {

  }

  @Test
  public void testMatrix10() throws Exception {

  }

  @Test
  public void testMatrix11() throws Exception {

  }

  @Test
  public void testMatrix12() throws Exception {

  }

  @Test
  public void testMatrix13() throws Exception {

  }

  @Test
  public void testMatrix14() throws Exception {

  }

  @Test
  public void testMatrix15() throws Exception {

  }

  @Test
  public void testMatrix16() throws Exception {

  }

  @Test
  public void testMatrix17() throws Exception {

  }

  @Test
  public void testRand() throws Exception {

  }

  @Test
  public void testRandi() throws Exception {

  }

  @Test
  public void testRandi1() throws Exception {
    IntMatrix r = bj.randi(100, -10, 10);
    System.out.println(r.reshape(10, 10));
  }
}
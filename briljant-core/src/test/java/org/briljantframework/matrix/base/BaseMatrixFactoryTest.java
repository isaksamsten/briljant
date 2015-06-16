package org.briljantframework.matrix.base;

import org.briljantframework.matrix.DoubleMatrix;
import org.briljantframework.matrix.IntMatrix;
import org.briljantframework.matrix.T;
import org.briljantframework.matrix.api.MatrixFactory;
import org.briljantframework.matrix.api.MatrixRoutines;
import org.briljantframework.matrix.netlib.NetlibMatrixBackend;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

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
    IntMatrix x = bj.matrix(new int[]{1, 2, 3, 4});
    assertEquals(4, x.size());
    assertEquals(4, x.rows());
    assertEquals(1, x.columns());
    assertArrayEquals(new int[]{1, 2, 3, 4}, x.data());
  }

  @Test
  public void testCreateIntFrom2dArray() throws Exception {
    DoubleMatrix arr = bj.doubleArray(new int[]{2, 3, 3});
    double c = 0;
    for (int i = 0; i < 2; i++) {
      for (int j = 0; j < 3; j++) {
        for (int k = 0; k < 3; k++) {
          arr.set(new int[]{i, j, k}, c++);
        }
      }
    }

//    arr.set(new int[]{0, 1, 1}, 30);
//    arr.set(new int[]{0, 1, 0}, 10);
//    arr.set(new int[]{1, 0, 0}, 22);
//    print(arr);
//    print(arr.reshape(3, 3, 2));
//    print(arr.transpose());

    DoubleMatrix d = bj.range(12).asDoubleMatrix().copy().reshape(2, 3, 2);
//    System.out.println(Arrays.toString(d.transpose().shape()));
    System.out.println(d);
    System.out.println(Arrays.toString(d.data()));
    System.out.println("=======" + d.stride(0));
//    System.out.println(Arrays.toString(d.getStride()));
//    System.out.println(d.getVectorAlong(0, 1));
    DoubleMatrix _3x2 = d.slice(0);
    testVectorAlongDimension(_3x2, 0);
    testVectorAlongDimension(_3x2, 1);

    DoubleMatrix _3x1 = _3x2.slice(0);
    testVectorAlongDimension(_3x1, 0);

    testVectorAlongDimension(d, 0);
    testVectorAlongDimension(d, 1);
    testVectorAlongDimension(d, 2);

//    System.out.println(_3x2);
//    System.out.println(_3x2.getVectorAlong(1, 0));
//
//    System.out.println(_3x2.size(0));
//    for (int i = 0; i < 2; i++) {
//      System.out.println(_3x2.getVectorAlong(0, i));
//    }

//
//    DoubleMatrix _3 = bj.doubleArray(new int[]{4});
//    _3.set(0, 1);
//    _3.set(1, 2);
//    _3.set(2, 3);
//    _3.set(3, 4);
//
//    System.out.println(_3.getVectorAlong(0, 0));
//
//    for (int i = 0; i < d.dims(); i++) {
//      System.out.printf("Size: %d => %d == %d\n", i, d.size() / d.size(i), d.size(i));
//    }
//    System.out.println("--------------");
//    int dim = 0;
//    for (int i = 0; i < d.size() / d.size(dim); i++) {
//      System.out.println(d.getVectorAlong(dim, i));
//    }

//    for (int i = 0; i < 6; i++) {
//      System.out.println(d.getVectorAlong(1, i));
//    }

//
//    print(d.transpose());
//    print(d.reshape(3, 2, 2));

//    print(arr.transpose());
//
//    for (int i = 0; i < arr.size(); i++) {
//      System.out.println(arr.get(i));
//    }
//    DoubleMatrix slice = arr.slice(new int[]{1, 0});
//
//    print(slice);
//    System.out.println();
//    DoubleMatrix s = arr.slice(0);
//    for (int i = 0; i < s.size(); i++) {
//      System.out.println(s.get(i));
//    }
//
//    for (int i = 0; i < s.rows(); i++) {
//      for (int j = 0; j < s.columns(); j++) {
//        System.out.print(s.get(i, j) + " ");
//      }
//      System.out.println();
//    }
  }

  void testVectorAlongDimension(DoubleMatrix d, int dim) {
    System.out.printf("Testing vector along %d of shape %s \n", dim, Arrays.toString(d.shape()));
    for (int i = 0; i < d.size() / d.size(dim); i++) {
      System.out.println(d.getVectorAlong(dim, i));
    }
    System.out.println("-------");
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
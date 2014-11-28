package org.briljantframework.matrix.math;

import static org.briljantframework.matrix.Matrices.parseMatrix;
import static org.briljantframework.matrix.Matrices.sum;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.stream.IntStream;

import org.briljantframework.matrix.*;
import org.junit.Before;
import org.junit.Test;


public class MatricesTest {

  @Before
  public void setUp() throws Exception {

  }

  @Test
  public void testNorm2() throws Exception {
    // assertEquals(3.7417, Matrices.norm2(DenseMatrix.of(3, 1, 1, 2, 3)), 0.01);
    // assertEquals(3.7417, Matrices.norm2(DenseMatrix.of(1, 3, 1, 2, 3)), 0.01);
  }

  @Test
  public void testSumRows() throws Exception {
    Matrix m = parseMatrix("1,1,1,1;" + "1,1,1,1;" + "1,1,1,1");
    Matrix rowSum = sum(m, Axis.ROW);
    Matrix columnSum = sum(m, Axis.COLUMN);
    System.out.println(rowSum);
    System.out.println(columnSum);

  }

  @Test
  public void testAxB() throws Exception {
    Matrix a = DenseMatrix.of(2, 3, 1, 2, 3, 1, 2, 3);
    Matrix b = DenseMatrix.of(3, 2, 2, 2, 1, 1, 3, 3);
    Matrix result = DenseMatrix.of(2, 2, 13, 13, 13, 13);
    assertArrayEquals(result.asDoubleArray(),
        Matrices.mmul(DenseMatrix::new, a, b).asDoubleArray(), 0.0001);
  }

  @Test
  public void testMatrixMultiplicationInplace() throws Exception {
    DenseMatrix a = DenseMatrix.of(2, 3, 1, 2, 3, 3, 2, 1);

    DenseMatrix b = DenseMatrix.of(3, 2, 1, 2, 3, 4, 5, 6);

    Matrix c = Matrices.n(2, 2, 10);

    // calculates c = a * b + 2c
    Matrices.mmuli(a, Transpose.NO, 1, b, Transpose.NO, 2, c.asDoubleArray());
    assertArrayEquals(new double[] {42.0, 34.0, 48.0, 40.0}, c.asDoubleArray(), 0.01);
  }

  @Test
  public void testMatrixMultiplyTranspose() throws Exception {
    Matrix x = DenseMatrix.of(3, 2, 1, 2, 3, 4, 5, 6);
    Matrix y = DenseMatrix.of(3, 2, 1, 2, 1, 2, 1, 2);


    // System.out.println(Matrices.multiply(x, y.transpose()));
    // System.out.println(Matrices.multiply(x, Transpose.NO, y, Transpose.YES));

  }

  @Test
  public void testAddition() {
    // Vector a = Vector.rows(1, 2, 3);
    // Vector b = Vector.rows(1, 2, 3);
    // Matrices.addInplace(a, 1, b, 1);
    //
    // assertArrayEquals(new double[]{2, 4, 6}, b.array(), 0.001);
  }


  @Test
  public void testMultiplyDiagonal() throws Exception {
    Diagonal eye = Diagonal.of(4, 4, 2, 2, 2, 2);

    DenseMatrix x = DenseMatrix.of(4, 3, 1, 2, 3, 1, 2, 3, 1, 2, 3, 1, 2, 3);

    DenseMatrix y = Matrices.mdmul(DenseMatrix::new, x, eye);
    assertArrayEquals(new double[] {2.0, 2.0, 2.0, 2.0, 4.0, 4.0, 4.0, 4.0, 6.0, 6.0, 6.0, 6.0},
        y.asDoubleArray(), 0.001);


    y = Matrices.dmmul(DenseMatrix::new, eye, x.transpose());
    assertArrayEquals(new double[] {2.0, 4.0, 6.0, 2.0, 4.0, 6.0, 2.0, 4.0, 6.0, 2.0, 4.0, 6.0},
        y.asDoubleArray(), 0.001);
  }

  @Test
  public void testMatrixVectorMultiply() throws Exception {
    Matrix A = DenseMatrix.of(3, 4, 1, 2, 3, 1, 1, 2, 3, 1, 1, 2, 3, 1);
    // Vector x = Vector.rows(1, 2, 3, 3);
    // Array result = A.multiply(x);
    // assertEquals(17.0, result.asDoubleStream().average().getAsDouble(), 0.001);
    //
    // Array arr = A.transpose().multiply(Vector.rows(1, 2, 3));
    // assertEquals(6, arr.get(0), 0.001);
    // assertEquals(12, arr.get(1), 0.001);
    // assertEquals(18, arr.get(2), 0.001);
    // assertEquals(6, arr.get(3), 0.001);
    //
    // result = x.transpose().multiply(A.transpose());
    // assertEquals(17.0, result.asDoubleStream().average().getAsDouble(), 0.001);
  }

  @Test
  public void testVectorScalarMultiplication() throws Exception {

    double[] l = IntStream.range(0, 100).asDoubleStream().toArray();
    // Vector x = Vector.rows(l);
    // Vector xa = x.multiply(2.0);
    //
    // for (int i = 0; i < x.rows(); i++) {
    // assertEquals(x.get(i) * 2.0, xa.get(i), 0.001);
    // }
  }

  @Test
  public void testVectorVectorMultiplication() throws Exception {
    // Vector a = Vector.rows(1, 2, 3);
    // Vector b = Vector.columns(1, 2, 3);
    //
    // Vector dot = b.multiply(a).asVector();
    // assertEquals(true, dot.isScalar());
    // assertEquals(14, dot.asScalar(), 0.001);
    // assertEquals(14, a.dot(b), 0.001);

  }

  @Test
  public void testMatrixMatrixMultiplication() throws Exception {
    DenseMatrix a = DenseMatrix.of(3, 2, 1, 1, 2, 2, 3, 3);

    DenseMatrix b = DenseMatrix.of(2, 2, 1, 2, 1, 2);

    assertEquals(DenseMatrix.of(3, 2, 3, 3, 6, 6, 9, 9),
        Matrices.mmul(DenseMatrix::new, a, b.transpose()));

    assertEquals(DenseMatrix.of(3, 2, 2, 4, 4, 8, 6, 12), a.mmul(b));
  }

  @Test
  public void testMatrixScalarMultiplication() throws Exception {
    Matrix a = DenseMatrix.of(2, 3, 1, 2, 3, 1, 2, 3);
    // Matrix ad = Matrices.multiply(a, 2.0);
    // assertEquals(Matrix.of(2, 3,
    // 2, 4, 6,
    // 2, 4, 6
    // ), ad);
  }


  @Test
  public void testVectorAddition() throws Exception {
    // Vector a = Vector.rows(1, 2, 3, 4);
    // Vector b = Vector.rows(1, 2, 3, 4);
    //
    // Vector c = a.add(b);
    // assertArrayEquals(new double[]{2, 4, 6, 8}, c.array(), 0.01);
  }

  @Test
  public void testSubtractScalar() throws Exception {
    // Vector a = Vector.columns(1, 2, 3, 4);
    // Vector b = Vector.columns(4, 3, 2, 1);
    //
    // Vector c = Vector.columns(-3, -1, 1, 3);
    //
    // assertEquals(c, Matrices.subtract(a, b));
    // Matrices.subtractInplace(a, b);
    // assertEquals(c, b);
  }

  @Test
  public void testMatrixAddition() throws Exception {

  }
}

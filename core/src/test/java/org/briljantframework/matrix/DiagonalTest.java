package org.briljantframework.matrix;

import org.junit.Test;

public class DiagonalTest {

  @Test
  public void testDiagonalMultiply() throws Exception {
    Diagonal d = Diagonal.of(2, 3, 2, 2);
    DoubleMatrix x = ArrayDoubleMatrix.of(2, 2, 1, 2, 1, 2);
    // assertArrayEquals(new double[] {2.0, 2.0, 4.0, 4.0, 0.0, 0.0}, x.mmul(d).asDoubleArray(),
    // 0.0001);
    // assertEquals()
  }

  @Test
  public void testApply() throws Exception {
    Diagonal a = Diagonal.of(3, 3, 1, 1, 1);
    System.out.println(a.map(d -> d * 2));

    // a.apply(x -> x * 2);
    System.out.println(a);

    // a.asDoubleArray();
    System.out.println(a.map(d -> d * 2));

    // a.apply(x -> x * 2);
    System.out.println(a);


  }

  @Test
  public void testReshape() throws Exception {
    Diagonal x = Diagonal.of(2, 3, 1, 2);

    System.out.println(x.transpose());
    // System.out.println(x.reshape(3, 2));

    // x.reshapeInplace(3, 2);
    // System.out.println(x.);
    System.out.println(x);
    // System.out.println(Arrays.toString(x.asDoubleArray()));

  }

  @Test
  public void testArray() throws Exception {
    // Diagonal diagonal = new Diagonal(2, 3, new double[]{1, 2});
    //
    // Matrix m = Matrix.fromColumnOrder(2, 3, diagonal.array());
    // System.out.println(m);
    //
    // System.out.println(diagonal);
    // System.out.println();
    // diagonal.asTensor().put(1, 2, 10);
    // System.out.println(diagonal.get(1, 2));
    // System.out.println(diagonal.asTensor().get(1, 2));
    // diagonal.put(1, 2, 10);


    // System.out.println(m.multiplyByDiagonal(diagonal.transpose()).asTensor());

  }

  @Test
  public void testMultiplication() throws Exception {
    ArrayDoubleMatrix u = ArrayDoubleMatrix.of(2, 2, -0.25803, -0.96614, -0.96614, 0.25803);
    Diagonal diagonal = Diagonal.of(2, 3, 13.6291, 3.0412);
    // System.out.println(Matrices.multiply(diagonal, Transpose.YES, u, Transpose.NO));

    System.out.println(diagonal.transpose().mmul(u));

    // System.out.println(Matrices.mdmul(u, diagonal.transpose()));

  }
}

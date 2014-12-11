package org.briljantframework.linalg;

import static org.junit.Assert.assertEquals;

import org.briljantframework.linalg.decomposition.LuDecomposition;
import org.briljantframework.linalg.decomposition.SingularValueDecomposition;
import org.briljantframework.matrix.ArrayMatrix;
import org.briljantframework.matrix.Diagonal;
import org.briljantframework.matrix.Matrices;
import org.briljantframework.matrix.Matrix;
import org.junit.Test;

public class LinearAlgebraTest {

  @Test
  public void testLLS() throws Exception {
    ArrayMatrix A =
        ArrayMatrix.of(6, 5, -0.09, 0.14, -0.46, 0.68, 1.29, -1.56, 0.20, 0.29, 1.09, 0.51, -1.48,
            -0.43, 0.89, -0.71, -0.96, -1.09, 0.84, 0.77, 2.11, -1.27, 0.08, 0.55, -1.13, 0.14,
            1.74, -1.59, -0.72, 1.06, 1.24, 0.34);
    Matrix b = ArrayMatrix.withRows(6).withValues(7.4, 4.2, -8.3, 1.8, 8.6, 2.1);
    Matrix res = LinearAlgebra.leastLinearSquares(A, b);

    assertEquals(0.6344, res.get(0, 0), 0.01);

    // System.out.println(res);
    // Matrix a = pinv(A.transpose().mmul(A)).mmul(A.transpose()).mmul(b);
    // System.out.println(a);
    // assertArrayEquals(res.asDoubleArray(), a.asDoubleArray(), 0.01);
  }

  @Test
  public void testPinvNonSymetric() throws Exception {
    Matrix x = Matrices.randn(20, 20);
    Diagonal d = Matrices.eye(20, 10);

    Diagonal d2 = Matrices.eye(10, 20);

    // Matrix mdmul = Matrices.mdmul(x, d);
    // System.out.println(mdmul);
    //
    // Matrix dmmul = Matrices.dmmul(d2, x);
    // System.out.println(dmmul);

    Matrix a = Matrices.randn(10, 20);
    System.out.println(LinearAlgebra.pinv(a));

    Matrix m = Matrices.parseMatrix("1,2,3,4;1,2,3,4");
    System.out.println(LinearAlgebra.pinv(m));
    // Matrix x = Matrices.parseMatrix("1,2,3,4;1,2,3,4");
    // Diagonal y = Diagonal.of(4, 2, 1, 1);
    //
    //
    // System.out.println(Matrices.mdmul(DenseMatrix::new, x, y));
    // System.out.println(Matrices.dmmul(DenseMatrix::new, Diagonal.of(2, 4, 1, 1), x.transpose()));

  }

  @Test
  public void testRank() throws Exception {
    ArrayMatrix matrix = ArrayMatrix.of(4, 4, 0, 2, 0, 1, 2, 2, 3, 2, 4, -3, 0, 1., 6, 1, -6, -5);
    double rank = LinearAlgebra.rank(matrix);
    assertEquals(4.0, rank, 0.0001);
  }

  @Test
  public void testLu() throws Exception {
    ArrayMatrix matrix = ArrayMatrix.of(4, 4, 0, 2, 0, 1, 2, 2, 3, 2, 4, -3, 0, 1., 6, 1, -6, -5);
    LuDecomposition lu = LinearAlgebra.lu(matrix);

    // assertArrayEquals(new double[] {-0.02564102564102574, 0.1794871794871794,
    // -0.5299145299145301,
    // 0.6410256410256412, 0.12820512820512822, 0.10256410256410262, 0.31623931623931634,
    // -0.20512820512820523, 0.0897435897435897, -0.12820512820512825, -0.14529914529914537,
    // 0.2564102564102565, 0.06410256410256412, 0.051282051282051294, -0.008547008547008544,
    // -0.10256410256410259}, lu.inverse().asDoubleArray(), 0.001);

    assertEquals(true, lu.isNonSingular());
  }

  @Test
  public void testPCA() throws Exception {
    ArrayMatrix matrix = ArrayMatrix.of(4, 4, 0, 2, 0, 1, 2, 2, 3, 2, 4, -3, 0, 1., 6, 1, -6, -5);
    Matrix u = LinearAlgebra.pca(matrix).getU();
    System.out.println(u);
  }

  @Test
  public void testInverse() throws Exception {
    ArrayMatrix matrix = ArrayMatrix.of(4, 4, 0, 2, 0, 1, 2, 2, 3, 2, 4, -3, 0, 1., 6, 1, -6, -5);
    Matrix inverse = LinearAlgebra.inv(matrix);
    assertEquals(-0.02564102564102574, inverse.get(0, 0), 0.01);

    System.out.println(matrix);
    System.out.println(inverse);

    ArrayMatrix a = ArrayMatrix.of(2, 2, 1, 1, 1, 2);
    inverse = LinearAlgebra.inv(a);

    // assertArrayEquals(new double[] {2, -1, -1, 1}, inverse.asDoubleArray(), 0.001);
    //
    // Matrix pinv = LinearAlgebra.pinv(a);
    // System.out.println(pinv);
    // assertArrayEquals(new double[] {2, -1, -1, 1}, pinv.asDoubleArray(), 0.001);


  }

  @Test
  public void testPInv() throws Exception {
    ArrayMatrix matrix = ArrayMatrix.of(2, 2, 1, 2, 1, 2);
    Matrix inverse = LinearAlgebra.pinv(matrix);
    assertEquals(0.1, inverse.get(0, 0), 0.001);
    assertEquals(0.1, inverse.get(0, 1), 0.001);
    assertEquals(0.2, inverse.get(1, 0), 0.001);
    assertEquals(0.2, inverse.get(1, 1), 0.001);

    double[] A = {0, 2, 0, 1, 2, 2, 3, 2, 4, -3, 0, 1., 6, 1, -6, -5};
    ArrayMatrix am = ArrayMatrix.of(4, 4, A);
    inverse = LinearAlgebra.pinv(am);

    double[] expected =
        {-0.0256410256410256, 0.1794871794871795, -0.5299145299145301, 0.6410256410256413,
            0.12820512820512825, 0.10256410256410253, 0.3162393162393162, -0.20512820512820507,
            0.08974358974358979, -0.12820512820512814, -0.1452991452991454, 0.25641025641025655,
            0.06410256410256417, 0.051282051282051266, -0.0085470085470086, -0.10256410256410259};

    // double[] actual = inverse.asDoubleArray();
    // assertArrayEquals(expected, actual, 0.001);

  }

  @Test
  public void testSingularValueDecomposition() throws Exception {
    ArrayMatrix a = ArrayMatrix.of(2, 3, 4, 2, 1, 5, 7, 10);
    SingularValueDecomposition svd = LinearAlgebra.svd(a);


    ArrayMatrix original = new ArrayMatrix(2, 3);
    // Matrices.mmuli(svd.u.mmul(svd.s), Transpose.NO, svd.v, Transpose.YES,
    // original.asDoubleArray());
    //
    // assertArrayEquals(a.asDoubleArray(), original.asDoubleArray(), 0.000001);

    // assertArrayEquals(new double[] {13.629052142997777, 0.0, 0.0, 3.0412066163691827, 0.0, 0.0},
    // svd.getDiagonal().asDoubleArray(), 0.001);

    // assertArrayEquals(new double[] {-0.2580260748828914, -0.9661379532346965,
    // -0.9661379532346965,
    // 0.2580260748828913}, svd.getLeftSingularValues().asDoubleArray(), 0.001);

    // assertArrayEquals(new double[] {-0.43016887779075563, -0.5340810018214224,
    // -0.7278133140261089,
    // -0.8465131650929605, -0.041461629607952355, 0.5307507838850085, 0.3136402364246974,
    // -0.8444160211434162, 0.43427109658804297}, svd.getRightSingularValues().asDoubleArray(),
    // 0.001);


    a = ArrayMatrix.of(4, 4, 0, 2, 0, 1, 2, 2, 3, 2, 4, -3, 0, 1., 6, 1, -6, -5);
    svd = LinearAlgebra.svd(a);

    // Restore the original matrix by the equation A = U*S*V'
    original = new ArrayMatrix(4, 4);
    // Matrices.mmuli(svd.u.mmul(svd.s), Transpose.NO, svd.v, Transpose.YES,
    // original.asDoubleArray());
    // assertArrayEquals(a.asDoubleArray(), original.asDoubleArray(), 0.000001);
    //
    // assertArrayEquals(new double[] {10.180981085161006, 0.0, 0.0, 0.0, 0.0, 5.2693872994459205,
    // 0.0, 0.0, 0.0, 0.0, 4.182537939277787, 0.0, 0.0, 0.0, 0.0, 1.0428604981174192}, svd
    // .getDiagonal().asDoubleArray(), 0.001);
    //
    // assertArrayEquals(new double[] {0.04885384184464653, 0.15792762180946246,
    // -0.19424189513562845,
    // -0.9669241203841933, 0.01020025693305927, -0.5962025549039872, -0.800222683642899,
    // 0.06389150857363936, 0.47263660909390987, 0.6926548354262623, -0.49121698859024465,
    // 0.23569001850982177, -0.8798431068319672, 0.3739393784508175, -0.2839359309383164,
    // 0.07366027237445737}, svd.getLeftSingularValues().asDoubleArray(), 0.001);
    //
    // assertArrayEquals(new double[] {-0.6151329628101067, 0.0028842497678057163,
    // 0.6163774920356119,
    // 0.4916115402221823, -0.7609891938213351, 0.24529511499304546, -0.41218392058260367,
    // -0.4368411256519435, 0.19953957134805714, 0.9659025051231179, 0.15871329916363666,
    // 0.045015538780645376, 0.05187334978433845, -0.08279093102936873, 0.6519150953871502,
    // -0.751971758597226}, svd.getRightSingularValues().asDoubleArray(), 0.001);
  }
}

package org.briljantframework.matrix;

import static org.briljantframework.matrix.Matrices.randn;
import static org.briljantframework.matrix.Matrices.zeros;

import org.briljantframework.Utils;
import org.briljantframework.complex.Complex;
import org.junit.Test;

public class ArrayComplexMatrixTest {

  @Test
  public void testConstructor() throws Exception {
    // ComplexMatrix matrix =
    // new ArrayComplexMatrix(2, 3, Arrays.asList(new Complex(1, 2.0), Complex.I, Complex.ONE,
    // Complex.ZERO, new Complex(3, 4), new Complex(10, 0)));
    //
    // System.out.println(matrix);
    // System.out.println(matrix.conjugateTranspose());
    // System.out.println(matrix.conjugateTranspose().mmul(matrix));
    // System.out.println(matrix.map(Complex::sqrt));
    // System.out.println(zeros(2, 3).assign(matrix, Complex::abs));
    // System.out.println(matrix.get(0, 2).abs());
    //

    Matrix a = Matrices.fill(2, 3, -2);
    Matrix randn = randn(10000, 100);
    Matrix zeros = zeros(100, 10000);

    ComplexMatrix rndc =
        new ArrayComplexMatrix(10000, 100).assign(() -> new Complex(Utils.getRandom()
            .nextGaussian()));
    ComplexMatrix zerosc = new ArrayComplexMatrix(100, 10000);

    long s = System.currentTimeMillis();
    // zeros.assign(randn, Math::sqrt);

//    System.out.println(rndc.getView(0, 0, 10, 10));
    zerosc.mmul(rndc);
    // zeros.mmul(randn);
    System.out.println(System.currentTimeMillis() - s);
    // matrix.assign(a, Complex::new);
    // System.out.println(new Complex(-2).sqrt());
    // matrix.assign(a, x -> new Complex(x).sqrt());
    // System.out.println(matrix);
  }
}

package org.briljantframework.matrix;

import static org.briljantframework.matrix.Matrices.randn;
import static org.briljantframework.matrix.Matrices.zeros;

import java.util.Arrays;

import org.briljantframework.Utils;
import org.briljantframework.complex.Complex;
import org.junit.Test;

public class DoubleArrayComplexDoubleMatrixTest {

  @Test
  public void testConstructor() throws Exception {
    ComplexMatrix matrix =
        new DoubleArrayComplexMatrix(2, 3, Arrays.asList(new Complex(1, 2.0), Complex.I,
            Complex.ONE, Complex.ZERO, new Complex(3, 4), new Complex(10, 0)));
    //
    // System.out.println(matrix);
    // System.out.println(matrix.conjugateTranspose());
    System.out.println(matrix.conjugateTranspose().mmul(matrix));
    // System.out.println(matrix.map(Complex::sqrt));
    // System.out.println(zeros(2, 3).assign(matrix, Complex::abs));
    // System.out.println(matrix.get(0, 2).abs());
    //

    DoubleMatrix a = Matrices.fill(2, 3, -2);
    DoubleMatrix randn = randn(10000, 100);
    DoubleMatrix zeros = zeros(100, 10000);
    Utils.setRandomSeed(123);
    ComplexMatrix rndc =
        new ArrayComplexMatrix(10000, 100).assign(() -> new Complex(Utils.getRandom()
            .nextGaussian()));
    ComplexMatrix zerosc = new ArrayComplexMatrix(100, 10000);

    System.out.println(rndc.getShape());
    System.out.println(zerosc.getShape());
    long s = System.nanoTime();
    // zeros.assign(randn, Math::sqrt);

    // System.out.println(rndc.getView(0, 0, 10, 10));

    // ComplexBuilder b = new ComplexBuilder(0);
    // for (int i = 0; i < 100; i++) {
    // for (int j = 0; j < rndc.size(); j++) {
    // b.plus(rndc.get(j));
    // }
    // }
    zerosc.mmul(rndc);
    // zeros.mmul(randn);
    System.out.println((System.nanoTime() - s) / 1e6);
    // matrix.assign(a, Complex::new);
    // System.out.println(new Complex(-2).sqrt());
    // matrix.assign(a, x -> new Complex(x).sqrt());
    // System.out.println(matrix);
  }
}

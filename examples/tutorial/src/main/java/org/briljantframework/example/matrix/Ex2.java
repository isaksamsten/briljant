package org.briljantframework.example.matrix;

import org.briljantframework.Bj;
import org.briljantframework.matrix.DoubleMatrix;

public class Ex2 {

  public static void main(String[] args) {
    DoubleMatrix a = Bj.range(0, 10).reshape(5, 2).asDoubleMatrix();
    DoubleMatrix b = Bj.linspace(0, 10, 50).reshape(10, 5);
    DoubleMatrix c = Bj.linspace(0, 2 * Math.PI, 100).map(Math::sqrt);
    DoubleMatrix d = Bj.matrix(new double[][]{
        new double[]{0, 5},
        new double[]{1, 6},
        new double[]{2, 7},
        new double[]{3, 8},
        new double[]{4, 9}
    });

    DoubleMatrix e = Bj.matrix(new double[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9}).reshape(5, 2);
  }
}

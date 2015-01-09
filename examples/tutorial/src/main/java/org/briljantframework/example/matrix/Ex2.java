package org.briljantframework.example.matrix;

import static org.briljantframework.matrix.Doubles.*;

import org.briljantframework.Utils;
import org.briljantframework.matrix.DoubleMatrix;
import org.briljantframework.matrix.Ints;

public class Ex2 {
  public static void main(String[] args) {
    DoubleMatrix a = Ints.range(0, 10).reshape(5, 2).asDoubleMatrix();
    DoubleMatrix b = linspace(0, 10, 50).reshape(10, 5);
    DoubleMatrix c = linspace(0, 2 * Math.PI, 100).mapi(Math::sin);
    DoubleMatrix d =
        newMatrix(new double[][] {new double[] {0, 5}, new double[] {1, 6}, new double[] {2, 7},
            new double[] {3, 8}, new double[] {4, 9}});

    DoubleMatrix e = newMatrix(0, 1, 2, 3, 4, 5, 6, 7, 8, 9).reshape(5, 2);
    DoubleMatrix f = parseMatrix("0,5;1,6;2,7;3,8;4,9");
    DoubleMatrix g = newMatrix(10, Utils.getRandom()::nextGaussian).reshape(2, 5);
  }
}

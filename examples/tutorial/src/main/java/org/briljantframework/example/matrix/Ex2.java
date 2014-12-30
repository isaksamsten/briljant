package org.briljantframework.example.matrix;

import org.briljantframework.matrix.Matrix;

import static org.briljantframework.matrix.Matrices.matrix;
import static org.briljantframework.matrix.Matrices.parseMatrix;
import static org.briljantframework.matrix.Matrices.range;

public class Ex2 {
  public static void main(String[] args) {
    Matrix a = range(0, 10, 1).reshape(5, 2);

    // verbose
    Matrix b =
        matrix(new double[][] {new double[] {0, 5}, new double[] {1, 6}, new double[] {2, 7},
            new double[] {3, 8}, new double[] {4, 9}});

    Matrix c = matrix(0, 1, 2, 3, 4, 5, 6, 7, 8, 9).reshape(5, 2);

    Matrix d = parseMatrix("0,5;1,6;2,7;3,8;4,9");
  }
}

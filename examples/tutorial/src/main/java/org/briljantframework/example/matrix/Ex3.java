package org.briljantframework.example.matrix;

import static org.briljantframework.matrix.Matrices.randn;
import static org.briljantframework.matrix.Matrices.zeros;

import org.briljantframework.matrix.DoubleMatrix;

/**
 * Created by Isak Karlsson on 01/01/15.
 */
public class Ex3 {

  public static void main(String[] args) {
    DoubleMatrix a = zeros(10, 10);
    DoubleMatrix b = randn(10, 10);
    a.assign(b);

    a.assign(b, Math::sqrt);

    DoubleMatrix x = a.assign(b, e -> e * e).reshape(5, 20);
    System.out.println(x);

    // Take the first row
    DoubleMatrix firstRow = b.getRowView(0);

    // Modifications views share data with the original
    firstRow.assign(zeros(10).getRowView(0));
    // Take the upper 4 elements of b
    DoubleMatrix up = b.getView(0, 0, 4, 4);
    System.out.println(up);
  }
}

package org.briljantframework.example.matrix;

import org.briljantframework.Bj;
import org.briljantframework.distribution.NormalDistribution;
import org.briljantframework.matrix.DoubleMatrix;

/**
 * Created by Isak Karlsson on 01/01/15.
 */
public class Ex3 {

  public static void main(String[] args) {
    NormalDistribution sampler = new NormalDistribution(-1, 1);
    DoubleMatrix a = Bj.doubleMatrix(10, 10);
    DoubleMatrix b = Bj.rand(100, sampler).reshape(10, 10);
    a.assign(b);
    a.assign(b, Math::sqrt);
    DoubleMatrix x = a.assign(b, e -> e * e).reshape(5, 20);
    System.out.println(x);

    // Take the first row
    DoubleMatrix firstRow = b.getRowView(0);

    // Modifications views share data with the original
    firstRow.assign(Bj.doubleMatrix(10, 10).getRowView(0));

    // Take the upper 4 elements of b
    DoubleMatrix up = b.getView(0, 0, 4, 4);
    System.out.println(up);
  }
}

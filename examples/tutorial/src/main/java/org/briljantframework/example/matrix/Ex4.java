package org.briljantframework.example.matrix;

import static org.briljantframework.matrix.Matrices.fill;
import static org.briljantframework.matrix.Matrices.randn;

import org.briljantframework.Utils;
import org.briljantframework.matrix.DoubleMatrix;

/**
 * Created by Isak Karlsson on 02/01/15.
 */
public class Ex4 {

  public static void main(String[] args) {
    Utils.setRandomSeed(123);
    DoubleMatrix a = fill(9, 2).reshape(3,3);
    DoubleMatrix b = randn(3, 3);
    b.add(a);
    b.sub(a);
    b.mul(a);
    b.mmul(a);
    b.add(1, a, -1); // == b.add(a.mul(-1));
    b.add(1, a, -1).equalsTo(b.add(a.mul(-1)));
    /*-
     * true  true  true  
     * true  true  true  
     * true  true  true  
     * Shape: 3x3
     */
  }
}

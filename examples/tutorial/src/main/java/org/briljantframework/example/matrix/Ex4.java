package org.briljantframework.example.matrix;

import org.briljantframework.Bj;
import org.briljantframework.Utils;
import org.briljantframework.distribution.NormalDistribution;
import org.briljantframework.matrix.DoubleMatrix;

/**
 * Created by Isak Karlsson on 02/01/15.
 */
public class Ex4 {

  public static void main(String[] args) {
    Utils.setRandomSeed(123);
    DoubleMatrix a = Bj.doubleVector(9).assign(2).reshape(3, 3);
    DoubleMatrix b = Bj.rand(9, new NormalDistribution(-1, 1)).reshape(3, 3);
    b.add(a);
    b.sub(a);
    b.mul(a);
    b.mmul(a);
    b.add(1, a, -1); // == b.add(a.mul(-1));
    b.add(1, a, -1).satisfies(b.add(a.mul(-1)), (x, y) -> x == y);
    /*-
     * true  true  true  
     * true  true  true  
     * true  true  true  
     * Shape: 3x3
     */
  }
}

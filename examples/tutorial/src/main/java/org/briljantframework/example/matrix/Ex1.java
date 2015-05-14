package org.briljantframework.example.matrix;


import org.briljantframework.Bj;
import org.briljantframework.matrix.Dim;
import org.briljantframework.matrix.DoubleMatrix;

import java.util.Random;

public class Ex1 {

  public static void main(String[] args) {
    Random random = new Random(123);
    DoubleMatrix m = Bj.doubleMatrix(3, 5);
    m.assign(random::nextGaussian);
    /*- =>
     * -1.4380   0.2775   1.3520   1.0175  -0.4671
     *  0.6342   0.1843   0.3592   1.3716  -0.6711
     *  0.2261  -0.3652  -0.2053  -1.8902  -1.6794
     * Shape: 3x5
     */

    m.getShape(); // => 3x5
    assert m.rows() == 3;
    assert m.columns() == 5;
    assert m.size() == 15;

    Bj.mean(m, Dim.R);
    /*- =>
     * -0.1926   0.0322   0.5020   0.1663  -0.9392  
     * Shape: 1x5
     */
  }
}

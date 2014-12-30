package org.briljantframework.example.matrix;

import static org.briljantframework.matrix.Matrices.mean;
import static org.briljantframework.matrix.Matrices.zeros;

import java.util.Random;

import org.briljantframework.matrix.Axis;
import org.briljantframework.matrix.Matrix;

public class Ex1 {

  public static void main(String[] args) {
    Random random = new Random(123);
    Matrix m = zeros(100000, 5);
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

    mean(m, Axis.ROW);
    /*- =>
     * -0.1926   0.0322   0.5020   0.1663  -0.9392  
     * Shape: 1x5
     */
  }
}

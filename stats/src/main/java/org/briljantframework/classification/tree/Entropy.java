package org.briljantframework.classification.tree;

import org.briljantframework.matrix.DoubleMatrix;

/**
 * Created by Isak Karlsson on 08/09/14.
 */
public class Entropy implements Impurity {

  private static final Entropy instance = new Entropy();

  private static final double LOG_2 = Math.log(2);

  private Entropy() {}

  /**
   * The constant instance.
   */
  public static Entropy getInstance() {
    return instance;
  }

  /**
   * @param values the getPosteriorProbabilities
   * @return the impurity
   * @see Impurity#impurity(org.briljantframework.matrix.DoubleMatrix)
   */
  @Override
  public double impurity(DoubleMatrix values) {
    return -1 * values.reduce(0, (v, acc) -> acc + v * (Math.log(v) / LOG_2));

    // for (double value : values) {
    // if (value != 0) {
    // entropy += value * (Math.log(value) / LOG_2);
    // }
    // }
    // return -1 * entropy;
  }
}

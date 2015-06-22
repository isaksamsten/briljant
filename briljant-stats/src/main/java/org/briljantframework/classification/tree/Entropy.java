package org.briljantframework.classification.tree;

import org.briljantframework.matrix.DoubleArray;

/**
 * @author Isak Karlsson
 */
public class Entropy implements Impurity {

  private static final Entropy INSTANCE = new Entropy();

  private static final double LOG_2 = Math.log(2);

  private Entropy() {}

  public static Entropy getInstance() {
    return INSTANCE;
  }

  @Override
  public double impurity(DoubleArray values) {
    double entropy = 0;
    for (int i = 0; i < values.size(); i++) {
      double value = values.get(i);
      if (value != 0) {
        entropy += value * (Math.log(value) / LOG_2);
      }
    }
    return -1 * entropy;
  }
}

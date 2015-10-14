package org.briljantframework.regression;

import java.util.Collections;
import java.util.Set;

import org.briljantframework.array.DoubleArray;
import org.briljantframework.data.dataframe.DataFrame;
import org.briljantframework.data.vector.Vector;
import org.briljantframework.evaluation.EvaluationContext;
import org.briljantframework.linalg.LinearAlgebra;
import org.briljantframework.supervised.Characteristic;

/**
 * @author Isak Karlsson <isak-kar@dsv.su.se>
 */
public final class LinearRegression implements Regression {

  private final DoubleArray theta;

  private LinearRegression(DoubleArray theta) {
    this.theta = theta;
  }

  public DoubleArray getTheta() {
    return theta;
  }

  @Override
  public double predict(Vector y) {
    return 0;
  }

  @Override
  public Vector predict(DataFrame x) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Set<Characteristic> getCharacteristics() {
    return Collections.emptySet();
  }

  @Override
  public void evaluate(EvaluationContext ctx) {

  }

  /**
   * @author Isak Karlsson
   */
  public static class Learner implements Regression.Learner {

    public Learner() {}

    @Override
    public Regression fit(DoubleArray x, DoubleArray y) {
      return new LinearRegression(LinearAlgebra.leastLinearSquares(x, y));
    }

  }
}

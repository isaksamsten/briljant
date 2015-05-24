/*
 * ADEB - machine learning pipelines made easy Copyright (C) 2014 Isak Karlsson
 * 
 * This program is free software; you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program; if
 * not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 */

package org.briljantframework.classification;

import org.briljantframework.Bj;
import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.matrix.DoubleMatrix;
import org.briljantframework.matrix.IntMatrix;
import org.briljantframework.vector.Vec;
import org.briljantframework.vector.Vector;

import java.util.Arrays;
import java.util.EnumSet;

import static com.google.common.base.Preconditions.checkArgument;
import static org.briljantframework.matrix.Matrices.shuffle;

/**
 * Logistic regression implemented using Stochastic Gradient Descent.
 * <p>
 * Created by isak on 01/07/14.
 */
public class LogisticRegression implements Classifier {

  private final int iterations;
  private final double learningRate;
  private final double regularization;
  private final double costEpsilon;

  private LogisticRegression(Builder builder) {
    this.learningRate = builder.learningRate;
    this.iterations = builder.iterations;
    this.regularization = builder.regularization;
    this.costEpsilon = builder.costEpsilon;
  }

  public static Builder builder() {
    return withIterations(10);
  }

  public static Builder withIterations(int iterations) {
    return new Builder(iterations);
  }

  public double getLearningRate() {
    return learningRate;
  }

  public double getRegularization() {
    return regularization;
  }

  public int getIterations() {
    return iterations;
  }

  @Override
  public String toString() {
    return String.format("LogisticRegression(%d, %.4f, %.3f)",
                         iterations, learningRate, regularization);
  }

  @Override
  public Predictor fit(DataFrame df, Vector target) {
    checkArgument(df.rows() == target.size(),
                  "The number of training instances must equal the number of target");
    Vector unique = Vec.unique(target);
    if (unique.size() > 2 || unique.size() < 1) {
      throw new IllegalArgumentException(
          "LogisticRegression only support binary classification tasks.");
    }

    DoubleMatrix x = Bj.hstack(Arrays.asList(
        Bj.doubleVector(df.rows()).assign(1),
        df.toMatrix().asDoubleMatrix()
    ));
    DoubleMatrix y = Bj.doubleVector(target.size());
    for (int i = 0; i < y.size(); i++) {
      y.set(i, Vec.find(unique, target, i));
    }
    DoubleMatrix theta = sdg(x, y, Bj.range(0, y.size()).copy());
    return new Predictor(theta, unique);
  }

  protected DoubleMatrix sdg(DoubleMatrix x, DoubleMatrix y, IntMatrix indexes) {
    DoubleMatrix theta = Bj.doubleMatrix(x.columns(), 1);
    int rows = x.rows();
    double prevCost = 0;
    for (int j = 0; j < this.iterations; j++) {
      shuffle(indexes).forEach(i -> {
        DoubleMatrix xi = x.getRowView(i);
//        double reg = (regularization/(2*rows)) * Bj.dot(theta, theta);
        double update = (learningRate * (y.get(i) - h(xi, theta)));
        theta.assign(xi, (v, xij) -> (v + xij * update) /*+ (0.5 * regularization * w2)*/
                                     /*(1.0 - (learningRate * regularization) / rows)*/);
//        System.out.println(reg);
      });
//      if (costEpsilon > 0) {
//        double cost = cost(theta, x, y);
//          System.out.println(cost);
//        if (Math.abs(cost - prevCost) < costEpsilon) {
//          break;
//        }
//        prevCost = cost;
//      }
    }
//    System.exit(0);
    return theta;
  }

  private static double cost(DoubleMatrix theta, DoubleMatrix x, DoubleMatrix y) {
    int m = x.rows();
    DoubleMatrix z = x.mmul(theta);
    DoubleMatrix sigmoid = sigmoid(z);
    return 1.0 / m * Bj.sum(
        y.mul(-1, sigmoid.map(Math::log), 1).sub(y.rsub(1).mul(sigmoid.rsub(1).map(Math::log)))
    );
  }

  private static double h(DoubleMatrix xi, DoubleMatrix t) {
    return sigmoid(Bj.dot(xi, t));
  }

  private static double sigmoid(double z) {
    return 1 / (1 + Math.exp(-z));
  }

  private static DoubleMatrix sigmoid(DoubleMatrix z) {
    return z.map(LogisticRegression::sigmoid);
  }

  public static class Builder implements Classifier.Builder<LogisticRegression> {

    private int iterations = 100;
    private double learningRate = 0.0001;
    private double regularization = 0.01;
    private double costEpsilon = 0.001;

    private Builder(int iterations) {
      this.iterations = iterations;
    }

    public Builder withIterations(int it) {
      this.iterations = it;
      return this;
    }

    public Builder withLearningRate(double lambda) {
      this.learningRate = lambda;
      return this;
    }

    public Builder withRegularization(double alpha) {
      this.regularization = alpha;
      return this;
    }

    public Builder withCostEpsilon(double epsiolon) {
      this.costEpsilon = epsiolon;
      return this;
    }

    @Override
    public LogisticRegression build() {
      return new LogisticRegression(this);
    }
  }

  /**
   * @author Isak Karlsson
   */
  public static class Predictor extends AbstractPredictor {

    private final DoubleMatrix theta;

    private Predictor(DoubleMatrix theta, Vector classes) {
      super(classes);
      this.theta = theta;
    }

    @Override
    public DoubleMatrix estimate(Vector record) {
      DoubleMatrix row = Bj.doubleMatrix(1, record.size() + 1);
      row.set(0, 1);
      for (int i = 0; i < record.size(); i++) {
        row.set(i + 1, record.getAsDouble(i));
      }

      double prob = h(row, theta);
      DoubleMatrix probs = Bj.doubleVector(2);
      probs.set(0, 1 - prob);
      probs.set(1, prob);
      return probs;
    }

    public DoubleMatrix theta() {
      return theta;
    }

    @Override
    public EnumSet<Characteristics> getCharacteristics() {
      return EnumSet.of(Characteristics.ESTIMATOR);
    }

    @Override
    public String toString() {
      return String.format("LogisticRegression.Model(%s)", theta());
    }
  }
}

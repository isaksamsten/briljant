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
import org.briljantframework.dataframe.Record;
import org.briljantframework.matrix.DoubleMatrix;
import org.briljantframework.matrix.IntMatrix;
import org.briljantframework.vector.Convert;
import org.briljantframework.vector.Vec;
import org.briljantframework.vector.Vector;

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

  private LogisticRegression(Builder builder) {
    this.learningRate = builder.learningRate;
    this.iterations = builder.iterations;
    this.regularization = builder.regularization;
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
    return String.format("LogisticRegression(%d, %.4f, %.3f)", iterations, learningRate,
                         regularization);
  }

  @Override
  public Model fit(DataFrame x, Vector y) {
    checkArgument(x.rows() == y.size(),
                  "The number of training instances must equal the number of target");
    return fit(x, y, Bj.range(0, y.size()));
  }

  protected Model fit(DataFrame x, Vector y, IntMatrix indexes) {
    DoubleMatrix theta = Bj.doubleMatrix(1, x.columns());
    Vector adaptedTheta = Convert.toAdapter(theta);
    Vector classes = Vec.unique(y);
    for (int j = 0; j < this.iterations; j++) {
      shuffle(indexes).forEach(i -> {
        Record row = x.getRecord(i);
        double update = learningRate * (y.getAsDouble(i) - Vec.sigmoid(row, adaptedTheta));
        // theta.add(1, row, update);
        // TODO(isak): fix!
        // theta.add(1, row, update);
        // Matrices.add(row, update, theta, 1, theta.asDoubleArray());
        theta.update(v -> v * (1.0 - (learningRate * regularization) / x.rows()));
      });
    }

    return new Model(adaptedTheta, classes);
  }

  public static class Builder implements Classifier.Builder<LogisticRegression> {

    private int iterations = 100;
    private double learningRate = 0.0001;
    private double regularization = 0.01;

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

    @Override
    public LogisticRegression build() {
      return new LogisticRegression(this);
    }
  }

  /**
   * @author Isak Karlsson
   */
  public static class Model extends AbstractPredictor {

    private final Vector theta;

    public Model(Vector theta, Vector classes) {
      super(classes);
      this.theta = theta;
    }

    @Override
    public Object predict(Vector record) {
      double prob = Vec.sigmoid(record, theta);
      return prob > 0.5 ? 1 : 0;
    }

    @Override
    public DoubleMatrix estimate(Vector record) {
      return null;
    }

    public Vector theta() {
      return theta;
    }

    @Override
    public String toString() {
      return String.format("LogisticRegression.Model(%s)", theta());
    }
  }
}

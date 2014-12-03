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

package org.briljantframework.learning.linear;

import org.briljantframework.Utils;
import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.learning.Classifier;
import org.briljantframework.learning.Prediction;
import org.briljantframework.learning.ensemble.Ensemble;
import org.briljantframework.learning.example.Example;
import org.briljantframework.learning.example.Examples;
import org.briljantframework.matrix.RealArrayMatrix;
import org.briljantframework.matrix.RealMatrices;
import org.briljantframework.matrix.RealMatrix;
import org.briljantframework.vector.Vector;

/**
 * Logistic regression implemented using Stochastic Gradient Descent.
 * <p>
 * Created by isak on 01/07/14.
 */
public class LogisticRegression implements Classifier {

  private final int iterations;

  private final double learningRate;
  private final double regularization;


  private Examples examples = null;

  /**
   * Instantiates a new Logistic regression.
   *
   * @param builder the builder
   * @param sample the sample
   */
  public LogisticRegression(Builder builder, Examples sample) {
    this(builder);
    this.examples = sample;
  }

  private LogisticRegression(Builder builder) {
    this.learningRate = builder.learningRate;
    this.iterations = builder.iterations;
    this.regularization = builder.regularization;
  }

  /**
   * Builder builder.
   *
   * @return the builder
   */
  public static Builder builder() {
    return withIterations(10);
  }

  /**
   * Iterations builder.
   *
   * @param iterations the iterations
   * @return the builder
   */
  public static Builder withIterations(int iterations) {
    return new Builder(iterations);
  }

  /**
   * Gets learning rate.
   *
   * @return the learning rate
   */
  public double getLearningRate() {
    return learningRate;
  }

  /**
   * Gets regularization.
   *
   * @return the regularization
   */
  public double getRegularization() {
    return regularization;
  }

  /**
   * Gets iterations.
   *
   * @return the iterations
   */
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
    // if (!y.getType().contains(Arrays.asList(Categoric.valueOf(0), Categoric.valueOf(1)))) {
    // throw new IllegalArgumentException("require target to be either 0 or 1");
    // }

    if (x.rows() != y.size()) {
      throw new IllegalArgumentException(
          "The number of training instances must equal the number ot target " + "values");
    }

    int[] perms;
    if (examples != null) {
      // Only take the examples in the sample to use
      perms = new int[(int) examples.getTotalWeight()];
      int p = 0;
      for (Example e : examples) {
        for (int i = 0; i < e.getWeight(); i++) {
          perms[p++] = e.getIndex();
        }
      }
    } else {
      // Take all values in the frame
      perms = new int[x.rows()];
      for (int i = 0; i < x.rows(); i++) {
        perms[i] = i;
      }
    }

    return fit(x, y, perms);
  }

  /**
   * Fit logistic regression classification.
   *
   * @param x the x
   * @param y the y
   * @param indicies the indicies
   * @return the logistic regression model
   */
  protected Model fit(DataFrame x, Vector y, int[] indicies) {
    RealArrayMatrix theta = new RealArrayMatrix(1, x.columns());
    for (int j = 0; j < this.iterations; j++) {
      Utils.permute(indicies);

      for (int i : indicies) {
        Vector row = x.getRow(i);
        double update = learningRate * (y.get(i) - RealMatrices.sigmoid(row, theta));
        RealMatrices.add(row, update, theta, 1, theta.asDoubleArray());
        theta.muli(1.0 - (learningRate * regularization) / x.rows());
      }
    }

    return new Model(theta);
  }

  /**
   * Builder for constructing a logistic regression classifier
   */
  public static class Builder implements Ensemble.Member, Classifier.Builder<LogisticRegression> {

    private int iterations = 100;
    private double learningRate = 0.0001;
    private double regularization = 0.01;

    private Builder(int iterations) {
      this.iterations = iterations;
    }

    /**
     * Iterations builder.
     *
     * @param it the it
     * @return the builder
     */
    public Builder withIterations(int it) {
      this.iterations = it;
      return this;
    }

    /**
     * The learning rate (i.e. the "step-size") of the gradient descent
     *
     * @param lambda default 0.01
     * @return this builder
     */
    public Builder withLearningRate(double lambda) {
      this.learningRate = lambda;
      return this;
    }

    /**
     * The regularization constant - reduced if underfitting and increased if overfitting
     *
     * @param alpha default 0.0001
     * @return this builder
     */
    public Builder withRegularization(double alpha) {
      this.regularization = alpha;
      return this;
    }

    /**
     * Create logistic regression.
     *
     * @return the finished LogisticRegression classifier
     */
    @Override
    public LogisticRegression create() {
      return new LogisticRegression(this);
    }

    @Override
    public LogisticRegression create(Examples sample) {
      return new LogisticRegression(this, sample);
    }
  }

  /**
   * Created by isak on 03/07/14.
   */
  public static class Model implements org.briljantframework.learning.Model {
    private final RealMatrix theta;

    /**
     * Instantiates a new Logistic regression classification.
     *
     * @param theta the theta
     */
    public Model(RealArrayMatrix theta) {
      this.theta = theta;
    }

    @Override
    public Prediction predict(Vector row) {
      double prob = RealMatrices.sigmoid(row, theta);
      return Prediction.binary("1", prob, "0", 1 - prob);
    }

    /**
     * Theta vector.
     *
     * @return the vector
     */
    public RealMatrix theta() {
      return theta;
    }


    @Override
    public String toString() {
      return String.format("LogisticRegression.Model(%s)", theta());
    }
  }
}

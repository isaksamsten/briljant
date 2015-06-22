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
import org.briljantframework.Check;
import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.evaluation.measure.LogLoss;
import org.briljantframework.evaluation.result.EvaluationContext;
import org.briljantframework.evaluation.result.Sample;
import org.briljantframework.matrix.DoubleArray;
import org.briljantframework.matrix.IntArray;
import org.briljantframework.optimize.DifferentialFunction;
import org.briljantframework.optimize.LimitedMemoryBfgsOptimizer;
import org.briljantframework.optimize.NonlinearOptimizer;
import org.briljantframework.stat.RunningStatistics;
import org.briljantframework.vector.GenericVector;
import org.briljantframework.vector.Is;
import org.briljantframework.vector.Vec;
import org.briljantframework.vector.Vector;

import java.util.EnumSet;
import java.util.Objects;

/**
 * Logistic regression implemented using a quasi newton method based on
 * the limited memory BFGS.
 *
 * <p>References:
 * <ol>
 * <li>
 * Murphy, Kevin P. Machine learning: a probabilistic perspective. MIT press, 2012.
 * </li>
 *
 * </ol>
 *
 * @author Isak Karlsson
 */
public class LogisticRegression implements Classifier {

  private final double regularization;
  private final NonlinearOptimizer optimizer;

  private LogisticRegression(Builder builder) {
    Check.argument(!Double.isNaN(builder.regularization) &&
                   !Double.isInfinite(builder.regularization));
    this.regularization = builder.regularization;
    this.optimizer = Objects.requireNonNull(builder.optimizer);
  }

  public static Builder withIterations(int iterations) {
    return new Builder(iterations);
  }

  public static LogisticRegression regularized(double lambda) {
    return withIterations(500).withRegularization(lambda).build();
  }

  public static LogisticRegression create() {
    return regularized(0);
  }

  @Override
  public String toString() {
    return "LogisticRegression{" +
           "regularization=" + regularization +
           ", optimizer=" + optimizer +
           '}';
  }

  @Override
  public Predictor fit(DataFrame df, Vector target) {
    int n = df.rows();
    int m = df.columns();
    Check.argument(n == target.size(),
                   "The number of training instances must equal the number of target");
    Vector unique = Vec.unique(target);
    DoubleArray x = constructInputMatrix(df, n, m);
    IntArray y = Bj.intArray(target.size());
    for (int i = 0; i < y.size(); i++) {
      y.set(i, Vec.find(unique, target, i));
    }
    DoubleArray theta;
    DifferentialFunction objective;
    int k = unique.size();
    if (k == 2) {
      objective = new BinaryObjectiveFunction(regularization, x, y);
      theta = Bj.doubleArray(x.columns());
    } else if (k > 2) {
      objective = new SoftmaxObjectiveFunction(x, y, regularization, k);
      theta = Bj.doubleArray(x.columns(), k);
    } else {
      throw new IllegalArgumentException(String.format("Illegal classes. k >= 2 (%d >= 2)", k));
    }
    double logLoss = optimizer.optimize(objective, theta);

    Vector.Builder names = new GenericVector.Builder(Object.class).add("(Intercept)");
    df.getColumnIndex().forEach(names::add);
    return new Predictor(names.build(), theta, logLoss, unique);
  }

  protected DoubleArray constructInputMatrix(DataFrame df, int n, int m) {
    DoubleArray x = Bj.doubleArray(n, m + 1);
    for (int i = 0; i < n; i++) {
      x.set(i, 0, 1);
      for (int j = 0; j < df.columns(); j++) {
        double v = df.getAsDouble(i, j);
        if (Is.NA(v) || Double.isNaN(v)) {
          throw new IllegalArgumentException(
              String.format("Illegal input value at (%d, %d)", i, j - 1));
        }
        x.set(i, j + 1, v);
      }
    }
    return x;
  }

  private static class BinaryObjectiveFunction implements DifferentialFunction {

    private final double lambda;
    private final DoubleArray x;
    private final IntArray y;

    private BinaryObjectiveFunction(double lambda, DoubleArray x, IntArray y) {
      this.lambda = lambda;
      this.x = x;
      this.y = y;
    }

    @Override
    public double gradientCost(DoubleArray w, DoubleArray g) {
      int p = w.size();
      int n = x.rows();
      g.assign(0);
      double f = 0.0;
      for (int i = 0; i < n; i++) {
        double wx = Bj.dot(x.getRow(i), w);
        f += log1pe(wx) - y.get(i) * wx;

        double yi = y.get(i) - logistic(wx);
        for (int j = 1; j < p; j++) {
          g.set(j, g.get(j) - yi * x.get(i, j));
        }
        g.set(0, g.get(0) - yi);
      }
      if (lambda != 0.0) {
        double w2 = 0.0;
        for (int i = 1; i < p; i++) {
          double v = w.get(i);
          w2 += v * v;
        }

        f += 0.5 * lambda * w2;
        for (int j = 1; j < p; j++) {
          g.set(j, g.get(j) + lambda * w.get(j));
        }
      }

      return f;
    }

    @Override
    public double cost(DoubleArray w) {
      int n = x.rows();
      double f = 0.0;
      for (int i = 0; i < n; i++) {
        double wx = Bj.dot(x.getRow(i), w);
        f += log1pe(wx) - y.get(i) * wx;
      }

      if (lambda != 0.0) {
        int p = w.size() - 1;
        double w2 = 0.0;
        for (int i = 0; i < p; i++) {
          double v = w.get(i);
          w2 += v * v;
        }
        f += 0.5 * lambda * w2;
      }
      return f;
    }
  }

  private static class SoftmaxObjectiveFunction implements DifferentialFunction {

    private final DoubleArray x;
    private final IntArray y;
    private final double lambda;
    private final int k;

    private SoftmaxObjectiveFunction(DoubleArray x, IntArray y, double lambda, int k) {
      this.x = x;
      this.y = y;
      this.lambda = lambda;
      this.k = k;
    }

    @Override
    public double gradientCost(DoubleArray w, DoubleArray g) {
      double f = 0.0;
      int n = x.rows();
      int p = x.columns();
      w = w.reshape(p, k);
      g = g.reshape(p, k).assign(0);
      DoubleArray prob = Bj.doubleArray(k);
      for (int i = 0; i < n; i++) {
        DoubleArray xi = x.getRow(i);
        for (int j = 0; j < k; j++) {
          prob.set(j, Bj.dot(xi, w.getColumn(j)));
        }
        softmax(prob);
        f -= log(prob.get(y.get(i)));
        for (int j = 0; j < k; j++) {
          double yi = (y.get(i) == j ? 1.0 : 0.0) - prob.get(j);
          for (int l = 1; l < p; l++) {
            g.set(l, j, g.get(l, j) - yi * x.get(i, l));
          }
          g.update(0, j, v -> v - yi);
        }
      }

      if (lambda != 0.0) {
        double w2 = 0.0;
        for (int i = 0; i < k; i++) {
          for (int j = 0; j < p; j++) {
            double v = w.get(j, i);
            w2 += v * v;
          }
        }
        f += 0.5 * lambda * w2;
      }

      return f;
    }

    @Override
    public double cost(DoubleArray w) {
      double f = 0.0;
      int n = x.rows();
      int p = x.columns();
      w = w.reshape(p, k);
      DoubleArray prob = Bj.doubleArray(k);
      for (int i = 0; i < n; i++) {
        DoubleArray xi = x.getRow(i);
        for (int j = 0; j < k; j++) {
          prob.set(j, Bj.dot(xi, w.getColumn(j)));
        }

        softmax(prob);
        f -= log(prob.get(y.get(i)));
      }
      if (lambda != 0.0) {
        double w2 = 0.0;
        for (int i = 0; i < k; i++) {
          for (int j = 0; j < p; j++) {
            double v = w.get(j, i);
            w2 += v * v;
          }
        }

        f += 0.5 * lambda * w2;
      }

      return f;
    }
  }

  private static void softmax(DoubleArray prob) {
    double max = Bj.max(prob);

    double Z = 0.0;
    for (int i = 0; i < prob.size(); i++) {
      double p = Math.exp(prob.get(i) - max);
      prob.set(i, p);
      Z += p;
    }
    prob.divi(Z);
  }

  /**
   * Logistic sigmoid function.
   */
  public static double logistic(double x) {
    double y;
    if (x < -40) {
      y = 2.353853e+17;
    } else if (x > 40) {
      y = 1.0 + 4.248354e-18;
    } else {
      y = 1.0 + Math.exp(-x);
    }

    return 1.0 / y;
  }

  private static double log1pe(double x) {
    double y = 0.0;
    if (x > 15) {
      y = x;
    } else {
      y += Math.log1p(Math.exp(x));
    }

    return y;
  }

  private static double log(double x) {
    double y;
    if (x < 1E-300) {
      y = -690.7755;
    } else {
      y = Math.log(x);
    }
    return y;
  }

  /**
   * @author Isak Karlsson
   */
  public static class Predictor extends AbstractPredictor {

    private final Vector names;
    private final DoubleArray coefficients;
    private final double logLoss;

    private Predictor(Vector names, DoubleArray coefficients, double logLoss, Vector classes) {
      super(classes);
      this.names = names;
      this.coefficients = coefficients;
      this.logLoss = logLoss;
    }

    @Override
    public DoubleArray estimate(Vector record) {
      DoubleArray x = Bj.doubleArray(record.size() + 1);
      x.set(0, 1);
      for (int i = 0; i < record.size(); i++) {
        x.set(i + 1, record.getAsDouble(i));
      }

      Vector classes = getClasses();
      int k = classes.size();
      if (k > 2) {
        DoubleArray probs = Bj.doubleArray(k);
        double max = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < k; i++) {
          double prob = Bj.dot(x, coefficients.getColumn(i));
          if (prob > max) {
            max = prob;
          }
          probs.set(i, prob);
        }

        double z = 0;
        for (int i = 0; i < k; i++) {
          probs.set(i, Math.exp(probs.get(i) - max));
          z += probs.get(i);
        }
        return probs.divi(z);
      } else {
        double prob = logistic(Bj.dot(x, coefficients));
        DoubleArray probs = Bj.doubleArray(2);
        probs.set(0, 1 - prob);
        probs.set(1, prob);
        return probs;
      }
    }

    public DoubleArray getParamaters() {
      return coefficients.copy();
    }

    public double getLogLoss() {
      return logLoss;
    }

    public double getOddsRatio(Object coefficient) {
      int i = Vec.find(names, coefficient);
      if (i < 0) {
        throw new IllegalArgumentException("Label not found");
      }
      int k = getClasses().size();
      if (k > 2) {
        return coefficients.getRow(i).map(Math::exp)
            .collect(RunningStatistics::new, RunningStatistics::add).getMean();
      } else {
        return Math.exp(coefficients.get(i));
      }

    }

    @Override
    public void evaluation(EvaluationContext ctx) {
      super.evaluation(ctx);
      ctx.getOrDefault(LogLoss.class, LogLoss.Builder::new).add(Sample.IN, logLoss);
    }

    @Override
    public EnumSet<Characteristics> getCharacteristics() {
      return EnumSet.of(Characteristics.ESTIMATOR);
    }

    @Override
    public String toString() {
      return "LogisticRegression.Predictor{" +
             "coefficients=" + coefficients.flat() +
             ", logLoss=" + logLoss +
             '}';
    }
  }

  public static class Builder implements Classifier.Builder<LogisticRegression> {

    private int iterations = 100;
    private double regularization = 0.01;

    private NonlinearOptimizer optimizer;

    private Builder(int iterations) {
      this.iterations = iterations;
    }

    public Builder withIterations(int it) {
      this.iterations = it;
      return this;
    }

    public Builder withRegularization(double lambda) {
      this.regularization = lambda;
      return this;
    }

    public void setOptimizer(NonlinearOptimizer optimizer) {
      this.optimizer = optimizer;
    }

    @Override
    public LogisticRegression build() {
      if (optimizer == null) {
        // m ~ 20, [1] pp 252.
        optimizer = new LimitedMemoryBfgsOptimizer(20, iterations, 1E-5);
      }
      return new LogisticRegression(this);
    }

  }
}

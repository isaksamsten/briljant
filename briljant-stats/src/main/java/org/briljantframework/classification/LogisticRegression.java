package org.briljantframework.classification;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;

import org.briljantframework.Check;
import org.briljantframework.array.Arrays;
import org.briljantframework.array.DoubleArray;
import org.briljantframework.array.IntArray;
import org.briljantframework.data.Is;
import org.briljantframework.data.dataframe.DataFrame;
import org.briljantframework.data.vector.Vector;
import org.briljantframework.data.vector.Vectors;
import org.briljantframework.evaluation.EvaluationContext;
import org.briljantframework.evaluation.PointMeasure;
import org.briljantframework.evaluation.Sample;
import org.briljantframework.optimize.DifferentialFunction;
import org.briljantframework.optimize.LimitedMemoryBfgsOptimizer;
import org.briljantframework.optimize.NonlinearOptimizer;
import org.briljantframework.supervised.Characteristic;

/**
 * @author Isak Karlsson
 */
public class LogisticRegression extends AbstractClassifier {

  private final Vector names;

  /**
   * If {@code getClasses().size()} is larger than {@code 2}, coefficients is a a 2d-array where
   * each column is the coefficients for the the j:th class and the i:th feature.
   *
   * On the other hand, if {@code getClasses().size() <= 2}, coefficients is a 1d-array where each
   * element is the coefficient for the i:th feature.
   */
  private final DoubleArray coefficients;
  private final double logLoss;

  private LogisticRegression(Vector names, DoubleArray coefficients, double logLoss, Vector classes) {
    super(classes);
    this.names = names;
    this.coefficients = coefficients;
    this.logLoss = logLoss;
  }

  @Override
  public DoubleArray estimate(Vector record) {
    DoubleArray x = Arrays.doubleArray(record.size() + 1);
    x.set(0, 1); // set the intercept
    for (int i = 0; i < record.size(); i++) {
      x.set(i + 1, record.loc().getAsDouble(i));
    }

    Vector classes = getClasses();
    int k = classes.size();
    if (k > 2) {
      DoubleArray probs = Arrays.doubleArray(k);
      double max = Double.NEGATIVE_INFINITY;
      for (int i = 0; i < k; i++) {
        double prob = Arrays.dot(x, coefficients.getColumn(i));
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
      double prob = Learner.logistic(Arrays.dot(x, coefficients));
      DoubleArray probs = Arrays.doubleArray(2);
      probs.set(0, 1 - prob);
      probs.set(1, prob);
      return probs;
    }
  }

  public DoubleArray getParameters() {
    return coefficients.copy();
  }

  public double getLogLoss() {
    return logLoss;
  }

  public double getOddsRatio(Object coefficient) {
    int i = names.loc().indexOf(coefficient);
    if (i < 0) {
      throw new IllegalArgumentException("Label not found");
    }
    int k = getClasses().size();
    if (k > 2) {
      return Arrays.mean(Arrays.exp(coefficients.getRow(i)));
    } else {
      return Math.exp(coefficients.get(i));
    }

  }

  @Override
  public void evaluate(EvaluationContext ctx) {
    super.evaluate(ctx);
    ctx.getOrDefault(LogLoss.class, LogLoss.Builder::new).add(Sample.IN, logLoss);
  }

  @Override
  public Set<Characteristic> getCharacteristics() {
    return Collections.singleton(ClassifierCharacteristic.ESTIMATOR);
  }

  @Override
  public String toString() {
    return "LogisticRegression{" + "coefficients=" + coefficients + ", logLoss=" + logLoss + '}';
  }

  public static final class Configurator implements Classifier.Configurator<Learner> {

    private int iterations = 100;
    private double regularization = 0.01;

    private NonlinearOptimizer optimizer;

    public Configurator(int iterations) {
      this.iterations = iterations;
    }

    public Configurator setIterations(int it) {
      this.iterations = it;
      return this;
    }

    public Configurator setRegularization(double lambda) {
      this.regularization = lambda;
      return this;
    }

    public void setOptimizer(NonlinearOptimizer optimizer) {
      this.optimizer = optimizer;
    }

    @Override
    public Learner configure() {
      if (optimizer == null) {
        // m ~ 20, [1] pp 252.
        optimizer = new LimitedMemoryBfgsOptimizer(20, iterations, 1E-5);
      }
      return new Learner(this);
    }

  }

  /**
   * Logistic regression implemented using a quasi newton method based on the limited memory BFGS.
   *
   * <p>
   * References:
   * <ol>
   * <li>
   * Murphy, Kevin P. Machine learning: a probabilistic perspective. MIT press, 2012.</li>
   *
   * </ol>
   *
   * @author Isak Karlsson
   */
  public static class Learner implements Classifier.Learner {

    private final double regularization;
    private final NonlinearOptimizer optimizer;

    private Learner(Configurator builder) {
      Check.argument(!Double.isNaN(builder.regularization)
          && !Double.isInfinite(builder.regularization));
      this.regularization = builder.regularization;
      this.optimizer = Objects.requireNonNull(builder.optimizer);
    }

    public Learner() {
      this.regularization = 0.01;
      this.optimizer = new LimitedMemoryBfgsOptimizer(20, 100, 1E-5);
    }

    @Override
    public String toString() {
      return "LogisticRegression.Learner{" + "regularization=" + regularization + ", optimizer="
          + optimizer + '}';
    }

    @Override
    public LogisticRegression fit(DataFrame df, Vector target) {
      int n = df.rows();
      int m = df.columns();
      Check.argument(n == target.size(),
          "The number of training instances must equal the number of target");
      Vector classes = Vectors.unique(target);
      DoubleArray x = constructInputMatrix(df, n, m);
      IntArray y = Arrays.intArray(target.size());
      for (int i = 0; i < y.size(); i++) {
        y.set(i, Vectors.find(classes, target, i));
      }
      DoubleArray theta;
      DifferentialFunction objective;
      int k = classes.size();
      if (k == 2) {
        objective = new BinaryObjectiveFunction(x, y, regularization);
        theta = Arrays.doubleArray(x.columns());
      } else if (k > 2) {
        objective = new SoftmaxObjectiveFunction(x, y, regularization, k);
        theta = Arrays.doubleArray(x.columns(), k);
      } else {
        throw new IllegalArgumentException(String.format("Illegal classes. k >= 2 (%d >= 2)", k));
      }
      double logLoss = optimizer.optimize(objective, theta);

      Vector.Builder names = Vector.Builder.of(Object.class).add("(Intercept)");
      df.getColumnIndex().keySet().forEach(names::add);
      return new LogisticRegression(names.build(), theta, logLoss, classes);
    }

    protected DoubleArray constructInputMatrix(DataFrame df, int n, int m) {
      DoubleArray x = Arrays.doubleArray(n, m + 1);
      for (int i = 0; i < n; i++) {
        x.set(i, 0, 1);
        for (int j = 0; j < df.columns(); j++) {
          double v = df.loc().getAsDouble(i, j);
          if (Is.NA(v) || Double.isNaN(v)) {
            throw new IllegalArgumentException(String.format("Illegal input value at (%d, %d)", i,
                j - 1));
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

      private BinaryObjectiveFunction(DoubleArray x, IntArray y, double lambda) {
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
          double wx = Arrays.dot(x.getRow(i), w);
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
          double wx = Arrays.dot(x.getRow(i), w);
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
        DoubleArray prob = Arrays.doubleArray(k);
        for (int i = 0; i < n; i++) {
          DoubleArray xi = x.getRow(i);
          for (int j = 0; j < k; j++) {
            prob.set(j, Arrays.dot(xi, w.getColumn(j)));
          }
          softmax(prob);
          f -= log(prob.get(y.get(i)));
          for (int j = 0; j < k; j++) {
            double yi = (y.get(i) == j ? 1.0 : 0.0) - prob.get(j);
            for (int l = 1; l < p; l++) {
              g.set(l, j, g.get(l, j) - yi * x.get(i, l));
            }
            g.set(0, j, g.get(0, j) - yi);
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
        DoubleArray prob = Arrays.doubleArray(k);
        for (int i = 0; i < n; i++) {
          DoubleArray xi = x.getRow(i);
          for (int j = 0; j < k; j++) {
            prob.set(j, Arrays.dot(xi, w.getColumn(j)));
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
      double max = Arrays.max(prob);

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

  }

  /**
   * @author Isak Karlsson
   */
  public static class LogLoss extends PointMeasure {

    private LogLoss(Builder builder) {
      super(builder);
    }

    @Override
    public String getName() {
      return "LogLoss";
    }

    public static class Builder extends PointMeasure.Builder<LogLoss> {

      @Override
      public LogLoss build() {
        return new LogLoss(this);
      }
    }
  }
}

package org.briljantframework.distribution;

import org.briljantframework.matrix.DoubleMatrix;
import org.briljantframework.vector.DoubleVector;
import org.briljantframework.vector.Vector;

/**
 * @author Isak Karlsson
 */
public class NormalDistribution extends Distribution {

  /*
   * Parameters for the ppf calculation
   */
  private static final double ppf_a1 = -39.6968302866538 + 0.00000000000004;
  private static final double ppf_a2 = 220.946098424521 - 0.0000000000005;
  private static final double ppf_a3 = -275.928510446969 + 0.0000000000003;
  private static final double ppf_a4 = 138.357751867269;
  private static final double ppf_a5 = -30.6647980661472 + 0.00000000000004;
  private static final double ppf_a6 = 2.50662827745924 - 0.000000000000001;
  private static final double b1 = -54.4760987982241 + 0.00000000000004;
  private static final double b2 = 161.585836858041 - 0.0000000000001;
  private static final double b3 = -155.698979859887 + 0.0000000000004;
  private static final double b4 = 66.8013118877197 + 0.00000000000002;
  private static final double b5 = -13.280681552885 - 0.00000000000002;
  private static final double c1 = -7.78489400243029E-03 - 3E-18;
  private static final double c2 = -0.322396458041136 - 5E-16;
  private static final double c3 = -2.40075827716184 + 0.000000000000002;
  private static final double c4 = -2.54973253934373 - 0.000000000000004;
  private static final double c5 = 4.37466414146497 - 0.000000000000002;
  private static final double c6 = 2.93816398269878 + 0.000000000000003;
  private static final double d1 = 7.78469570904146E-03 + 2E-18;
  private static final double d2 = 0.32246712907004 - 2E-16;
  private static final double d3 = 2.445134137143 - 0.000000000000004;
  private static final double p_low = 0.02425;
  private static final double p_high = 1 - p_low;
  private static final double d4 = 3.75440866190742 - 0.000000000000004;

  /*
   * Parameters for the cdf calculation
   */
  private static final double cdf_a1 = 0.31938153;
  private static final double cdf_a2 = -0.356563782;
  private static final double cdf_a3 = 1.781477937;
  private static final double cdf_a4 = -1.821255978;
  private static final double cdf_a5 = 1.330274429;

  private final double mean;
  private final double std;

  public NormalDistribution(double mean, double std) {
    this.mean = mean;
    this.std = std;
  }

  public NormalDistribution() {
    this(0, 1);
  }

  /**
   * Cumulative distribution function. Returns the probability of a random variable being less than
   * or equal to {@code value}
   * 
   * @param value the value
   * @param mean the mean
   * @param scale the variance
   * @return the probability of {@code <= value}
   */
  public static double cdf(double value, double mean, double scale) {
    double x = (value - mean) / scale;
    double L = Math.abs(x);
    double k = 1 / (1 + 0.2316419 * L);
    double result =
        1 - ((1 / Math.sqrt(2 * Math.PI)) * Math.exp(-L * L / 2) * ((cdf_a1 * k) + (cdf_a2 * k * k)
            + (cdf_a3 * k * k * k) + (cdf_a4 * k * k * k * k) + (cdf_a5 * k * k * k * k * k)));
    if (x < 0) {
      result = 1 - result;
    }
    return result;
  }

  /**
   * Cumulative distribution function with 0 mean and unit variance
   * 
   * @param value the value
   * @return the probability of {@code <= value}
   */
  public static double cdf(double value) {
    return cdf(value, 0, 1);
  }

  /**
   * Element wise cumulative distribution function
   * 
   * @param in the vector
   * @param mean the mean
   * @param scale the variance
   * @return a new vector
   * @see #cdf(double, double, double)
   */
  public static Vector cdf(Vector in, double mean, double scale) {
    DoubleVector.Builder builder = new DoubleVector.Builder(0, in.size());
    for (int i = 0; i < in.size(); i++) {
      builder.add(cdf(in.getAsDouble(i), mean, scale));
    }
    return builder.build();
  }

  /**
   * Element wise cumulative distribution function
   * 
   * @param in the vector
   * @return a new vector
   * @see #cdf(double)
   */
  public static Vector cdf(Vector in) {
    return cdf(in, 0, 1);
  }

  /**
   * Element wise quantile function
   * 
   * @param in the vector
   * @param mean the mean
   * @param scale the variance
   * @return a new vector
   * @see #ppf(double, double, double)
   */
  public static Vector ppf(Vector in, double mean, double scale) {
    DoubleVector.Builder builder = new DoubleVector.Builder(0, in.size());
    for (int i = 0; i < in.size(); i++) {
      builder.add(ppf(in.getAsDouble(i), mean, scale));
    }
    return builder.build();
  }

  /**
   * Element wise quantile function with 0 mean and unit variance
   * 
   * @param in the vector
   * @return a new vector
   */
  public static Vector ppf(Vector in) {
    return ppf(in, 0, 1);
  }

  public static DoubleMatrix ppf(DoubleMatrix in, double mean, double scale) {
    return in.map(v -> ppf(v, mean, scale));
  }

  public static DoubleMatrix ppf(DoubleMatrix in) {
    return ppf(in, 0, 1);
  }

  /**
   * <p>
   * Percentage point function (quantile function) for the normal distribution. Inverse of the CDF.
   * </p>
   *
   * <p>
   * Returns the value at which the probability of a random variable will be less than {@code <= p}
   * </p>
   * 
   * <p>
   * Algorithm from <a href="http://home.online.no/~pjacklam/notes/invnorm/">here</a>
   * </p>
   *
   * @param p probability
   * @param loc mean of the normal distribution
   * @param scale the variance of the normal distribution
   * @return the probability of being less than or equal to {@code p}. If {@code p > 1 || p < 0},
   *         returns {@link Double#NaN}
   */
  public static double ppf(double p, double loc, double scale) {
    if (p > 1 || p < 0) {
      return Double.NaN;
    }
    if (p == 1) {
      return Double.POSITIVE_INFINITY;
    }
    if (p == 0) {
      return Double.NEGATIVE_INFINITY;
    }

    if (0 < p && p < p_low) {
      double q = Math.sqrt(-2 * Math.log(p));
      return loc + scale * (((((c1 * q + c2) * q + c3) * q + c4) * q + c5) * q + c6)
          / ((((d1 * q + d2) * q + d3) * q + d4) * q + 1);
    } else if (p_low <= p && p <= p_high) {
      double q = p - 0.5;
      double r = q * q;
      return loc + scale
          * (((((ppf_a1 * r + ppf_a2) * r + ppf_a3) * r + ppf_a4) * r + ppf_a5) * r + ppf_a6) * q
          / (((((b1 * r + b2) * r + b3) * r + b4) * r + b5) * r + 1);
    } else {
      double q = Math.sqrt(-2 * Math.log(1 - p));
      return loc + scale * -(((((c1 * q + c2) * q + c3) * q + c4) * q + c5) * q + c6)
          / ((((d1 * q + d2) * q + d3) * q + d4) * q + 1);
    }
  }

  /**
   * Quantile function for normal distribution with zero mean and unit variance
   *
   * @param p the probability
   * @return the probability of being less than or equal to {@code p}
   */
  public static double ppf(double p) {
    return ppf(p, 0, 1);
  }

  /**
   * The probability density function
   *
   * @param x the value
   * @param loc the mean
   * @param scale the variance
   * @return the
   */
  public static double pdf(double x, double loc, double scale) {
    double o = Math.sqrt(scale);
    double d = x - loc;
    return 1 / (o * Math.sqrt(2 * Math.PI)) * Math.exp(-(d * d) / (2 * scale));
  }

  @Override
  public double next() {
    return random.nextGaussian();
  }
}

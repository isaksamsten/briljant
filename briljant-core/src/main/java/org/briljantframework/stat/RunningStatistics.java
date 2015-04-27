package org.briljantframework.stat;

/**
 * @author Isak Karlsson
 */
public class RunningStatistics {

  private int n = 0;
  private double om, nm, os, ns;
  private double min = Double.NEGATIVE_INFINITY, max = Double.POSITIVE_INFINITY;

  public void add(double x) {
    n += 1;
    if (n == 1) {
      om = x;
      nm = x;
      os = 0;
      min = x;
      max = x;
    } else {
      nm = om + (x - om) / n;
      ns = os + (x - om) * (x - nm);
      om = nm;
      os = ns;

      if (x < min) {
        min = x;
      } else if (x > max) {
        max = x;
      }
    }
  }

  public int size() {
    return n;
  }

  public double getMax() {
    return max;
  }

  public double getMin() {
    return min;
  }

  public double getMean() {
    return n > 0 ? nm : 0;
  }

  public double getVariance() {
    return n > 1 ? ns / n : 0;
  }

  public double getStandardDeviation() {
    return Math.sqrt(getVariance());
  }
}

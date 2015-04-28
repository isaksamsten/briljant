package org.briljantframework.stat;

/**
 * Created by isak on 28/04/15.
 */
public interface DescriptiveStatistics {

  int size();

  double getMax();

  double getMin();

  double getMean();

  double getVariance();

  double getStandardDeviation();
}

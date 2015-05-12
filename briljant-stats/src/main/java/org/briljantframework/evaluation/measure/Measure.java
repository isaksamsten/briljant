package org.briljantframework.evaluation.measure;

import org.briljantframework.evaluation.result.Sample;
import org.briljantframework.vector.DoubleVector;

import java.util.Map;

/**
 * <p>
 * Metrics are produced from evaluators to contain the performance of algorithms.
 * </p>
 *
 * @author Isak Karlsson
 */
public interface Measure extends Comparable<Measure> {

  /**
   * Gets standard deviation.
   *
   * @return the standard deviation for {@code Sample.OUT}
   */
  default double getStandardDeviation() {
    return getStandardDeviation(Sample.OUT);
  }

  /**
   * Gets standard deviation.
   *
   * @param sample the sample
   * @return the standard deviation
   */
  double getStandardDeviation(Sample sample);

  /**
   * Gets the minimum value of a specified run.
   *
   * @return the min
   */
  default double getMin() {
    return getMin(Sample.OUT);
  }

  /**
   * @param sample the sample
   * @return the min
   */
  double getMin(Sample sample);

  /**
   * @return the max (for {@code Sample.OUT})
   */
  default double getMax() {
    return getMax(Sample.OUT);
  }

  /**
   * @param sample the sample
   * @return the max
   */
  double getMax(Sample sample);

  /**
   * @param i the i:th out of sample measurement
   * @return the measurement
   */
  default double get(int i) {
    return get(Sample.OUT, i);
  }

  /**
   * @param sample the sample
   * @param i      the index
   * @return the i:th measurement
   */
  double get(Sample sample, int i);

  /**
   * @return {@code get(Sample.OUT)}
   */
  default DoubleVector get() {
    return get(Sample.OUT);
  }

  /**
   * Get a {@code DoubleVector} of measurements. The i:th index contains the measurement from the
   * i:th run. For example, {@code Vectors.mean(measure.get(IN))}.
   *
   * @param sample the sample
   * @return the measurements
   */
  DoubleVector get(Sample sample);

  /**
   * @return the number of measurements
   */
  int size();

  String getName();

  @Override
  default int compareTo(Measure other) {
    return Double.compare(other.getMean(), getMean());
  }

  double getMean(Sample sample);

  default double getMean() {
    return getMean(Sample.OUT);
  }

  /**
   * Metrics can be produced either in sample (denoted by {@link Sample#IN}) or out of sample
   * (denoted by {@link Sample#OUT})
   * <p>
   * Created by isak on 02/10/14.
   */
  interface Builder<T extends Measure> {

    /**
     * Add a performance metric
     *
     * @param sample      the sample
     * @param measurement the measurement
     */
    public void add(Sample sample, double measurement);

    public void add(Sample sample, Map<Object, Double> values);

    /**
     * Gets performance metric.
     *
     * @return the performance metric
     */
    T build();
  }
}

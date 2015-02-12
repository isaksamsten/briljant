package org.briljantframework.evaluation.result;

import org.briljantframework.classification.Predictor;
import org.briljantframework.matrix.DoubleMatrix;
import org.briljantframework.vector.DoubleVector;
import org.briljantframework.vector.Vector;

/**
 * Metrics are produced from evaluators to contain the performance of algorithms.
 * <p>
 * Created by isak on 02/10/14.
 */
public interface Measure extends Comparable<Measure> {

  /**
   * Gets standard deviation.
   *
   * @return the standard deviation
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
   * Gets min.
   *
   * @param out the out
   * @return the min
   */
  double getMin(Sample out);

  /**
   * Gets max.
   *
   * @return the max
   */
  default double getMax() {
    return getMax(Sample.OUT);
  }

  /**
   * Gets max.
   *
   * @param out the out
   * @return the max
   */
  double getMax(Sample out);

  /**
   * Get double.
   *
   * @param i the i
   * @return the double
   */
  default double get(int i) {
    return get(Sample.OUT, i);
  }

  /**
   * Get double.
   *
   * @param sample the sample
   * @param i the i
   * @return the double
   */
  double get(Sample sample, int i);

  /**
   * Get list.
   *
   * @return the list
   */
  default DoubleVector get() {
    return get(Sample.OUT);
  }

  /**
   * Get list.
   *
   * @param sample the sample
   * @return the list
   */
  DoubleVector get(Sample sample);

  Vector getDomain();

  /**
   * Size int.
   *
   * @return the int
   */
  int size();

  /**
   * Gets name.
   *
   * @return the name
   */
  String getName();

  /**
   * Gets mean.
   *
   * @param sample the sample
   * @return the mean
   */
  double getAverage(Sample sample);

  /**
   * Default order is descending order
   *
   * @param other metric
   * @return comparison
   */
  @Override
  default int compareTo(Measure other) {
    return Double.compare(other.getAverage(), getAverage());
  }

  /**
   * Gets average.
   *
   * @return the average
   */
  default double getAverage() {
    return getAverage(Sample.OUT);
  }


  /**
   * If a metric is calculated in or out of sample
   */
  public enum Sample {

    /**
     * Used to denote metrics calculated using the training sample
     */
    IN,

    /**
     * Used to denote metrics calculated out of the training sample
     */
    OUT
  }

  /**
   * Metrics can be produced either in sample (denoted by {@link Measure.Sample#IN}) or out of
   * sample (denoted by {@link Measure.Sample#OUT})
   * <p>
   * Created by isak on 02/10/14.
   */
  interface Builder {

    /**
     * Add producer.
     * 
     * @param sample the sample
     * @param predictor
     * @param predicted the predictions
     * @param probabilities
     * @param truth the target
     */
    public void compute(Sample sample, Predictor predictor, Vector predicted,
        DoubleMatrix probabilities, Vector truth);

    /**
     * Gets performance metric.
     *
     * @return the performance metric
     */
    Measure build();
  }
}

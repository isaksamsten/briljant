/*
 * The MIT License (MIT)
 * 
 * Copyright (c) 2015 Isak Karlsson
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.briljantframework.evaluation.measure;

import org.briljantframework.data.vector.Vector;
import org.briljantframework.evaluation.result.Sample;

/**
 * A measure is an immutable container of evaluation measures either produced in sample or out of
 * sample.
 * 
 * @author Isak Karlsson
 */
public interface Measure extends Comparable<Measure> {

  /**
   * Get the mean
   *
   * @param sample the sample
   * @return the mean
   */
  double getMean(Sample sample);

  /**
   * Get the out of sample mean
   *
   * @return the out of sample mean
   */
  default double getMean() {
    return getMean(Sample.OUT);
  }

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
   * @param i the index
   * @return the i:th measurement
   */
  double get(Sample sample, int i);

  /**
   * @return {@code get(Sample.OUT)}
   */
  default Vector get() {
    return get(Sample.OUT);
  }

  /**
   * Get a {@code Vector} of measurements. The i:th index contains the measurement from the i:th
   * run. For example, {@code Vectors.mean(measure.get(IN))}.
   *
   * @param sample the sample
   * @return the measurements
   */
  Vector get(Sample sample);

  /**
   * @return the number of measurements
   */
  int size();

  /**
   * Get the name of the current measurement
   *
   * @return the name of the measurement
   */
  String getName();

  @Override
  default int compareTo(Measure other) {
    return Double.compare(other.getMean(), getMean());
  }

  /**
   * Measures can be produced either in sample (denoted by {@link Sample#IN}) or out of sample
   * (denoted by {@link Sample#OUT})
   *
   * @author Isak Karlsson
   */
  interface Builder<T extends Measure> {
    void add(Sample sample, double measurement);

    void add(Sample sample, Vector measurements);

    T build();
  }
}

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

package org.briljantframework.evaluation.result;

import static org.briljantframework.evaluation.result.Measure.Sample;

import org.briljantframework.vector.DoubleVector;

/**
 * Created by Isak Karlsson on 08/10/14.
 */
public interface ClassMeasure {

  /**
   * Get list.
   *
   * @param value the value
   * @return the list
   */
  default DoubleVector get(String value) {
    return get(Sample.OUT, value);
  }

  /**
   * Get list.
   *
   * @param sample the sample
   * @param value the value
   * @return the list
   */
  DoubleVector get(Sample sample, String value);

  /**
   * Gets for value.
   *
   * @param value the value
   * @return the for value
   */
  default double getAverage(String value) {
    return getAverage(Sample.OUT, value);
  }

  /**
   * Gets average.
   *
   * @param sample the sample
   * @param value the value
   * @return the average
   */
  double getAverage(Sample sample, String value);

  /**
   * Gets standard deviation.
   *
   * @param value the value
   * @return the standard deviation
   */
  default double getStandardDeviation(String value) {
    return getStandardDeviation(Sample.OUT, value);
  }

  /**
   * Gets standard deviation.
   *
   * @param sample the sample
   * @param value the value
   * @return the standard deviation
   */
  double getStandardDeviation(Sample sample, String value);

  /**
   * Gets min.
   *
   * @param value the value
   * @return the min
   */
  default double getMin(String value) {
    return getMin(Sample.OUT, value);
  }

  /**
   * Gets min.
   *
   * @param out the out
   * @param value the value
   * @return the min
   */
  double getMin(Sample out, String value);

  /**
   * Gets max.
   *
   * @param value the value
   * @return the max
   */
  default double getMax(String value) {
    return getMax(Sample.OUT, value);
  }

  /**
   * Gets max.
   *
   * @param out the out
   * @param value the value
   * @return the max
   */
  double getMax(Sample out, String value);
}

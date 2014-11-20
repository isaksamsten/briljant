/*
 * ADEB - machine learning pipelines made easy
 * Copyright (C) 2014  Isak Karlsson
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.briljantframework.learning.evaluation.result;

import org.briljantframework.data.values.Value;

import java.util.List;
import java.util.Set;

import static org.briljantframework.learning.evaluation.result.Metric.Sample;

/**
 * Created by Isak Karlsson on 08/10/14.
 */
public interface PerValueMetric {

    /**
     * Get list.
     *
     * @param value the value
     * @return the list
     */
    default List<Double> get(Value value) {
        return get(Sample.OUT, value);
    }

    /**
     * Get list.
     *
     * @param sample the sample
     * @param value  the value
     * @return the list
     */
    List<Double> get(Sample sample, Value value);

    /**
     * Gets for value.
     *
     * @param value the value
     * @return the for value
     */
    default double getAverage(Value value) {
        return getAverage(Sample.OUT, value);
    }

    /**
     * Gets average.
     *
     * @param sample the sample
     * @param value  the value
     * @return the average
     */
    double getAverage(Sample sample, Value value);

    /**
     * Gets standard deviation.
     *
     * @param value the value
     * @return the standard deviation
     */
    default double getStandardDeviation(Value value) {
        return getStandardDeviation(Sample.OUT, value);
    }

    /**
     * Gets standard deviation.
     *
     * @param sample the sample
     * @param value  the value
     * @return the standard deviation
     */
    double getStandardDeviation(Sample sample, Value value);

    /**
     * Gets min.
     *
     * @param value the value
     * @return the min
     */
    default double getMin(Value value) {
        return getMin(Sample.OUT, value);
    }

    /**
     * Gets min.
     *
     * @param out   the out
     * @param value the value
     * @return the min
     */
    double getMin(Sample out, Value value);

    /**
     * Gets max.
     *
     * @param value the value
     * @return the max
     */
    default double getMax(Value value) {
        return getMax(Sample.OUT, value);
    }

    /**
     * Gets max.
     *
     * @param out   the out
     * @param value the value
     * @return the max
     */
    double getMax(Sample out, Value value);

    /**
     * Gets labels.
     *
     * @param sample the sample
     * @return the labels
     */
    Set<Value> getLabels(Sample sample);

    /**
     * Gets labels.
     *
     * @return the labels
     */
    default Set<Value> getLabels() {
        return getLabels(Sample.OUT);
    }
}

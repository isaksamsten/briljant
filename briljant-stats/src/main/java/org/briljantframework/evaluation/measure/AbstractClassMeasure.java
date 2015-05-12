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

package org.briljantframework.evaluation.measure;

import org.briljantframework.evaluation.result.Sample;
import org.briljantframework.vector.DoubleVector;
import org.briljantframework.vector.Vec;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Isak Karlsson
 */
public abstract class AbstractClassMeasure extends AbstractMeasure implements ClassMeasure {

  protected final EnumMap<Sample, Map<Object, DoubleVector>> valueForValue;

  protected AbstractClassMeasure(Builder<? extends Measure> producer) {
    super(producer);
    this.valueForValue = new EnumMap<>(Sample.class);
    for (Map.Entry<Sample, Map<Object, DoubleVector.Builder>> e : producer.sampleMetricValues
        .entrySet()) {
      Map<Object, DoubleVector> values = new HashMap<>();
      for (Map.Entry<Object, DoubleVector.Builder> ve : e.getValue().entrySet()) {
        values.put(ve.getKey(), ve.getValue().build());
      }
      valueForValue.put(e.getKey(), values);
    }
  }

  @Override
  public DoubleVector get(Sample sample, String value) {
    return valueForValue.get(sample).getOrDefault(value, zeroVector);
  }

  @Override
  public double getAverage(Sample sample, String value) {
    return Vec.mean(get(sample, value));
  }

  @Override
  public double getStandardDeviation(Sample sample, String value) {
    double mean = getAverage(sample, value);
    return Vec.std(get(sample, value), mean);
  }

  @Override
  public double getMin(Sample sample, String value) {
    return get(sample, value).stream(Number.class).mapToDouble(Number::doubleValue).min().orElse(0);
  }

  @Override
  public double getMax(Sample sample, String value) {
    return get(sample, value).stream(Number.class).mapToDouble(Number::doubleValue).min().orElse(0);
  }

  protected static abstract class Builder<T extends Measure> extends AbstractMeasure.Builder<T> {

    protected final EnumMap<Sample, Map<Object, DoubleVector.Builder>> sampleMetricValues =
        new EnumMap<>(Sample.class);

    /**
     * Adds values for each value. Callers must ensure that an average value is added using {@link
     * #add(org.briljantframework.evaluation.result.Sample, double)}
     *
     * @param sample the sample
     * @param values the values
     */
    public void add(Sample sample, Map<Object, Double> values) {
      Map<Object, DoubleVector.Builder> all =
          sampleMetricValues.computeIfAbsent(sample, x -> new HashMap<>());
      for (Map.Entry<Object, Double> entry : values.entrySet()) {
        all.computeIfAbsent(entry.getKey(), x -> new DoubleVector.Builder()).add(
            entry.getValue());
      }
      sampleMetricValues.put(sample, all);
    }
  }
}

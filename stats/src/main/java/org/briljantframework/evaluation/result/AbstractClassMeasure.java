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

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import org.briljantframework.classification.Predictor;
import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.matrix.DoubleMatrix;
import org.briljantframework.vector.DoubleVector;
import org.briljantframework.vector.Value;
import org.briljantframework.vector.Vector;
import org.briljantframework.vector.Vectors;

/**
 * @author Isak Karlsson
 */
public abstract class AbstractClassMeasure extends AbstractMeasure implements ClassMeasure {

  protected final EnumMap<Sample, Map<String, DoubleVector>> valueForValue;

  protected AbstractClassMeasure(Builder producer) {
    super(producer);
    this.valueForValue = new EnumMap<>(Sample.class);
    for (Map.Entry<Sample, Map<String, DoubleVector.Builder>> e : producer.sampleMetricValues
        .entrySet()) {
      Map<String, DoubleVector> values = new HashMap<>();
      for (Map.Entry<String, DoubleVector.Builder> ve : e.getValue().entrySet()) {
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
    return Vectors.mean(get(sample, value));
  }

  @Override
  public double getStandardDeviation(Sample sample, String value) {
    double mean = getAverage(sample, value);
    return Vectors.std(get(sample, value), mean);
  }

  @Override
  public double getMin(Sample sample, String value) {
    return get(sample, value).stream().mapToDouble(Value::getAsDouble).min().orElse(0);
  }

  @Override
  public double getMax(Sample sample, String value) {
    return get(sample, value).stream().mapToDouble(Value::getAsDouble).min().orElse(0);
  }

  protected static abstract class Builder extends AbstractMeasure.Builder {
    protected final EnumMap<Sample, Map<String, DoubleVector.Builder>> sampleMetricValues =
        new EnumMap<>(Sample.class);

    protected Builder(Vector domain) {
      super(domain);
    }

    @Override
    public void compute(Sample sample, Predictor predictor, DataFrame dataFrame, Vector predicted,
        DoubleMatrix probabilities, Vector truth) {
      Map<String, DoubleVector.Builder> metricValues =
          sampleMetricValues.computeIfAbsent(sample, x -> new HashMap<>());

      double average = 0.0;
      Vector classes = predictor.getClasses();
      for (int i = 0; i < classes.size(); i++) {
        double metricForValue =
            calculateMetricForLabel(classes.getAsString(i), predicted,
                probabilities.getColumnView(i), truth);
        metricValues.computeIfAbsent(classes.getAsString(i), x -> new DoubleVector.Builder()).add(
            metricForValue);
        average += metricForValue;
      }
      addComputedValue(sample, average / getDomain().size());
    }

    protected abstract double calculateMetricForLabel(String real, Vector predictions,
        DoubleMatrix proba, Vector column);
  }


}

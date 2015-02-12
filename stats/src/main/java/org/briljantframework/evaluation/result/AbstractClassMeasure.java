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

import java.util.*;
import java.util.stream.Collectors;

import org.briljantframework.classification.Label;
import org.briljantframework.vector.Value;
import org.briljantframework.vector.Vector;

/**
 * @author Isak Karlsson
 */
public abstract class AbstractClassMeasure extends AbstractMeasure implements ClassMeasure {

  protected final EnumMap<Sample, Set<String>> labels;
  protected final EnumMap<Sample, List<Map<String, Double>>> valueForValue;

  protected AbstractClassMeasure(Builder producer) {
    super(producer);
    this.labels = producer.sampleLabels;
    this.valueForValue = producer.sampleMetricValues;
  }

  @Override
  public List<Double> get(Sample sample, String value) {
    List<Map<String, Double>> valueForValue = this.valueForValue.get(sample);

    return valueForValue.stream().map(x -> x.getOrDefault(value, 0.0)).collect(Collectors.toList());
  }

  @Override
  public double getAverage(Sample sample, String value) {
    if (!getLabels(sample).contains(value)) {
      throw new IllegalArgumentException(String.format("Average not calculate for value %s", value));
    }
    List<Map<String, Double>> valueForValue = this.valueForValue.get(sample);
    return valueForValue.stream().mapToDouble(x -> x.getOrDefault(value, 0.0)).summaryStatistics()
        .getAverage();
  }

  @Override
  public double getStandardDeviation(Sample sample, String value) {
    double mean = getAverage(sample, value);
    double std = 0.0;
    for (double d : get(sample, value)) {
      double r = d - mean;
      std += r * r;
    }

    return size() > 1 ? Math.sqrt(std / size() - 1) : 0.0;
  }

  @Override
  public double getMin(Sample sample, String value) {
    if (!getLabels(sample).contains(value)) {
      throw new IllegalArgumentException(String.format("Min not calculate for value %s", value));
    }
    List<Map<String, Double>> valueForValue = this.valueForValue.get(sample);
    return valueForValue.stream().mapToDouble(x -> x.getOrDefault(value, 0.0)).summaryStatistics()
        .getMin();
  }

  @Override
  public double getMax(Sample sample, String value) {
    if (!getLabels(sample).contains(value)) {
      throw new IllegalArgumentException(String.format("Max not calculate for value %s", value));
    }
    List<Map<String, Double>> valueForValue = this.valueForValue.get(sample);
    return valueForValue.stream().mapToDouble(x -> x.getOrDefault(value, 0.0)).summaryStatistics()
        .getMax();
  }

  @Override
  public Set<String> getLabels(Sample sample) {
    return labels.get(sample);
  }

  protected static abstract class Builder extends AbstractMeasure.Builder {
    protected final EnumMap<Sample, List<Map<String, Double>>> sampleMetricValues = new EnumMap<>(
        Sample.class);

    /**
     * FIXME: this stores the labels produced for a particular sample. Shouldn't this be the same as
     * FIXME: sampleMetricValues.get(Sample.IN).keySet()?
     */
    protected final EnumMap<Sample, Set<String>> sampleLabels = new EnumMap<>(Sample.class);

    protected Builder(Set<Value> domain) {
      super(domain);
    }

    @Override
    public void compute(Sample sample, List<Label> predicted, Vector truth) {
      Map<String, Double> valueMetrics = new HashMap<>();

      double average = 0.0;
      for (Value value : getDomain()) {
        double metricForValue = calculateMetricForLabel(value.getAsString(), predicted, truth);
        valueMetrics.put(value.getAsString(), metricForValue);
        average += metricForValue;
      }

      List<Map<String, Double>> metricValues = sampleMetricValues.get(sample);
      if (metricValues == null) {
        metricValues = new ArrayList<>();
        sampleMetricValues.put(sample, metricValues);
      }
      metricValues.add(valueMetrics);

      this.sampleLabels.put(sample,
          getDomain().stream().map(Value::getAsString).collect(Collectors.toSet()));

      addComputedValue(sample, average / getDomain().size());
    }

    /**
     * Calculate a metric for the value {@code value}.
     *
     * @param value the value
     * @param predictions the predictions
     * @param column the target
     * @return the double
     */
    protected abstract double calculateMetricForLabel(String value, List<Label> predictions,
        Vector column);
  }


}

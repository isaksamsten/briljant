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

import org.briljantframework.classification.Predictions;
import org.briljantframework.vector.Vector;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

/**
 * Created by Isak Karlsson on 08/10/14.
 */
public abstract class AbstracPerValueMetric extends AbstractMetric implements PerValueMetric {

  /**
   * The Labels.
   */
  protected final EnumMap<Sample, Set<String>> labels;

  /**
   * The Value for value.
   */
  protected final EnumMap<Sample, List<Map<String, Double>>> valueForValue;


  /**
   * Instantiates a new Abstract metric.
   *
   * @param producer the producer
   */

  protected AbstracPerValueMetric(AbstracPerValueMetric.Producer producer) {
    super(producer);
    this.labels = producer.sampleLabels;
    this.valueForValue = producer.sampleMetricValues;
  }

  public JFreeChart getPerValueChart() {
    JFreeChart chart = new JFreeChart(getName(), getPerValuePlot());
    ChartFactory.getChartTheme().apply(chart);
    return chart;
  }

  public Plot getPerValuePlot() {
    DefaultCategoryDataset dataset = new DefaultCategoryDataset();
    for (String label : getLabels(Sample.OUT)) {
      for (int i = 0; i < size(); i++) {
        List<Double> outValues = get(Sample.OUT, label);
        dataset.addValue(outValues.get(i), label, String.valueOf(i));
      }
      dataset.addValue(getAverage(Sample.OUT, label), label, "Average");
    }

    NumberAxis numberAxis = new NumberAxis(getName());
    BarRenderer barRenderer = new BarRenderer();
    return new CategoryPlot(dataset, new CategoryAxis("Result"), numberAxis, barRenderer);
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
    if (!getLabels().contains(value)) {
      throw new IllegalArgumentException(String.format("Min not calculate for value %s", value));
    }
    List<Map<String, Double>> valueForValue = this.valueForValue.get(sample);
    return valueForValue.stream().mapToDouble(x -> x.getOrDefault(value, 0.0)).summaryStatistics()
        .getMin();
  }

  @Override
  public double getMax(Sample sample, String value) {
    if (!getLabels().contains(value)) {
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

  /**
   * The type Producer.
   */
  protected static abstract class Producer extends AbstractMetric.Producer {

    /**
     * The
     */
    protected final EnumMap<Sample, List<Map<String, Double>>> sampleMetricValues = new EnumMap<>(
        Sample.class);

    /**
     * FIXME: this stores the labels produced for a particular sample. Shouldn't this be the same as
     * FIXME: sampleMetricValues.get(Sample.IN).keySet()?
     */
    protected final EnumMap<Sample, Set<String>> sampleLabels = new EnumMap<>(Sample.class);

    @Override
    public Metric.Producer add(Sample sample, Predictions predictions, Vector targets) {
      // checkArgument(targets.getType().getScale() != Type.Scale.NUMERICAL,
      // "Can't calculate per-value metrics for numerical targets");

      Map<String, Double> valueMetrics = new HashMap<>();
      Set<String> labels = new HashSet<>();

      // FIXME! targets.getType().getDomain();
      for (int i = 0; i < targets.rows(); i++) {
        labels.add(targets.getAsString(i));
      }

      double average = 0.0;
      for (String value : labels) {
        double metricForValue = calculateMetricForValue(value, predictions, targets);
        valueMetrics.put(value, metricForValue);
        average += metricForValue;
      }

      List<Map<String, Double>> metricValues = sampleMetricValues.get(sample);
      if (metricValues == null) {
        metricValues = new ArrayList<>();
        sampleMetricValues.put(sample, metricValues);
      }
      metricValues.add(valueMetrics);

      Set<String> sampleLabels = this.sampleLabels.get(sample);
      if (sampleLabels == null) {
        sampleLabels = new HashSet<>();
        this.sampleLabels.put(sample, sampleLabels);
      }
      sampleLabels.addAll(labels);

      add(sample, average / labels.size());
      return this;
    }

    /**
     * Calculate a metric for the value {@code value}.
     *
     * @param value the value
     * @param predictions the predictions
     * @param column the target
     * @return the double
     */
    protected abstract double calculateMetricForValue(String value, Predictions predictions,
        Vector column);
  }


}

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

import java.text.DecimalFormat;
import java.util.*;

import org.briljantframework.Utils;
import org.briljantframework.chart.Chartable;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.CombinedDomainCategoryPlot;
import org.jfree.chart.plot.Plot;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableTable;

/**
 * Created by Isak Karlsson on 02/10/14.
 */
public class Result implements Chartable {

  private static final DecimalFormat FORMATTER = new DecimalFormat("##0.00");
  private final Map<Class<?>, Measure> metrics;

  private final List<ConfusionMatrix> confusionMatrices;

  private Result(Map<Class<?>, Measure> metrics, List<ConfusionMatrix> confusionMatrices) {
    this.confusionMatrices = confusionMatrices;
    this.metrics = metrics;
  }

  /**
   * Create result.
   *
   * @param measures the metrics
   * @param confusionMatrices the confusion matrices
   * @return the result
   */
  public static Result create(List<Measure> measures, List<ConfusionMatrix> confusionMatrices) {
    Preconditions.checkArgument(measures.size() > 0 && confusionMatrices.size() > 0);
    Map<Class<?>, Measure> metricMap = new HashMap<>();

    int length = 0;
    if (measures.size() > 0) {
      length = measures.get(0).size();
      if (confusionMatrices.size() != length) {
        throw new IllegalArgumentException(
            "ConfusionMatrix don't have the same number of values as the metrics");
      }
    }

    for (Measure measure : measures) {
      if (measure.size() != length) {
        throw new IllegalArgumentException(String.format("Invalid number of metrics for %s",
            measure.getName()));
      }
      metricMap.put(measure.getClass(), measure);
    }
    return new Result(metricMap, confusionMatrices);
  }

  /**
   * Gets average error.
   *
   * @return the average error
   */

  public double getAverageError() {
    double error = 0;
    for (ConfusionMatrix matrix : confusionMatrices) {
      error += matrix.getError();
    }

    return error / confusionMatrices.size();
  }

  /**
   * Gets average accuracy.
   *
   * @return the average accuracy
   */
  public double getAverageAccuracy() {
    return 1 - getAverageError();
  }

  /**
   * Gets confusion matrix.
   *
   * @return the confusion matrix
   */
  public List<ConfusionMatrix> getConfusionMatrices() {
    return Collections.unmodifiableList(confusionMatrices);
  }

  /**
   * Gets confusion matrix.
   *
   * @return the confusion matrix
   */
  public ConfusionMatrix getAverageConfusionMatrix() {
    Map<String, Map<String, Double>> matrix = new HashMap<>();
    Set<String> valueSet = new HashSet<>();

    double sum = 0.0;
    for (ConfusionMatrix cm : confusionMatrices) {
      Set<String> labels = cm.getLabels();
      for (String predicted : labels) {
        for (String actual : labels) {
          Map<String, Double> actuals = matrix.get(predicted);
          if (actuals == null) {
            actuals = new HashMap<>();
            matrix.put(predicted, actuals);
          }
          double count = cm.get(predicted, actual);
          actuals.compute(actual, (key, value) -> value == null ? count : count + value);
          sum += count;
        }
      }
      valueSet.addAll(labels);
    }

    return new ConfusionMatrix(matrix, valueSet, sum);
  }

  /**
   * Get metric.
   *
   * @param <T> the type parameter
   * @param key the key
   * @return the metric
   */
  public <T extends Measure> T get(Class<T> key) {
    Measure measure = metrics.get(key);
    if (measure != null) {
      return key.cast(measure);
    } else {
      // TODO(isak) - is it reasonable to throw an ex
      throw new NoSuchElementException(String.format("%s can't be found", key.getSimpleName()));
    }

  }

  /**
   * Gets average.
   *
   * @param key the key
   * @return the average
   */
  public double getAverage(Class<? extends Measure> key) {
    return getAverage(key, Measure.Sample.OUT);
  }

  /**
   * Gets average.
   *
   * @param key the key
   * @param sample the sample
   * @return the average
   */
  public double getAverage(Class<? extends Measure> key, Measure.Sample sample) {
    return get(key).getAverage(sample);
  }

  /**
   * Gets standard deviation.
   *
   * @param key the key
   * @return the standard deviation
   */
  public double getStandardDeviation(Class<? extends Measure> key) {
    return getStandardDeviation(key, Measure.Sample.OUT);
  }

  /**
   * Gets standard deviation.
   *
   * @param key the key
   * @param sample the sample
   * @return the standard deviation
   */
  public double getStandardDeviation(Class<? extends Measure> key, Measure.Sample sample) {
    return get(key).getStandardDeviation(sample);
  }

  /**
   * Gets min.
   *
   * @param key the key
   * @return the min
   */
  public double getMin(Class<? extends Measure> key) {
    return getMin(key, Measure.Sample.OUT);
  }

  /**
   * Gets min.
   *
   * @param key the key
   * @param sample the sample
   * @return the min
   */
  public double getMin(Class<? extends Measure> key, Measure.Sample sample) {
    return get(key).getMin(sample);
  }

  /**
   * Gets max.
   *
   * @param key the key
   * @return the max
   */
  public double getMax(Class<? extends Measure> key) {
    return getMax(key, Measure.Sample.OUT);
  }

  /**
   * Gets max.
   *
   * @param key the key
   * @param sample the sample
   * @return the max
   */
  public double getMax(Class<? extends Measure> key, Measure.Sample sample) {
    return get(key).getMax(sample);
  }

  /**
   * Get double.
   *
   * @param key the key
   * @param index the index
   * @return the double
   */
  public double get(Class<? extends Measure> key, int index) {
    return get(key).get(index);
  }

  /**
   * Get double.
   *
   * @param key the key
   * @param sample the sample
   * @param index the index
   * @return the double
   */
  public double get(Class<? extends Measure> key, Measure.Sample sample, int index) {
    return get(key).get(sample, index);
  }

  /**
   * Gets performance metrics.
   *
   * @return the performance metrics
   */
  public Collection<Measure> getMetrics() {
    return Collections.unmodifiableCollection(metrics.values());
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("Average Confusion Matrix\n").append(getAverageConfusionMatrix()).append("\n\n")
        .append("Metrics\n");

    ImmutableTable.Builder<String, String, Object> table = ImmutableTable.builder();
    for (Measure measure : getMetrics()) {
      for (int i = 0; i < measure.size(); i++) {
        table.put(i + "", measure.getName(), String.format("%.4f", measure.get(i)));
      }
      table.put("Average", measure.getName(), String.format("%.4f", measure.getAverage()));
      table.put("Standard Deviation", measure.getName(),
          String.format("%.4f", measure.getStandardDeviation()));
    }
    Utils.prettyPrintTable(builder, table.build(), 0, 3, true, true);
    return builder.toString();
  }

  @Override
  public JFreeChart getChart() {
    return Chartable.create("Combined Result Metrics", getPlot());
  }

  @Override
  public Plot getPlot() {
    CombinedDomainCategoryPlot cdcp = new CombinedDomainCategoryPlot();
    for (Measure measure : metrics.values()) {
      Plot plot = measure.getPlot();
      if (plot != null && plot instanceof CategoryPlot) {
        NumberAxis axis = (NumberAxis) ((CategoryPlot) plot).getRangeAxis();
        axis.setNumberFormatOverride(FORMATTER);
        cdcp.add((CategoryPlot) plot);
      }
    }
    cdcp.getDomainAxis().setCategoryLabelPositions(CategoryLabelPositions.DOWN_90);
    return cdcp;
  }
}

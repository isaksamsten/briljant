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

package org.briljantframework.classification.tune;

import java.util.*;
import java.util.stream.Collectors;

import org.briljantframework.Utils;
import org.briljantframework.chart.Chartable;
import org.briljantframework.evaluation.ClassificationEvaluator;
import org.briljantframework.evaluation.result.ConfusionMatrix;
import org.briljantframework.evaluation.result.ErrorRate;
import org.briljantframework.evaluation.result.Measure;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableTable;

/**
 * The type Configurations.
 */
public class Configurations implements Iterable<Configuration>, Chartable {
  private final List<Configuration> configurations;
  private final ClassificationEvaluator evaluator;

  /**
   * Instantiates a new Configurations.
   *
   * @param configurations the configurations
   * @param evaluator the evaluator
   */
  protected Configurations(List<Configuration> configurations, ClassificationEvaluator evaluator) {
    this.configurations = configurations;
    this.evaluator = evaluator;
  }

  /**
   * Create configurations.
   *
   * @param configurations the configurations
   * @param evaluator the evaluator
   * @return the configurations
   */
  public static Configurations create(List<Configuration> configurations,
      ClassificationEvaluator evaluator) {
    Preconditions.checkArgument(configurations.size() > 0);
    return new Configurations(configurations, evaluator);
  }

  /**
   * Size int.
   *
   * @return the int
   */
  public int size() {
    return configurations.size();
  }

  /**
   * Is empty.
   *
   * @return the boolean
   */
  public boolean isEmpty() {
    return configurations.isEmpty();
  }

  /**
   * Iterator iterator.
   *
   * @return the iterator
   */
  public Iterator<Configuration> iterator() {
    return configurations.iterator();
  }

  /**
   * Best configuration.
   *
   * @return the configuration
   */
  public Configuration best() {
    return get(0);
  }

  /**
   * Sort void.
   *
   * @param cmp the cmp
   */
  public void sort(Comparator<Configuration> cmp) {
    Collections.sort(configurations, cmp);
  }

  /**
   * Sort void.
   */
  public void sort() {
    Collections.sort(configurations);
  }

  /**
   * Get configuration.
   *
   * @param index the index
   * @return the configuration
   */
  public Configuration get(int index) {
    return configurations.get(index);
  }

  /**
   * Gets parameters.
   *
   * @return the parameters
   */
  public Set<String> getParameters() {
    Set<String> set = new HashSet<>();
    for (Configuration c : configurations) {
      set.addAll(c.keys());
    }
    return set;
  }

  @Override
  public String toString() {
    StringBuilder out = new StringBuilder(configurations.get(0).getClassifier().toString());
    out.append("\n\n").append("Resampling: ").append(evaluator).append("\n\n")
        .append("Results across tuning parameters:\n\n");

    ImmutableTable.Builder<Integer, String, Object> table = ImmutableTable.builder();
    int index = 0;
    for (Configuration configuration : configurations) {
      for (Map.Entry<String, Object> params : configuration.entries()) {
        if (params.getValue() instanceof Double) {
          table.put(index, params.getKey(), String.format("%.4f", (double) params.getValue()));
        } else {
          table.put(index, params.getKey(), params.getValue());
        }
      }
      for (Measure measure : configuration.getResult().getMetrics()) {
        table.put(index, measure.getName(), String.format("%.4f", measure.getAverage()));
      }
      ConfusionMatrix m = configuration.getAverageConfusionMatrix();
      table.put(index, "Precision", String.format("%.4f", m.getAveragePrecision()));
      table.put(index, "Recall", String.format("%.4f", m.getAverageRecall()));
      table.put(index, "F-Measure", String.format("%.4f", m.getAverageFMeasure(2)));
      index += 1;
    }
    out.append(Utils.prettyPrintTable(table.build(), 3, 2, false, true));
    return out.toString();
  }

  /**
   * Gets plot for parameter.
   *
   * @param key the key
   * @return the plot for parameter
   */
  public Plot getPlotForParameter(String key, Class<? extends Measure> metricKey) {
    Map<Number, Double> map = new HashMap<>();
    for (Configuration configuration : configurations) {
      Measure measure = configuration.getMetric(metricKey);
      double average = measure.getAverage(Measure.Sample.OUT);
      Number param = (Number) configuration.get(key);
      map.compute(param, (k, v) -> v == null ? average : v + average);
    }

    XYSeriesCollection collection = new XYSeriesCollection();
    XYSeries series = new XYSeries(configurations.get(0).getMetric(metricKey).getName(), true);

    int noParams = getParameters().size();
    for (Map.Entry<Number, Double> entry : map.entrySet()) {
      series.add(entry.getKey(), entry.getValue() / noParams);
    }
    collection.addSeries(series);


    NumberAxis xAxis = new NumberAxis(key + " value");
    xAxis.setAutoRangeIncludesZero(false);

    return new XYPlot(collection, xAxis, new NumberAxis(configurations.get(0).getMetric(metricKey)
        .getName()), new XYLineAndShapeRenderer());
  }

  public JFreeChart getChartForParameter(String key, Class<? extends Measure> metric) {
    JFreeChart chart = new JFreeChart("Configurations", getPlotForParameter(key, metric));
    ChartFactory.getChartTheme().apply(chart);
    return chart;
  }

  @Override
  public JFreeChart getChart() {
    JFreeChart chart = new JFreeChart("Configurations", getPlot());
    ChartFactory.getChartTheme().apply(chart);
    return chart;
  }

  @Override
  public Plot getPlot() {
    DefaultCategoryDataset dataset = new DefaultCategoryDataset();
    for (Configuration configuration : configurations) {
      dataset.addValue(configuration.getAverage(ErrorRate.class), "Error", configuration.entries()
          .stream().map(kv -> kv.getKey() + ":" + kv.getValue()).collect(Collectors.joining(","))

      );
    }

    NumberAxis numberAxis = new NumberAxis("Error");
    CategoryAxis categoryAxis = new CategoryAxis();
    categoryAxis.setCategoryLabelPositions(CategoryLabelPositions.DOWN_45);
    BarRenderer barRenderer = new BarRenderer();
    barRenderer.setSeriesToolTipGenerator(0,
        (dataset1, row, column) -> (String) dataset1.getColumnKey(column));

    return new CategoryPlot(dataset, categoryAxis, numberAxis, barRenderer);
  }

}

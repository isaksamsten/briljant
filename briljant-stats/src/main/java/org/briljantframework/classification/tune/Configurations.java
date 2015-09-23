/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Isak Karlsson
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.briljantframework.classification.tune;

import org.briljantframework.Check;
import org.briljantframework.evaluation.Validator;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @author Isak Karlsson
 */
public class Configurations implements Iterable<Configuration> {

  private final List<Configuration> configurations;
  private final Validator evaluator;

  protected Configurations(List<Configuration> configurations, Validator evaluator) {
    this.configurations = configurations;
    this.evaluator = evaluator;
  }

  public static Configurations create(List<Configuration> configurations,
                                      Validator evaluator) {
    Check.argument(configurations.size() > 0);
    return new Configurations(configurations, evaluator);
  }

  public int size() {
    return configurations.size();
  }

  public boolean isEmpty() {
    return configurations.isEmpty();
  }

  public Iterator<Configuration> iterator() {
    return configurations.iterator();
  }

  public Configuration best() {
    return get(0);
  }

  public void sort(Comparator<Configuration> cmp) {
    Collections.sort(configurations, cmp);
  }

  public void sort() {
    Collections.sort(configurations);
  }

  public Configuration get(int index) {
    return configurations.get(index);
  }

  public Set<String> getParameters() {
    Set<String> set = new HashSet<>();
    for (Configuration c : configurations) {
      set.addAll(c.getParameters());
    }
    return set;
  }

  @Override
  public String toString() {
    StringBuilder out = new StringBuilder(configurations.get(0).getClassifier().toString());
    out.append("\n\n").append("Resampling: ").append(evaluator).append("\n\n")
        .append("Results across tuning parameters:\n\n");

//    ImmutableTable.Builder<Integer, String, Object> table = ImmutableTable.builder();
//    int index = 0;
//    for (Configuration configuration : configurations) {
//      for (Map.Entry<String, Object> params : configuration.entries()) {
//        if (params.getValue() instanceof Double) {
//          table.put(index, params.getKey(), String.format("%.4f", (double) params.getValue()));
//        } else {
//          table.put(index, params.getKey(), params.getValue());
//        }
//      }
//      for (Measure measure : configuration.getResult().getMeasures()) {
//        table.put(index, measure.getName(), String.format("%.4f", measure.getMean()));
//      }
//      ConfusionMatrix m = configuration.getAverageConfusionMatrix();
//      table.put(index, "Precision", String.format("%.4f", m.getAveragePrecision()));
//      table.put(index, "Recall", String.format("%.4f", m.getAverageRecall()));
//      table.put(index, "F-Measure", String.format("%.4f", m.getAverageFMeasure(2)));
//      index += 1;
//    }
//    out.append(Utils.prettyPrintTable(table.build(), 3, 2, false, true));
    return out.toString();
  }

  // /**
  // * Gets plot for parameter.
  // *
  // * @param key the key
  // * @return the plot for parameter
  // */
  // public Plot getPlotForParameter(String key, Class<? extends Measure> metricKey) {
  // Map<Number, Double> map = new HashMap<>();
  // for (Configuration configuration : configurations) {
  // Measure measure = configuration.getMeasure(metricKey);
  // double average = measure.getAverage(Measure.Sample.OUT);
  // Number param = (Number) configuration.get(key);
  // map.compute(param, (k, v) -> v == null ? average : v + average);
  // }
  //
  // XYSeriesCollection collection = new XYSeriesCollection();
  // XYSeries series = new XYSeries(configurations.get(0).getMeasure(metricKey).getName(), true);
  //
  // int noParams = getParameters().size();
  // for (Map.Entry<Number, Double> entry : map.entrySet()) {
  // series.add(entry.getKey(), entry.getValue() / noParams);
  // }
  // collection.addSeries(series);
  //
  //
  // NumberAxis xAxis = new NumberAxis(key + " value");
  // xAxis.setAutoRangeIncludesZero(false);
  //
  // return new XYPlot(collection, xAxis, new NumberAxis(configurations.get(0).getMeasure(metricKey)
  // .getName()), new XYLineAndShapeRenderer());
  // }
  //
  // public JFreeChart getChartForParameter(String key, Class<? extends Measure> metric) {
  // JFreeChart chart = new JFreeChart("Configurations", getPlotForParameter(key, metric));
  // ChartFactory.getChartTheme().apply(chart);
  // return chart;
  // }
  //
  // @Override
  // public JFreeChart getChart() {
  // JFreeChart chart = new JFreeChart("Configurations", getPlot());
  // ChartFactory.getChartTheme().apply(chart);
  // return chart;
  // }
  //
  // @Override
  // public Plot getPlot() {
  // DefaultCategoryDataset dataset = new DefaultCategoryDataset();
  // for (Configuration configuration : configurations) {
  // dataset.addValue(configuration.getAverage(ErrorRate.class), "Error", configuration.entries()
  // .stream().map(kv -> kv.getKey() + ":" + kv.getValue()).collect(Collectors.joining(","))
  //
  // );
  // }
  //
  // NumberAxis numberAxis = new NumberAxis("Error");
  // CategoryAxis categoryAxis = new CategoryAxis();
  // categoryAxis.setCategoryLabelPositions(CategoryLabelPositions.DOWN_45);
  // BarRenderer barRenderer = new BarRenderer();
  // barRenderer.setSeriesToolTipGenerator(0,
  // (dataset1, row, column) -> (String) dataset1.getColumnKey(column));
  //
  // return new CategoryPlot(dataset, categoryAxis, numberAxis, barRenderer);
  // }

}

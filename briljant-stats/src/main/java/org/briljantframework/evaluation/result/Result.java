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

package org.briljantframework.evaluation.result;

import org.briljantframework.Check;
import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.dataframe.HashIndex;
import org.briljantframework.dataframe.Index;
import org.briljantframework.dataframe.MixedDataFrame;
import org.briljantframework.evaluation.measure.Measure;
import org.briljantframework.function.Aggregates;
import org.briljantframework.vector.DoubleVector;
import org.briljantframework.vector.IntVector;
import org.briljantframework.vector.VectorType;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * @author Isak Karlsson
 */
public class Result {

  private final Map<Class<?>, Measure> metrics;

  private final List<ConfusionMatrix> confusionMatrices;

  private Result(Map<Class<?>, Measure> metrics, List<ConfusionMatrix> confusionMatrices) {
    this.confusionMatrices = confusionMatrices;
    this.metrics = metrics;
  }

  public static Result create(List<Measure> measures, List<ConfusionMatrix> confusionMatrices) {
    Check.argument(measures.size() > 0 && confusionMatrices.size() > 0);
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
    Map<Object, Map<Object, Double>> matrix = new HashMap<>();
    Set<Object> valueSet = new HashSet<>();

    double sum = 0.0;
    for (ConfusionMatrix cm : confusionMatrices) {
      Set<Object> labels = cm.getLabels();
      for (Object predicted : labels) {
        for (Object actual : labels) {
          Map<Object, Double> actuals = matrix.get(predicted);
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
    return getAverage(key, Sample.OUT);
  }

  /**
   * Gets average.
   *
   * @param key    the key
   * @param sample the sample
   * @return the average
   */
  public double getAverage(Class<? extends Measure> key, Sample sample) {
    return get(key).getMean(sample);
  }

  /**
   * Gets standard deviation.
   *
   * @param key the key
   * @return the standard deviation
   */
  public double getStandardDeviation(Class<? extends Measure> key) {
    return getStandardDeviation(key, Sample.OUT);
  }

  /**
   * Gets standard deviation.
   *
   * @param key    the key
   * @param sample the sample
   * @return the standard deviation
   */
  public double getStandardDeviation(Class<? extends Measure> key, Sample sample) {
    return get(key).getStandardDeviation(sample);
  }

  /**
   * Gets min.
   *
   * @param key the key
   * @return the min
   */
  public double getMin(Class<? extends Measure> key) {
    return getMin(key, Sample.OUT);
  }

  /**
   * Gets min.
   *
   * @param key    the key
   * @param sample the sample
   * @return the min
   */
  public double getMin(Class<? extends Measure> key, Sample sample) {
    return get(key).getMin(sample);
  }

  /**
   * Gets max.
   *
   * @param key the key
   * @return the max
   */
  public double getMax(Class<? extends Measure> key) {
    return getMax(key, Sample.OUT);
  }

  /**
   * Gets max.
   *
   * @param key    the key
   * @param sample the sample
   * @return the max
   */
  public double getMax(Class<? extends Measure> key, Sample sample) {
    return get(key).getMax(sample);
  }

  /**
   * Get double.
   *
   * @param key   the key
   * @param index the index
   * @return the double
   */
  public double get(Class<? extends Measure> key, int index) {
    return get(key).get(index);
  }

  /**
   * Get double.
   *
   * @param key    the key
   * @param sample the sample
   * @param index  the index
   * @return the double
   */
  public double get(Class<? extends Measure> key, Sample sample, int index) {
    return get(key).get(sample, index);
  }

  /**
   * Gets performance metrics.
   *
   * @return the performance metrics
   */
  public Collection<Measure> getMeasures() {
    return Collections.unmodifiableCollection(metrics.values());
  }

  public DataFrame toDataFrame() {
    DataFrame.Builder df = new MixedDataFrame.Builder();
    Index.Builder index = new HashIndex.Builder();
    index.add("Fold");
    index.add("Sample");
    Iterator<Measure> it = getMeasures().iterator();

    if (it.hasNext()) {
      Measure measure = it.next();
      df.addColumn(IntVector.range(measure.size()).collect(Aggregates.repeat(2)));
      df.addColumnBuilder(VectorType.from(Sample.class));
      for (int i = 0; i < measure.size() * 2; i++) {
        if (i < measure.size()) {
          df.set(i, 1, Sample.OUT);
        } else {
          df.set(i, 1, Sample.IN);
        }
      }
      index.add(measure.getName());
      df.addColumn(new DoubleVector.Builder()
                       .addAll(measure.get(Sample.OUT))
                       .addAll(measure.get(Sample.IN)));
      while (it.hasNext()) {
        measure = it.next();
        index.add(measure.getName());
        DoubleVector.Builder bf = new DoubleVector.Builder();
        bf.addAll(measure.get(Sample.OUT));
        bf.addAll(measure.get(Sample.IN));
        df.addColumn(bf);
      }
      DataFrame bdf = df.build();
      bdf.setColumnIndex(index.build());
      return bdf;
    } else {
      return df.build();
    }
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("Average Confusion Matrix\n").append(getAverageConfusionMatrix()).append("\n\n")
        .append("Metrics\n");

//    ImmutableTable.Builder<String, String, Object> table = ImmutableTable.builder();
//    getMeasures()
//        .stream()
//        .sorted(Comparator.comparing(Measure::getName))
//        .forEach(
//            measure -> {
//              for (int i = 0; i < measure.size(); i++) {
//                table.put(i + "", measure.getName(), String.format("%.4f", measure.get(i)));
//              }
//              table.put("Average", measure.getName(), String.format("%.4f", measure.getMean()));
//              table.put("Standard Deviation", measure.getName(),
//                        String.format("%.4f", measure.getStandardDeviation()));
//            });
//    Utils.prettyPrintTable(builder, table.build(), 0, 3, true, true);
    return builder.toString();
  }
  // @Override
  // public JFreeChart getChart() {
  // return Chartable.create("Combined Result Metrics", getPlot());
  // }
  //
  // @Override
  // public Plot getPlot() {
  // CombinedDomainCategoryPlot cdcp = new CombinedDomainCategoryPlot();
  // for (Measure measure : metrics.values()) {
  // Plot plot = measure.getPlot();
  // if (plot != null && plot instanceof CategoryPlot) {
  // NumberAxis axis = (NumberAxis) ((CategoryPlot) plot).getRangeAxis();
  // axis.setNumberFormatOverride(FORMATTER);
  // cdcp.add((CategoryPlot) plot);
  // }
  // }
  // cdcp.getDomainAxis().setCategoryLabelPositions(CategoryLabelPositions.DOWN_90);
  // return cdcp;
  // }
}

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
import org.briljantframework.data.vector.Vector;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * In the field of machine learning, a confusion matrix, also known as a contingency table or an
 * error matrix, is a specific table layout that allows visualization of the performance of an
 * algorithm, typically a supervised learning one. Each column of the matrix represents the
 * instances in a predicted class, while each row represents the instances in an actual class. The
 * name stems from the fact that it makes it easy to see if the system is confusing two classes
 * (i.e. commonly mislabeling one as another).
 * <p>
 * Example:
 *
 * <pre>
 *           acc       unacc       vgood       good
 *   acc     344.0     28.0        3.0         15.0
 *   unacc   29.0      1179.0      0.0         0.0
 *   vgood   5.0       0.0         61.0        9.0
 *   good    6.0       3.0         1.0         45.0
 * </pre>
 *
 * Created by isak on 02/10/14.
 */
public class ConfusionMatrix {

  private final Map<Object, Map<Object, Double>> matrix;
  private final Set<Object> labels;
  private final double sum;

  public ConfusionMatrix(Map<Object, Map<Object, Double>> matrix, Set<Object> labels, double sum) {
    this.matrix = Objects.requireNonNull(matrix, "Matrix cannot be null");
    this.labels = Collections.unmodifiableSet(
        Objects.requireNonNull(labels, "Labels cannot be null"));

    this.sum = sum;
  }

  public static ConfusionMatrix compute(Vector predictions, Vector truth, Vector domain) {
    Check.argument(predictions.size() == truth.size(), "The vector sizes don't match %s != %s.",
                   predictions.size(), truth.size());

    Map<Object, Map<Object, Double>> matrix = new HashMap<>();
    Set<Object> labels = new HashSet<>();
    double sum = 0;
    for (int i = 0; i < predictions.size(); i++) {
      Object predicted = predictions.loc().get(Object.class, i);
      Object actual = truth.loc().get(Object.class, i);

      Map<Object, Double> actuals = matrix.get(predicted);
      if (actuals == null) {
        actuals = new HashMap<>();
        matrix.put(predicted, actuals);
      }
      actuals.compute(actual, (key, value) -> value == null ? 1 : value + 1);

      labels.add(predicted);
      labels.add(actual);
      sum++;
    }
    return new ConfusionMatrix(matrix, labels, sum);
  }

  public double getAverageRecall() {
    return labels.stream().mapToDouble(this::getRecall).average().orElse(0);
  }

  public double getAveragePrecision() {
    return labels.stream().mapToDouble(this::getPrecision).average().orElse(0);
  }

  public double getAverageFMeasure(double beta) {
    return labels.stream().mapToDouble(value -> getFMeasure(value, beta)).average().orElse(0);
  }

  public double getFMeasure(Object target, double beta) {
    double precision = getPrecision(target);
    double recall = getRecall(target);
    double beta2 = beta * beta;
    if (precision > 0 && recall > 0) {
      return (1 + beta2) * ((precision * recall) / ((beta2 * precision) + recall));
    } else {
      return 0;
    }
  }

  public double getPrecision(Object target) {
    double tp = get(target, target);
    if (tp == 0) {
      return 0;
    } else {
      double conditional = 0.0;
      for (Object actual : getLabels()) {
        conditional += get(target, actual);
      }
      return conditional > 0 ? tp / conditional : 0;
    }
  }

  public double getRecall(Object target) {
    double tp = get(target, target);
    if (tp == 0) {
      return 0;
    } else {
      double conditional = 0.0;
      for (Object actual : getLabels()) {
        conditional += get(actual, target);
      }
      return conditional > 0 ? tp / conditional : 0;
    }
  }

  public double get(Object predicted, Object actual) {
    Map<Object, Double> values = matrix.get(predicted);
    if (values == null) {
      return 0;
    } else {
      return values.getOrDefault(actual, 0.0);
    }
  }

  public Set<Object> getLabels() {
    return labels;
  }

  public double getAccuracy() {
    double diagonal = 0.0;
    for (Object value : labels) {
      diagonal += get(value, value);
    }
    return diagonal / sum;
  }

  public double getError() {
    return 1 - getAccuracy();
  }

  public double getActual(String actual) {
    double sum = 0;
    for (Object predicted : getLabels()) {
      sum += get(predicted, actual);
    }
    return sum;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    int longest = labels.stream()
        .map(Object::toString)
        .mapToInt(String::length)
        .summaryStatistics()
        .getMax();
    if (longest < 3) {
      longest = 3;
    }

    int longestValue = 0;
    for (Object p : labels) {
      for (Object n : labels) {
        int len = Double.toString(get(p, n)).length();
        if (len > longestValue) {
          longestValue = len;
        }
      }
    }

    builder.append(repeat(" ", longest + 3));
    for (Object value : labels) {
      builder.append(value);
      builder.append(repeat(" ", longestValue + 1));
    }

    builder.append("\n");
    for (Object predicted : labels) {
      builder.append(padEnd(predicted.toString(), longest + 3, ' '));

      for (Object actual : labels) {
        String valueStr = Double.toString(get(predicted, actual));
        builder.append(valueStr);
        builder.append(
            repeat(" ", actual.toString().length() + 1 + longestValue - valueStr.length()));
      }
      builder.append("\n");
    }
    builder.append(" ---- (rows: predicted, columns: actual) ---- \n");
    builder.append("Accuracy       ");
    builder.append(String.format("%.2f", getAccuracy()));
    builder.append(" (");
    builder.append(String.format("%.2f", getError()));
    builder.append(")\n");
    builder.append("Avg. precision ").append(String.format("%.2f\n", getAveragePrecision()));
    builder.append("Avg. recall    ").append(String.format("%.2f\n", getAverageRecall()));

    return builder.toString();
  }

  private String padEnd(String s, int i, char c) {
    int m = Math.max(s.length(), i);
    StringBuilder builder = new StringBuilder(s);
    for (int j = 0; j < m; j++) {
      builder.append(c);
    }

    return builder.toString();
  }

  private String repeat(String p, int len) {
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < len; i++) {
      builder.append(p);
    }
    return builder.toString();
  }
}

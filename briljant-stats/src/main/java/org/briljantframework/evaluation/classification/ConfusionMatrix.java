/*
 * The MIT License (MIT)
 * 
 * Copyright (c) 2015 Isak Karlsson
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.briljantframework.evaluation.classification;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.briljantframework.data.dataframe.DataFrame;
import org.briljantframework.data.dataframe.MixedDataFrame;
import org.briljantframework.data.vector.Vector;
import org.briljantframework.evaluation.Measure;

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
 * @author Isak Karlsson
 */
public class ConfusionMatrix implements Measure {

  private final List<Map<Object, Map<Object, Double>>> matrices;
  private final Map<Object, Map<Object, Double>> averageMatrix;
  private final Set<Object> labels;
  private final Vector sums;
  private final double averageSum;

  public ConfusionMatrix(Map<Object, Map<Object, Double>> averageMatrix,
      List<Map<Object, Map<Object, Double>>> matrices, Set<Object> labels, Vector sums,
      double averageSum) {
    this.matrices = matrices;
    this.averageMatrix = Objects.requireNonNull(averageMatrix, "Matrix cannot be null");
    this.labels =
        Collections.unmodifiableSet(Objects.requireNonNull(labels, "Labels cannot be null"));

    this.averageSum = averageSum;
    this.sums = sums;
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
    Map<Object, Double> values = averageMatrix.get(predicted);
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
    return getAccuracy(averageMatrix, averageSum);
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

  private double getAccuracy(Map<Object, Map<Object, Double>> matrix, double sum) {
    double diagonal = 0.0;
    for (Object value : labels) {
      diagonal += get(matrix, value, value);
    }
    return diagonal / sum;
  }

  public DataFrame toDataFrame() {
    DataFrame.Builder builder = MixedDataFrame.builder();
    for (Object predicted : labels) {
      for (Object actual : labels) {
        builder.set(predicted, actual, get(predicted, actual));
      }
    }
    return builder.build();
  }

  @Override
  public String toString() {
    return "ConfusionMatrix{accuracy=" + getAccuracy() + " precision=" + getAveragePrecision()
        + " recall=" + getAverageRecall() + "}";
  }

  @Override
  public int size() {
    return matrices.size();
  }

  @Override
  public String getName() {
    return "Confusion Matrix";
  }

  private static double get(Map<Object, Map<Object, Double>> averageMatrix, Object predicted,
      Object actual) {
    Map<Object, Double> values = averageMatrix.get(predicted);
    if (values == null) {
      return 0;
    } else {
      return values.getOrDefault(actual, 0.0);
    }
  }

  public static final class Builder implements Measure.Builder<ConfusionMatrix> {

    private final List<Map<Object, Map<Object, Double>>> matrices = new ArrayList<>();
    private final Set<Object> labels = new HashSet<>();
    private final Vector.Builder sums = Vector.Builder.of(Integer.class);

    public void add(Vector p, Vector t) {
      Map<Object, Map<Object, Double>> matrix = new HashMap<>();
      double sum = 0;
      for (int i = 0; i < p.size(); i++) {
        Object predicted = p.loc().get(i);
        Object actual = t.loc().get(i);

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
      sums.add(sum);
      matrices.add(matrix);
    }

    private Map<Object, Map<Object, Double>> getAverageConfusionMatrix() {
      Map<Object, Map<Object, Double>> matrix = new HashMap<>();
      for (Map<Object, Map<Object, Double>> cm : matrices) {
        for (Object predicted : labels) {
          for (Object actual : labels) {
            Map<Object, Double> actuals = matrix.get(predicted);
            if (actuals == null) {
              actuals = new HashMap<>();
              matrix.put(predicted, actuals);
            }
            double count = get(cm, predicted, actual);
            actuals.compute(actual, (key, value) -> value == null ? count : count + value);
          }
        }
      }
      return matrix;
    }

    @Override
    public ConfusionMatrix build() {
      Vector s = sums.build();
      return new ConfusionMatrix(getAverageConfusionMatrix(), matrices, labels, s, s.sum());
    }
  }
}

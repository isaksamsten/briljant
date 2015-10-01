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

package org.briljantframework.evaluation.result;

import static org.briljantframework.data.vector.Vectors.find;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.briljantframework.Check;
import org.briljantframework.array.DoubleArray;
import org.briljantframework.data.vector.Vector;

/**
 * @author Isak Karlsson
 */
public final class Measures {

  private Measures() {}

  /**
   * Returns the prediction error, i.e. the fraction of miss-classified values. The same as
   * {@code 1 - accuracy}.
   *
   * @param predicted the predicted values; shape {@code [no sample]}
   * @param actual the actual values; shape {@code [no samples]}
   * @return the error rate
   */
  public static double error(Vector predicted, Vector actual) {
    return 1 - accuracy(predicted, actual);
  }

  /**
   * Returns the prediction accuracy, i.e., the fraction of correctly classified examples.
   *
   * @param predicted the predicted values; shape {@code [no sample]}
   * @param actual the actual values; shape {@code [no samples]}
   * @return the accuracy
   */
  public static double accuracy(Vector predicted, Vector actual) {
    Check.size(predicted.size(), actual.size());
    double accuracy = 0;
    int n = predicted.size();
    for (int i = 0; i < n; i++) {
      if (predicted.loc().equals(i, actual, i)) {
        accuracy += 1;
      }
    }
    return accuracy / n;
  }

  /**
   * Computes the brier score. The brier score is defined as the squared difference between the
   * classification probabilities and the optimal probability.
   *
   * @param predicted vector of shape {@code [no samples]}
   * @param scores matrix of shape {@code [no samples, no classes]}
   * @param actual vector of shape {@code [no samples]}
   * @param classes vector of shape {@code [no classes]}; the i:th index gives the score column in
   *        {@code scores}
   * @return the brier score
   */
  public static double brier(Vector predicted, DoubleArray scores, Vector actual, Vector classes) {
    Check.size(predicted.size(), actual.size());
    Check.size(actual.size(), scores.rows());

    int n = predicted.size();
    double brier = 0;
    for (int i = 0; i < n; i++) {
      double prob = scores.get(i, find(classes, predicted.loc().get(Object.class, i)));
      if (predicted.loc().equals(i, actual, i)) {
        brier += Math.pow(1 - prob, 2);
      } else {
        brier += prob * prob;
      }
    }
    return brier / n;
  }

  /**
   * @param predicted vector of shape {@code [no samples]}
   * @param probabilities matrix of shape {@code [no samples, domain.size()]}
   * @param actual vector of shape {@code [no samples]}
   * @param domain vector of shape {@code [no classes]}
   * @return a map of values (from {@code domain}) and its associated area under roc-curve
   */
  public static Map<Object, Double> auc(Vector predicted, DoubleArray probabilities, Vector actual,
      Vector domain) {
    Map<Object, Double> aucs = new HashMap<>();
    for (int i = 0; i < domain.size(); i++) {
      Object value = domain.loc().get(Object.class, i);
      DoubleArray p = probabilities.getColumn(i);
      aucs.put(value, computeAuc(value, predicted, p, actual));
    }
    return aucs;
  }

  private static double computeAuc(Object value, Vector predicted, DoubleArray proba, Vector actual) {
    double truePositives = 0, falsePositives = 0, positives = 0;
    List<PredictionProbability> pairs = new ArrayList<>(predicted.size());
    for (int i = 0; i < actual.size(); i++) {
      boolean positiveness = actual.loc().get(Object.class, i).equals(value); // .equals(i, value,
                                                                              // 0);
      if (positiveness) {
        positives++;
      }
      pairs.add(new PredictionProbability(positiveness, proba.get(i)));
    }

    // Sort in decreasing order of posterior probability
    Collections.sort(pairs);

    double negatives = predicted.size() - positives;
    double previousProbability = -1;
    double auc = 0.0;
    double previousTruePositive = 0.0;
    double previousFalsePositive = 0.0;

    // Calculates the auc using trapezoidal rule
    for (PredictionProbability pair : pairs) {
      double probability = pair.probability;
      if (probability != previousProbability) {
        double falseChange = Math.abs(falsePositives - previousFalsePositive);
        double trueChange = truePositives + previousTruePositive;
        auc += falseChange * trueChange / 2;

        previousFalsePositive = falsePositives;
        previousTruePositive = truePositives;
        previousProbability = probability;
      }

      if (pair.positive) {
        truePositives++;
      } else {
        falsePositives++;
      }
    }
    if (positives * negatives == 0) {
      return 0;
    } else {
      double negChange = Math.abs(negatives - previousFalsePositive);
      double posChange = positives + previousTruePositive;
      return (auc + negChange * posChange / 2) / (positives * negatives);
    }
  }

  private static final class PredictionProbability implements Comparable<PredictionProbability> {

    public final boolean positive;
    public final double probability;

    private PredictionProbability(boolean positive, double probability) {
      this.positive = positive;
      this.probability = probability;
    }

    @Override
    public int compareTo(PredictionProbability o) {
      return Double.compare(o.probability, this.probability);
    }
  }
}

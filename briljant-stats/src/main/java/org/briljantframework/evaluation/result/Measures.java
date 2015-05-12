package org.briljantframework.evaluation.result;

import org.briljantframework.Check;
import org.briljantframework.matrix.DoubleMatrix;
import org.briljantframework.vector.Vector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.briljantframework.vector.Vec.find;

/**
 * @author Isak Karlsson
 */
public final class Measures {

  private Measures() {
  }

  /**
   * Returns the prediction error, i.e. the fraction of miss-classified values. The same as
   * {@code 1 - accuracy}.
   *
   * @param predicted the predicted values; shape {@code [no sample]}
   * @param actual    the actual values; shape {@code [no samples]}
   * @return the error rate
   */
  public static double error(Vector predicted, Vector actual) {
    return 1 - accuracy(predicted, actual);
  }

  /**
   * Returns the prediction accuracy, i.e., the fraction of correctly classified examples.
   *
   * @param predicted the predicted values; shape {@code [no sample]}
   * @param actual    the actual values; shape {@code [no samples]}
   * @return the accuracy
   */
  public static double accuracy(Vector predicted, Vector actual) {
    Check.size(predicted, actual);
    double accuracy = 0;
    int n = predicted.size();
    for (int i = 0; i < n; i++) {
      if (predicted.equals(i, actual, i)) {
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
   * @param scores    matrix of shape {@code [no samples, no classes]}
   * @param actual    vector of shape {@code [no samples]}
   * @param classes   vector of shape {@code [no classes]}; the i:th index gives the score column
   *                  in
   *                  {@code scores}
   * @return the brier score
   */
  public static double brier(Vector predicted, DoubleMatrix scores, Vector actual, Vector classes) {
    Check.size(predicted.size(), actual.size());
    Check.size(actual.size(), scores.rows());

    int n = predicted.size();
    double brier = 0;
    for (int i = 0; i < n; i++) {
      double prob = scores.get(i, find(classes, predicted.get(Object.class, i)));
      if (predicted.equals(i, actual, i)) {
        brier += Math.pow(1 - prob, 2);
      } else {
        brier += prob * prob;
      }
    }
    return brier / n;
  }

  /**
   * @param predicted     vector of shape {@code [no samples]}
   * @param probabilities matrix of shape {@code [no samples, domain.size()]}
   * @param actual        vector of shape {@code [no samples]}
   * @param domain        vector of shape {@code [no classes]}
   * @return a map of values (from {@code domain}) and its associated area under roc-curve
   */
  public static Map<Object, Double> auc(Vector predicted, DoubleMatrix probabilities, Vector actual,
                                        Vector domain) {
    Map<Object, Double> aucs = new HashMap<>();
    for (int i = 0; i < domain.size(); i++) {
      Object value = domain.get(Object.class, i);
      DoubleMatrix p = probabilities.getColumnView(i);
      aucs.put(value, computeAuc(value, predicted, p, actual));
    }
    return aucs;
  }

  private static double computeAuc(Object value, Vector predicted, DoubleMatrix proba,
                                   Vector actual) {
    double truePositives = 0, falsePositives = 0, positives = 0;
    List<PredictionProbability> pairs = new ArrayList<>(predicted.size());
    for (int i = 0; i < actual.size(); i++) {
      boolean positiveness = actual.get(Object.class, i).equals(value); //.equals(i, value, 0);
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

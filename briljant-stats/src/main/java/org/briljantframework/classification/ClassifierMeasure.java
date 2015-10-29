package org.briljantframework.classification;

import static org.briljantframework.data.vector.Vectors.find;

import java.util.Arrays;

import org.briljantframework.Check;
import org.briljantframework.array.DoubleArray;
import org.briljantframework.data.Is;
import org.briljantframework.data.dataframe.DataFrame;
import org.briljantframework.data.vector.DoubleVector;
import org.briljantframework.data.vector.Vector;
import org.briljantframework.evaluation.PredictionMeasure;

/**
 * @author Isak Karlsson <isak-kar@dsv.su.se>
 */
public enum ClassifierMeasure implements PredictionMeasure<Classifier> {

  /**
   * Measures the fraction of correctly classified examples
   */
  ACCURACY,

  /**
   * Measures the fraction of incorrectly classified examples
   */
  ERROR,

  /**
   * Measures the probability of ranking a true positive ahead of a false positive example
   */
  AUCROC,

  /**
   * Measures the quality of probabilities
   */
  BRIER_SCORE,

  /**
   * Measures the fraction of true positives among the predicted positives (macro-averaged)
   */
  PRECISION,

  /**
   * Measures the fraction of true positives among the actual positives (macro-averaged)
   */
  RECALL,

  /**
   * Measures the harmonic mean between precision and recall (macro-averaged)
   */
  F1_SCORE;

  @Override
  public double compute(Classifier predictor, DataFrame x, Vector t) {
    Vector p = predictor.predict(x);
    Vector c = predictor.getClasses();
    switch (this) {
      case ACCURACY:
        return accuracy(p, t);
      case ERROR:
        return error(p, t);
      case AUCROC:
        return averageAreaUnderRocCurve(p, t, predictor.estimate(x), c);
      case BRIER_SCORE:
        return brierScore(p, t, predictor.estimate(x), c);
      case PRECISION:
        throw new UnsupportedOperationException();
      case RECALL:
        throw new UnsupportedOperationException();
      case F1_SCORE:
        throw new UnsupportedOperationException();
      default:
        throw new EnumConstantNotPresentException(ClassifierMeasure.class, this.getName());
    }
  }

  public static Vector computeAll(Classifier c, DataFrame x, Vector t) {
    Vector.Builder measures = new DoubleVector.Builder();
    for (ClassifierMeasure measure : ClassifierMeasure.values()) {
      measures.set(measure, measure.compute(c, x, t));
    }
    return measures.build();
  }

  /**
   * Returns the prediction error, i.e. the fraction of miss-classified values. The same as
   * {@code 1 - accuracy}.
   *
   * @param p the predicted values; shape {@code [no sample]}
   * @param t the actual values; shape {@code [no samples]}
   * @return the error rate
   */
  public static double error(Vector p, Vector t) {
    return 1 - accuracy(p, t);
  }

  /**
   * Returns the prediction accuracy, i.e., the fraction of correctly classified examples.
   *
   * @param p the predicted values; shape {@code [no sample]}
   * @param t the actual values; shape {@code [no samples]}
   * @return the accuracy
   */
  public static double accuracy(Vector p, Vector t) {
    Check.size(p.size(), t.size());
    double accuracy = 0;
    int n = p.size();
    for (int i = 0; i < n; i++) {
      if (Is.equal(p.loc().get(i), t.loc().get(i))) {
        accuracy += 1;
      }
    }
    return accuracy / n;
  }

  /**
   * Computes the brier score. The brier score is defined as the squared difference between the
   * classification probabilities and the optimal probability.
   *
   * @param p vector of shape {@code [no samples]}
   * @param t vector of shape {@code [no samples]}
   * @param scores matrix of shape {@code [no samples, no classes]}
   * @param c vector of shape {@code [no classes]}; the i:th index gives the score column in
   *        {@code scores}
   * @return the brier score
   */
  public static double brierScore(Vector p, Vector t, DoubleArray scores, Vector c) {
    Check.size(p.size(), t.size());
    Check.size(t.size(), scores.rows());

    int n = p.size();
    double brier = 0;
    for (int i = 0; i < n; i++) {
      int classIndex = find(c, p, i);
      if (classIndex < 0 || classIndex > c.size()) {
        throw new IllegalStateException("Missing class " + p.loc().get(i));
      }

      double prob = scores.get(i, classIndex);
      if (Is.equal(p.loc().get(i), t.loc().get(i))) {
        brier += Math.pow(1 - prob, 2);
      } else {
        brier += prob * prob;
      }
    }
    return brier / n;
  }

  /**
   * @param p vector of shape {@code [no samples]} the predicted labels
   * @param t vector of shape {@code [no samples]} the true labels
   * @param score matrix of shape {@code [no samples, domain.size()]} with scores (probabilities,
   *        confidences or binary indicators)
   * @param c vector of shape {@code [no classes]} the i:th index in the domain denotes the score in
   *        the j:th column of the score matrix
   * @return a vector of labels (from {@code c}) and its associated area under roc-curve
   */
  public static Vector areaUnderRocCurve(Vector p, Vector t, DoubleArray score, Vector c) {
    Vector.Builder builder = new DoubleVector.Builder();
    for (int i = 0; i < c.size(); i++) {
      Object value = c.loc().get(i);
      DoubleArray s = score.getColumn(i);
      builder.set(value, computeAuc(p, t, s, value));
    }
    return builder.build();
  }

  public static double averageAreaUnderRocCurve(Vector p, Vector a, DoubleArray score, Vector c) {
    Vector auc = areaUnderRocCurve(p, a, score, c);
    Vector dist = a.valueCounts();
    double averageAuc = 0;
    for (Object classKey : auc) {
      if (dist.getIndex().contains(classKey)) {
        int classCount = dist.getAsInt(classKey);
        averageAuc += auc.getAsDouble(classKey) * (classCount / (double) a.size());
      }
    }
    return averageAuc;
  }

  private static double computeAuc(Vector p, Vector t, DoubleArray score, Object label) {
    double truePositives = 0, falsePositives = 0, positives = 0;
    PredictionProbability[] pairs = new PredictionProbability[p.size()];
    for (int i = 0; i < t.size(); i++) {
      boolean positiveness = Is.equal(t.loc().get(i), label);
      if (positiveness) {
        positives++;
      }
      pairs[i] = new PredictionProbability(positiveness, score.get(i));
    }

    // Sort in decreasing order of posterior probability
    Arrays.sort(pairs);

    double negatives = p.size() - positives;
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
    if (truePositives == 0) {
      return 0;
    } else if (falsePositives == 0) {
      return 1;
    } else {
      double negChange = Math.abs(negatives - previousFalsePositive);
      double posChange = positives + previousTruePositive;
      return (auc + negChange * posChange / 2) / (positives * negatives);
    }
  }

  public String getName() {
    return null;
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

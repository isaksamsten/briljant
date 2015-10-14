package org.briljantframework.evaluation.conformal;

import org.briljantframework.Check;
import org.briljantframework.array.Arrays;
import org.briljantframework.array.BooleanArray;
import org.briljantframework.array.DoubleArray;
import org.briljantframework.conformal.ConformalClassifier;
import org.briljantframework.data.vector.Vector;
import org.briljantframework.data.vector.Vectors;
import org.briljantframework.evaluation.EvaluationContext;
import org.briljantframework.evaluation.Evaluator;
import org.briljantframework.evaluation.Sample;
import org.briljantframework.evaluation.classification.Accuracy;
import org.briljantframework.evaluation.classification.ErrorRate;

/**
 * @author Isak Karlsson <isak-kar@dsv.su.se>
 */
public class ConformalClassifierEvaluator implements Evaluator {

  private final double significance;

  public ConformalClassifierEvaluator(double significance) {
    this.significance = significance;
  }

  @Override
  public void accept(EvaluationContext ctx) {
    Check.argument(ctx.getPredictor() instanceof ConformalClassifier,
        "requires a conformal predictor");

    ConformalClassifier classifier = (ConformalClassifier) ctx.getPredictor();
    Vector y = ctx.getPartition().getValidationTarget();
    Vector classes = classifier.getClasses();
    DoubleArray estimates = ctx.getEstimation();

    // Compute accuracy
    BooleanArray predictions = estimates.satisfies(v -> v >= significance);
    double accuracy = 0;
    for (int i = 0; i < predictions.rows(); i++) {
      int j = Vectors.find(classes, y, i);
      Check.state(j >= 0, "Illegal class");
      if (predictions.get(i, j)) {
        accuracy++;
      }
    }
    ctx.getOrDefault(Accuracy.class, Accuracy.Builder::new).add(Sample.OUT, accuracy / y.size());
    ctx.getOrDefault(ErrorRate.class, ErrorRate.Builder::new).add(Sample.OUT,
        1 - (accuracy / y.size()));

    // Compute confidence and credibility
    double avgConfidence = 0, avgCredibility = 0;
    for (int i = 0; i < estimates.rows(); i++) {
      DoubleArray estimate = estimates.getRow(i);
      int prediction = Arrays.argmax(estimate);
      double credibility = estimate.get(prediction);
      double confidence = 1 - maxnot(estimate, prediction);

      avgCredibility += credibility / estimates.rows();
      avgConfidence += confidence / estimates.rows();
    }

    ctx.getOrDefault(Confidence.class, Confidence.Builder::new).add(Sample.OUT, avgConfidence);
    ctx.getOrDefault(Credibility.class, Credibility.Builder::new).add(Sample.OUT, avgCredibility);
  }

  private static double maxnot(DoubleArray array, int not) {
    Double max = Double.NEGATIVE_INFINITY;
    for (int i = 0; i < array.size(); i++) {
      if (i == not) {
        continue;
      }
      double m = array.get(i);
      if (m > max) {
        max = m;
      }
    }
    return max;
  }

}

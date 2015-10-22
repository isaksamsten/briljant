package org.briljantframework.evaluation.conformal;

import org.briljantframework.Check;
import org.briljantframework.array.Arrays;
import org.briljantframework.array.BooleanArray;
import org.briljantframework.array.DoubleArray;
import org.briljantframework.classification.ClassifierMeasure;
import org.briljantframework.conformal.ConformalClassifier;
import org.briljantframework.data.vector.Vector;
import org.briljantframework.data.vector.Vectors;
import org.briljantframework.evaluation.EvaluationContext;
import org.briljantframework.evaluation.Evaluator;
import org.briljantframework.evaluation.MeasureCollection;

/**
 * @author Isak Karlsson <isak-kar@dsv.su.se>
 */
public class ConformalClassifierEvaluator implements Evaluator<ConformalClassifier> {

  private final double significance;

  public ConformalClassifierEvaluator(double significance) {
    this.significance = significance;
  }

  @Override
  public void accept(EvaluationContext<? extends ConformalClassifier> ctx) {
    ConformalClassifier classifier = ctx.getPredictor();
    Vector y = ctx.getPartition().getValidationTarget();
    Vector classes = classifier.getClasses();
    DoubleArray estimates = ctx.getEstimates();
    MeasureCollection<? extends ConformalClassifier> measureCollection = ctx.getMeasureCollection();

    // Compute accuracy
    BooleanArray predictions = estimates.where(v -> v >= significance);
    double accuracy = 0;
    for (int i = 0; i < predictions.rows(); i++) {
      int j = Vectors.find(classes, y, i);
      Check.state(j >= 0, "Illegal class");
      if (predictions.get(i, j)) {
        accuracy++;
      }
    }
    measureCollection.add(ClassifierMeasure.ACCURACY, accuracy / y.size());
    measureCollection.add(ClassifierMeasure.ERROR, 1 - (accuracy / y.size()));

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

    measureCollection.add(ConformalClassifierMeasure.CONFIDENCE, avgConfidence);
    measureCollection.add(ConformalClassifierMeasure.CREDIBILITY, avgCredibility);
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

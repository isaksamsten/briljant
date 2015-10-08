package org.briljantframework.conformal.conformal;

import org.briljantframework.Check;
import org.briljantframework.array.BooleanArray;
import org.briljantframework.conformal.ConformalClassifier;
import org.briljantframework.data.vector.Vector;
import org.briljantframework.data.vector.Vectors;
import org.briljantframework.evaluation.measure.Accuracy;
import org.briljantframework.evaluation.result.EvaluationContext;
import org.briljantframework.evaluation.result.Evaluator;
import org.briljantframework.evaluation.result.Sample;

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
    if (!(ctx.getPredictor() instanceof ConformalClassifier)) {
      throw new IllegalArgumentException("requires conformal predictor");
    }
    Vector y = ctx.getPartition().getValidationTarget();
    Vector classes = ctx.getPredictor().getClasses();
    BooleanArray predictions = ctx.getEstimation(Sample.OUT).satisfies(v -> v > significance);
    double accuracy = 0;
    for (int i = 0; i < predictions.rows(); i++) {
      int j = Vectors.find(classes, y.loc().get(i));
      Check.state(j >= 0, "Illegal class");
      if (predictions.get(i, j)) {
        accuracy++;
      }
    }
    ctx.getOrDefault(Accuracy.class, Accuracy.Builder::new).add(Sample.OUT, accuracy / y.size());
  }
}

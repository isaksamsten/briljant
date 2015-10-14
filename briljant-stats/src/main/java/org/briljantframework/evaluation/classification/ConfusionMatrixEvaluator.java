package org.briljantframework.evaluation.classification;

import org.briljantframework.evaluation.EvaluationContext;
import org.briljantframework.evaluation.Evaluator;

/**
 * @author Isak Karlsson <isak-kar@dsv.su.se>
 */
public class ConfusionMatrixEvaluator implements Evaluator {

  @Override
  public void accept(EvaluationContext ctx) {
    ctx.getOrDefault(ConfusionMatrix.class, ConfusionMatrix.Builder::new).add(
        ctx.getPredictions(), ctx.getPartition().getValidationTarget());
  }
}

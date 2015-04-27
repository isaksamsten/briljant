package org.briljantframework.evaluation.result;

import static org.briljantframework.evaluation.result.Measures.accuracy;

import org.briljantframework.evaluation.measure.Accuracy;
import org.briljantframework.evaluation.measure.ErrorRate;

/**
 * @author Isak Karlsson
 */
public class ErrorEvaluator implements Evaluator {

  @Override
  public void accept(EvaluationContext ctx) {
    double a = accuracy(ctx.getPredictions(Sample.OUT), ctx.getPartition().getValidationTarget());
    ctx.getOrDefault(ErrorRate.class, ErrorRate.Builder::new).add(Sample.OUT, 1 - a);
    ctx.getOrDefault(Accuracy.class, Accuracy.Builder::new).add(Sample.OUT, a);
  }
}

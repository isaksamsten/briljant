package org.briljantframework.evaluation.result;

import org.briljantframework.vector.Vector;

/**
 * Created by isak on 2/13/15.
 */
public class ErrorConsumer implements MeasureConsumer {
  @Override
  public void accept(ResultContext ctx) {
    Vector predicted = ctx.getPredictions(Sample.OUT);
    Vector actual = ctx.getPartition().getValidationTarget();
    double accuracy = 0.0;
    for (int i = 0; i < predicted.size(); i++) {
      if (predicted.getAsString(i).equals(actual.getAsString(i))) {
        accuracy++;
      }
    }

    ctx.getOrDefault(ErrorRate.class, ErrorRate.Builder::new).add(1 - accuracy);
    ctx.getOrDefault(Accuracy.class, Accuracy.Builder::new).add(accuracy);
  }
}

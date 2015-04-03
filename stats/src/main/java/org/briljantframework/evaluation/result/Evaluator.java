package org.briljantframework.evaluation.result;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.function.IntConsumer;

/**
 * @author Isak Karlsson
 */
public interface Evaluator {

  static Evaluator foldOutput(IntConsumer consumer) {
    return new Evaluator() {
      private int fold = 0;

      @Override
      public void accept(EvaluationContext ctx) {
        consumer.accept(fold++);
      }
    };
  }

  static List<Evaluator> getDefaultClassificationEvaluators() {
    return Lists.newArrayList(new ErrorEvaluator(), new ProbabilityEvaluator());
  }

  /**
   * Performs a modification to the evaluation context. For example, adding a measure.
   *
   * @param ctx the evaluation context
   */
  void accept(EvaluationContext ctx);
}

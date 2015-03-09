package org.briljantframework.evaluation.result;

import java.util.List;
import java.util.function.Consumer;

import com.google.common.collect.Lists;

/**
 * @author Isak Karlsson
 */
public interface Evaluator {

  static Evaluator foldOutput(Consumer<Integer> consumer) {
    return new Evaluator() {
      private int fold = 0;

      @Override
      public void accept(EvaluationContext ctx) {
        consumer.accept(fold++);
      }
    };
  };

  static List<Evaluator> getDefaultClassificationEvaluators() {
    return Lists.newArrayList(new ErrorEvaluator(), new ProbabilityEvaluator());
  }

  void accept(EvaluationContext ctx);
}

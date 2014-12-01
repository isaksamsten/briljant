package org.briljantframework.learning.evaluation;

import org.briljantframework.learning.evaluation.result.Metrics;

/**
 * Created by isak on 02/10/14.
 */
public class Evaluators {

  /**
   * Cross validation.
   *
   * @param folds the folds
   * @return the evaluator
   */
  public static Evaluator crossValidation(int folds) {
    return new DefaultEvaluator(Metrics.CLASSIFICATION, new RandomFoldPartitioner(folds));
  }

  /**
   * Split validation
   * 
   * @param testFraction the validation fraction
   * @return an evaluator
   */
  public static Evaluator splitValidation(double testFraction) {
    return new DefaultEvaluator(Metrics.CLASSIFICATION, new RandomSplitPartitioner(testFraction));
  }

}

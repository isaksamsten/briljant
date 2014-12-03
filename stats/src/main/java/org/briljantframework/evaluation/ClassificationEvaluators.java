package org.briljantframework.evaluation;

/**
 * Created by isak on 02/10/14.
 */
public class ClassificationEvaluators {

  /**
   * Cross validation.
   *
   * @param folds the folds
   * @return the evaluator
   */
  public static ClassificationEvaluator crossValidation(int folds) {
    return new DefaultClassificationEvaluator(new RandomFoldPartitioner(folds));
  }

  /**
   * Split validation
   * 
   * @param testFraction the validation fraction
   * @return an evaluator
   */
  public static ClassificationEvaluator splitValidation(double testFraction) {
    return new DefaultClassificationEvaluator(new RandomSplitPartitioner(testFraction));
  }

}

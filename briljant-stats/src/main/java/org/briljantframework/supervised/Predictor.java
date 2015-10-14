package org.briljantframework.supervised;

import java.util.Set;

import org.briljantframework.data.dataframe.DataFrame;
import org.briljantframework.data.vector.Vector;
import org.briljantframework.evaluation.EvaluationContext;

/**
 * @author Isak Karlsson <isak-kar@dsv.su.se>
 */
public interface Predictor {

  /**
   * Return a vector of predictions for the records in the given data frame.
   * 
   * @param x the data frame
   * @return a vector of predictions
   */
  Vector predict(DataFrame x);

  /**
   * Get a set of characteristics for this particular predictor
   *
   * @return the set of characteristics
   */
  Set<Characteristic> getCharacteristics();

  /**
   * Performs an internal evaluation of the predictor and appending the produced evaluators to the
   * supplied {@linkplain EvaluationContext evaluation context}.
   *
   * @param ctx the evaluation context
   */
  void evaluate(EvaluationContext ctx);

  interface Learner {

    Predictor fit(DataFrame x, Vector y);
  }
}

package org.briljantframework.conformal;

import org.briljantframework.data.dataframe.DataFrame;
import org.briljantframework.data.vector.Vector;

/**
 * Learn a {@linkplain NonconformityScorer nonconformity score function} using the given data and
 * targets.
 * 
 * @author Isak Karlsson <isak-kar@dsv.su.se>
 */
public interface NonconformityLearner {

  /**
   * Fit a {@linkplain NonconformityScorer nonconformity score function} using the given data.
   * 
   * @param x the input data
   * @param y the input target
   * @return a nonconformity score function
   */
  NonconformityScorer fit(DataFrame x, Vector y);
}

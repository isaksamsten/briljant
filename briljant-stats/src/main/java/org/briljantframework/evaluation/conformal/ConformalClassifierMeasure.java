package org.briljantframework.evaluation.conformal;

import org.briljantframework.conformal.ConformalClassifier;
import org.briljantframework.data.dataframe.DataFrame;
import org.briljantframework.data.vector.Vector;
import org.briljantframework.evaluation.PredictionMeasure;

/**
 * @author Isak Karlsson <isak-kar@dsv.su.se>
 */
public enum  ConformalClassifierMeasure implements PredictionMeasure<ConformalClassifier> {
  CONFIDENCE, CREDIBILITY;

  @Override
  public double compute(ConformalClassifier predictor, DataFrame x, Vector t) {
    return 0;
  }
}

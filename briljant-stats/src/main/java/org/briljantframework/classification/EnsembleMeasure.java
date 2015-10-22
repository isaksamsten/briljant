package org.briljantframework.classification;

import org.briljantframework.data.dataframe.DataFrame;
import org.briljantframework.data.vector.Vector;
import org.briljantframework.evaluation.PredictionMeasure;

/**
 * @author Isak Karlsson <isak-kar@dsv.su.se>
 */
public enum EnsembleMeasure implements PredictionMeasure<Ensemble> {
  BASE_ERROR, OOB_ERROR, BIAS, VARIANCE, MSE, QUALITY, CORRELATION, STRENGTH, ERROR_BOUND;

  @Override
  public double compute(Ensemble predictor, DataFrame x, Vector t) {
    throw new UnsupportedOperationException();
  }
}

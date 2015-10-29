package org.briljantframework.evaluation;

import org.briljantframework.data.Na;
import org.briljantframework.data.dataframe.DataFrame;
import org.briljantframework.data.vector.Vector;
import org.briljantframework.supervised.Predictor;

/**
 * @author Isak Karlsson <isak-kar@dsv.su.se>
 */
public enum DefaultMeasure implements PredictionMeasure<Predictor> {
  /**
   * Measures the time (in milliseconds) required to fit a predictor
   */
  FIT_TIME,

  /**
   * Measures the time (in milliseconds) required to make predictions using the predictor
   */
  PREDICT_TIME,

  /**
   * Measures the training set size
   */
  TRAINING_SIZE,

  /**
   * Measures the validation set size
   */
  VALIDATION_SIZE;

  /**
   * Compute the value for this measure given the specified predictor and the given validation data.
   * 
   * <p/>
   * Note that {@code NA} is returned for {@link #FIT_TIME} and {@link #TRAINING_SIZE} since these
   * are unknown.
   * 
   * @param predictor the predictor
   * @param x the data frame to predict
   * @param t the true class labels
   * @return the computed measure
   */
  @Override
  public double compute(Predictor predictor, DataFrame x, Vector t) {
    switch (this) {
      case FIT_TIME:
        return Na.of(double.class);
      case PREDICT_TIME:
        long start = System.nanoTime();
        predictor.predict(x);
        return (System.nanoTime() - start) / 1e6;
      case TRAINING_SIZE:
        return Na.of(double.class);
      case VALIDATION_SIZE:
        return x.rows();
    }
    throw new EnumConstantNotPresentException(DefaultMeasure.class, this.toString());
  }
}

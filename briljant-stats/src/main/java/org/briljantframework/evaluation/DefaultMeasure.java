package org.briljantframework.evaluation;

import org.briljantframework.data.Na;
import org.briljantframework.data.dataframe.DataFrame;
import org.briljantframework.data.vector.Vector;
import org.briljantframework.supervised.Predictor;

/**
 * @author Isak Karlsson <isak-kar@dsv.su.se>
 */
public enum DefaultMeasure implements PredictionMeasure<Predictor> {
  FIT_TIME, PREDICT_TIME, TRAINING_SIZE, VALIDATION_SIZE;

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

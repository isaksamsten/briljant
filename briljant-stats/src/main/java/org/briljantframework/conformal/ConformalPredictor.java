package org.briljantframework.conformal;

import java.util.stream.IntStream;

import org.briljantframework.Bj;
import org.briljantframework.array.BooleanArray;
import org.briljantframework.array.DoubleArray;
import org.briljantframework.classification.Predictor;
import org.briljantframework.data.Na;
import org.briljantframework.data.dataframe.DataFrame;
import org.briljantframework.data.vector.Vector;

/**
 * @author Isak Karlsson <isak-kar@dsv.su.se>
 */
public interface ConformalPredictor extends Predictor {

  double DEFAULT_SIGNIFICANCE = 0.05;

  void calibrate(DataFrame x, Vector y);

  /**
   * Returns the conformal predictions for the records in the given data frame using the
   * {@link #DEFAULT_SIGNIFICANCE}
   *
   * @param x to determine class labels for
   * @return a vector of class-labels for those records with which a label can be assigned with
   *         {@link #DEFAULT_SIGNIFICANCE} probability; or {@code NA}.
   */
  @Override
  default Vector predict(DataFrame x) {
    return predict(x, DEFAULT_SIGNIFICANCE);
  }

  /**
   * Returns the conformal predictions for the records in the given data frame using the given
   * significance level.
   *
   * @param x to determine class labels for
   * @return a vector of class-labels for those records with which a label can be assigned with the
   *         given probability; or {@code NA}.
   */
  default Vector predict(DataFrame x, double significance) {
    Vector.Builder predictions = getClasses().newBuilder();
    for (int i = 0, size = x.rows(); i < size; i++) {
      predictions.add(predict(x.loc().getRecord(i), significance));
    }
    return predictions.build();
  }

  /**
   * Returns the prediction of the given example or {@code NA} if no class with
   * {@link #DEFAULT_SIGNIFICANCE} can be assigned.
   *
   * @param record to which the class label shall be assigned
   * @return a class-label or {@code NA}
   */
  @Override
  Object predict(Vector record);

  /**
   * Returns the prediction of the given example or {@code NA} if no class with the given
   * significance can be assigned.
   *
   * @param record to which the class label shall be assigned
   * @return a class-label or {@code NA}
   */
  default Object predict(Vector record, double significance) {
    DoubleArray estimate = estimate(record);
    if (estimate.filter(v -> v > significance).size() == 1) {
      return getClasses().loc().get(Object.class, Bj.argmax(estimate));
    } else {
      return Na.of(Object.class);
    }
  }

  /**
   * Returns a boolean array {@code [n-classes]}, where each element denotes which labels are
   * included in the prediction set.
   *
   * @param example the example to predict
   * @param significance the significance level
   * @return a
   */
  BooleanArray conformalPredict(Vector example, double significance);

  default BooleanArray conformalPredict(DataFrame x, double significance) {
    BooleanArray estimates = Bj.booleanArray(x.rows(), getClasses().size());
    IntStream.range(0, x.rows()).parallel().forEach(i -> {
      BooleanArray estimate = conformalPredict(x.loc().getRecord(i), significance);
      estimates.setRow(i, estimate);
    });
    return estimates;
  }

  /**
   * Returns an {@code [n-samples, n-classes]} double array of p-values associated with each class.
   *
   * @param x the data frame of records to estimate the p-values
   * @return the p-values
   */
  @Override
  DoubleArray estimate(DataFrame x);

  @Override
  DoubleArray estimate(Vector record);
}

package org.briljantframework.classification;

import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.matrix.DoubleMatrix;
import org.briljantframework.vector.Value;
import org.briljantframework.vector.Vector;

/**
 * The interface Model.
 * <p>
 * TODO(isak) - below:
 * <p>
 * In some cases models produce additional measurements. For example, a random forest produces
 * variable importance and a linear models produce standard error, t-statistics R^2 etc. One
 * question is how to incorporate these measurements into the different models. Perhaps they may
 * feeling is that they don't belong here, but rather to the particular implementation. However, it
 * might be useful to have for example a summary() function or similar. Perhaps even a plot(onto)
 * function.
 */
public interface Predictor {

  Vector getClasses();

  /**
   * Determine the class label of every instance in {@code x}
   *
   * @param x to determine class labels for
   * @return the predictions
   */
  Vector predict(DataFrame x);

  /**
   * Predict the class label of a specific {@link org.briljantframework.vector.Vector}
   *
   * @param row to which the class label shall be assigned
   * @return the prediction
   */
  Value predict(Vector row);

  DoubleMatrix predictProba(DataFrame x);

  DoubleMatrix predictProba(Vector row);
}

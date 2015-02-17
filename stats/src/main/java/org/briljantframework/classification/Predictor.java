package org.briljantframework.classification;

import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.evaluation.result.EvaluationContext;
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

  /**
   * The classes this predictor is able to predict, i.e. its co-domain. Note that the i:th element
   * of the returned vector is the label of the j:th column in the probability matrix returned by
   * {@link #estimate(org.briljantframework.dataframe.DataFrame)}.
   * 
   * @return the vector of classes.
   */
  Vector getClasses();

  /**
   * Determine the class label of every instance in {@code x}
   *
   * @param x to determine class labels for
   * @return the predictions; shape = {@code [x.rows, 1]}.
   */
  Vector predict(DataFrame x);

  /**
   * Predict the class label of a specific {@link org.briljantframework.vector.Vector}
   *
   * @param row to which the class label shall be assigned
   * @return the prediction
   */
  Value predict(Vector row);

  /**
   * Estimates the posterior probabilities for all records in {@code x}.
   * 
   * @param x the data frame of records to estimate the posterior probabilities for
   * @return a matrix with probability estimates; shape =
   *         {@code [x.rows(), this.getClasses().size()]}.
   */
  DoubleMatrix estimate(DataFrame x);

  /**
   * Estimates the posterior probability for the supplied vector.
   * 
   * @param row the vector to estimate the posterior probability for
   * @return a matrix with probability estimates; shape = {@code [1, this.getClasses().size()]}.
   */
  DoubleMatrix estimate(Vector row);

  void evaluation(EvaluationContext ctx);

}

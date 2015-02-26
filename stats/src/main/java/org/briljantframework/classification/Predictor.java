package org.briljantframework.classification;

import java.util.EnumSet;

import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.evaluation.result.EvaluationContext;
import org.briljantframework.matrix.DoubleMatrix;
import org.briljantframework.vector.Value;
import org.briljantframework.vector.Vector;

/**
 * A predictor is a model fit by a classifier to make predictions.
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

  /**
   * Get a set of characteristics for this particular predictor
   * 
   * @return the set of characteristics
   */
  EnumSet<Characteristics> getCharacteristics();

  /**
   * Perform an evaluation of the predictor and appending those evaluations to the
   * {@code EvaluationContext}.
   * 
   * @param ctx the evaluation context
   */
  void evaluation(EvaluationContext ctx);

  public enum Characteristics {
    ESTIMATOR
  }

}

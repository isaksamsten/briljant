/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Isak Karlsson
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.briljantframework.classification;

import org.briljantframework.array.DoubleArray;
import org.briljantframework.data.dataframe.DataFrame;
import org.briljantframework.data.vector.Vector;
import org.briljantframework.evaluation.result.EvaluationContext;

import java.util.EnumSet;

/**
 * A predictor is a model fit by a classifier to make predictions.
 */
public interface Predictor {

  /**
   * The classes this predictor is able to predict, i.e. its co-domain. Note that the i:th element
   * of the returned vector is the label of the j:th column in the probability matrix returned by
   * {@link #estimate(org.briljantframework.data.dataframe.DataFrame)}.
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
   * Predict the class label of a specific {@link org.briljantframework.data.vector.Vector}
   *
   * @param record to which the class label shall be assigned
   * @return the prediction
   */
  Object predict(Vector record);

  /**
   * Estimates the posterior probabilities for all records in {@code x}.
   *
   * <p> Each column corresponds to the probability of a particular class (the j:th column
   * correspond to the j:th element (using {@link Vector#loc()}) in {@linkplain #getClasses()}) and
   * each row corresponds to a particular record in the supplied data frame.
   *
   * @param x the data frame of records to estimate the posterior probabilities for
   * @return a matrix with probability estimates; shape =
   * {@code [x.rows(), getClasses().size()]}.
   */
  DoubleArray estimate(DataFrame x);

  /**
   * Estimates the posterior probability for the supplied vector.
   *
   * <p> The i:th element in the returned array correspond to the probability of the i:th class in
   * {@linkplain #getClasses()} (using {@link Vector#loc()})
   *
   * @param record the vector to estimate the posterior probability for
   * @return a matrix with probability estimates; shape = {@code [1, this.getClasses().size()]}.
   */
  DoubleArray estimate(Vector record);

  /**
   * Get a set of characteristics for this particular predictor
   *
   * @return the set of characteristics
   */
  EnumSet<Characteristics> getCharacteristics();

  /**
   * // TODO: rename evaluate
   * Perform an internal evaluation of the predictor and appending the produced evaluators to the
   * supplied {@linkplain EvaluationContext evaluation context}.
   *
   * @param ctx the evaluation context
   */
  void evaluate(EvaluationContext ctx);

  public enum Characteristics {
    ESTIMATOR
  }

}

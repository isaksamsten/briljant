/*
 * ADEB - machine learning pipelines made easy Copyright (C) 2014 Isak Karlsson
 * 
 * This program is free software; you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program; if
 * not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 */

package org.briljantframework.learning;

import java.util.ArrayList;
import java.util.List;

import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.vector.CompoundVector;
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
public interface Model {

  /**
   * Predict the class label every example in dataset
   *
   * @param dataset to determine class labels for
   * @return the predictions
   */
  default Predictions predict(DataFrame dataset) {
    List<Prediction> predictions = new ArrayList<>();
    for (CompoundVector e : dataset) {
      predictions.add(predict(e));
    }
    return Predictions.create(predictions);
  }

  /**
   * Predict the class label of a specific {@link org.briljantframework.vector.Vector}
   *
   * @param row to which the class label shall be assigned
   * @return the prediction
   */
  Prediction predict(Vector row);
}

package org.briljantframework.classification;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.briljantframework.dataframe.DataFrame;
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
public interface ClassifierModel {

  /**
   * Determine the class label of every instance in {@code x}
   *
   * @param x to determine class labels for
   * @return the predictions
   */
  default List<Label> predict(DataFrame x) {
    List<Label> labels = new ArrayList<>();
    for (Vector e : x) {
      labels.add(predict(e));
    }
    return Collections.unmodifiableList(labels);
  }

  /**
   * Predict the class label of a specific {@link org.briljantframework.vector.Vector}
   *
   * @param row to which the class label shall be assigned
   * @return the prediction
   */
  Label predict(Vector row);
}

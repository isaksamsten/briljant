package org.briljantframework.classification;

import java.util.*;

import com.google.common.base.Preconditions;

/**
 * Created by isak on 02/10/14.
 */
public class Predictions extends AbstractList<Prediction> {

  private final List<Prediction> predictions;
  private final Set<String> labels;

  /**
   * Instantiates a new Predictions.
   *
   * @param predictions the predictions
   * @param labels
   */
  private Predictions(List<Prediction> predictions, Set<String> labels) {
    this.predictions = Collections.unmodifiableList(predictions);
    this.labels = Collections.unmodifiableSet(labels);
  }

  /**
   * Create predictions.
   *
   * @param predictions the predictions
   * @return the predictions
   */
  public static Predictions create(List<Prediction> predictions) {
    Preconditions.checkNotNull(predictions);
    Preconditions.checkArgument(predictions.size() > 0);

    ArrayList<Prediction> copy = new ArrayList<>(predictions.size());
    Set<String> labels = new HashSet<>();
    for (Prediction p : predictions) {
      copy.add(p);
      labels.addAll(p.getPredictedValues());
    }
    return new Predictions(copy, labels);
  }

  /**
   * Gets labels.
   *
   * @return the labels
   */
  public Set<String> getDomainLabels() {
    return labels;
  }

  /**
   * Size int.
   *
   * @return the int
   */
  @Override
  public int size() {
    return predictions.size();
  }

  /**
   * Get prediction.
   *
   * @param index the index
   * @return the prediction
   */
  @Override
  public Prediction get(int index) {
    return predictions.get(index);
  }
}

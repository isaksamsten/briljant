package org.briljantframework.classification.shapelet;

import org.briljantframework.classification.tree.Gain;
import org.briljantframework.classification.tree.Splitter;
import org.briljantframework.matrix.distance.Distance;

/**
 * Created by isak on 02/10/14.
 */
public abstract class ShapeletSplitter implements Splitter<ShapeletThreshold> {

  protected final Gain gain;
  private final Distance metric;

  /**
   * Instantiates a new Shapelet splitter.
   *
   * @param metric the metric
   */
  protected ShapeletSplitter(Distance metric, Gain gain) {
    this.metric = metric;
    this.gain = gain;
  }

  /**
   * Gets metric.
   *
   * @return the metric
   */
  public Distance getDistanceMetric() {
    return metric;
  }

  /**
   * Gets gain.
   *
   * @return the gain
   */
  public Gain getGain() {
    return gain;
  }
}

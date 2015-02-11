package org.briljantframework.classification.tree;

import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.distance.Distance;
import org.briljantframework.vector.Vector;

/**
 * @author Isak Karlsson
 */
public abstract class ShapeletSplitter {

  protected final Gain gain;
  private final Distance metric;

  protected ShapeletSplitter(Distance metric, Gain gain) {
    this.metric = metric;
    this.gain = gain;
  }

  public final Distance getDistanceMetric() {
    return metric;
  }

  public final Gain getGain() {
    return gain;
  }

  public abstract TreeSplit<ShapeletThreshold> find(ClassSet classSet, DataFrame x, Vector y);
}

package org.briljantframework.evaluation.result;

import java.util.List;

import org.briljantframework.Check;
import org.briljantframework.classification.Label;
import org.briljantframework.vector.Vector;

/**
 * Created by isak on 2/11/15.
 */
public class Brier extends AbstractMeasure {
  /**
   * Instantiates a new Abstract metric.
   *
   * @param producer the producer
   */
  protected Brier(Builder producer) {
    super(producer);
  }

  @Override
  public String getName() {
    return "Brier-score";
  }

  public static class Builder extends AbstractMeasure.Builder {

    @Override
    public void compute(Sample sample, List<Label> predicted, Vector truth) {
      Check.size(predicted.size(), truth.size());
      double brier = 0;
      for (int i = 0; i < predicted.size(); i++) {
        Label label = predicted.get(i);
        double prob = label.getPredictedProbability();
        if (truth.getAsString(i).equals(label.getPredictedValue())) {
          brier += Math.pow(1 - prob, 2);
        } else {
          brier += prob * prob;
        }
      }
      addComputedValue(sample, brier / predicted.size());
    }

    @Override
    public Measure build() {
      return new Brier(this);
    }
  }

}

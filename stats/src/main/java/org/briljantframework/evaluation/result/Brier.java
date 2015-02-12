package org.briljantframework.evaluation.result;

import org.briljantframework.Check;
import org.briljantframework.classification.Predictor;
import org.briljantframework.matrix.DoubleMatrix;
import org.briljantframework.vector.Vector;
import org.briljantframework.vector.Vectors;

/**
 * @author Isak Karlsson
 */
public class Brier extends AbstractMeasure {

  protected Brier(Builder producer) {
    super(producer);
  }

  @Override
  public String getName() {
    return "Brier-score";
  }

  public static class Builder extends AbstractMeasure.Builder {

    public Builder(Vector domain) {
      super(domain);
    }

    @Override
    public void compute(Sample sample, Predictor predictor, Vector predicted,
        DoubleMatrix probabilities, Vector truth) {
      Check.size(predicted.size(), truth.size());
      Check.size(truth.size(), probabilities.rows());

      double brier = 0;
      for (int i = 0; i < predicted.size(); i++) {
        String label = predicted.getAsString(i);
        double prob = probabilities.get(i, Vectors.find(predictor.getClasses(), label));
        if (truth.getAsString(i).equals(label)) {
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

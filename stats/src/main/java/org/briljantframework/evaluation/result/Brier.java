package org.briljantframework.evaluation.result;

import static org.briljantframework.vector.Vectors.find;

import org.briljantframework.Check;
import org.briljantframework.classification.Predictor;
import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.matrix.DoubleMatrix;
import org.briljantframework.vector.Vector;

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
    public void compute(Sample sample, Predictor predictor, DataFrame dataFrame, Vector predicted,
        DoubleMatrix probabilities, Vector truth) {
      Check.size(predicted.size(), truth.size());
      Check.size(truth.size(), probabilities.rows());
      Vector classes = predictor.getClasses();

      double brier = 0;
      for (int i = 0; i < predicted.size(); i++) {
        String pred = predicted.getAsString(i);
        String actual = truth.getAsString(i);
        double prob = probabilities.get(i, find(classes, pred));
        if (actual.equals(pred)) {
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

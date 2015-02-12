package org.briljantframework.evaluation.result;

import org.briljantframework.classification.Predictor;
import org.briljantframework.matrix.DoubleMatrix;
import org.briljantframework.vector.Vector;

import com.google.common.base.Preconditions;

/**
 * Error rate, i.e. miss-classification rate, i.e. fraction of errors.
 *
 * @author Isak Karlsson
 */
public class ErrorRate extends AbstractMeasure {

  protected ErrorRate(AbstractMeasure.Builder builder) {
    super(builder);
  }

  @Override
  public String getName() {
    return "Error";
  }

  @Override
  public int compareTo(Measure other) {
    return Double.compare(getAverage(), other.getAverage());
  }

  public static class Builder extends AbstractMeasure.Builder {

    public Builder(Vector domain) {
      super(domain);
    }

    @Override
    public void compute(Sample sample, Predictor predictor, Vector predicted,
        DoubleMatrix probabilities, Vector truth) {
      Preconditions.checkArgument(predicted.size() == truth.size());

      double accuracy = 0.0;
      for (int i = 0; i < predicted.size(); i++) {
        if (predicted.getAsString(i).equals(truth.getAsString(i))) {
          accuracy++;
        }
      }

      addComputedValue(sample, 1 - (accuracy / predicted.size()));
    }

    @Override
    public Measure build() {
      return new ErrorRate(this);
    }
  }
}

package org.briljantframework.evaluation.result;

import org.briljantframework.classification.Predictor;
import org.briljantframework.dataframe.DataFrame;
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
    return Double.compare(getMean(), other.getMean());
  }

  public static class Builder extends AbstractMeasure.Builder {

    public Builder() {
      super(null);
    }

    @Override
    public void compute(Sample sample, Predictor predictor, DataFrame dataFrame, Vector predicted,
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

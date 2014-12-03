package org.briljantframework.evaluation.result;

import org.briljantframework.classification.Predictions;
import org.briljantframework.vector.Vector;

import com.google.common.base.Preconditions;

/**
 * Error rate, i.e. miss-classification rate, i.e. fraction of errors.
 *
 * @author Isak Karlsson
 */
public class ErrorRate extends AbstractMeasure {

  /**
   * Instantiates a new Error.
   *
   * @param builder the producer
   */
  protected ErrorRate(AbstractMeasure.Builder builder) {
    super(builder);
  }

  /**
   * The constant FACTORY.
   */
  public static Factory getFactory() {
    return Builder::new;
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

    @Override
    public void compute(Sample sample, Predictions predictions, Vector truth) {
      Preconditions.checkArgument(predictions.size() == truth.size());

      double accuracy = 0.0;
      for (int i = 0; i < predictions.size(); i++) {
        if (predictions.get(i).getPredictedValue().equals(truth.getAsString(i))) {
          accuracy++;
        }
      }

      addComputedValue(sample, 1 - (accuracy / predictions.size()));
    }

    @Override
    public Measure build() {
      return new ErrorRate(this);
    }
  }
}

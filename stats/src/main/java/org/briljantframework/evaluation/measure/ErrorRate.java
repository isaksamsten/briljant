package org.briljantframework.evaluation.measure;

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

  public static class Builder extends AbstractMeasure.Builder<ErrorRate> {

    public Builder() {
      super();
    }

    // @Override
    // public void compute(Sample sample, Predictor predictor, DataFrame dataFrame, Vector
    // predicted,
    // DoubleMatrix probabilities, Vector truth) {
    // Preconditions.checkArgument(predicted.size() == truth.size());
    //
    // double accuracy = 0.0;
    // for (int i = 0; i < predicted.size(); i++) {
    // if (predicted.getAsString(i).equals(truth.getAsString(i))) {
    // accuracy++;
    // }
    // }
    //
    // addComputedValue(sample, 1 - (accuracy / predicted.size()));
    // }

    @Override
    public ErrorRate build() {
      return new ErrorRate(this);
    }
  }
}

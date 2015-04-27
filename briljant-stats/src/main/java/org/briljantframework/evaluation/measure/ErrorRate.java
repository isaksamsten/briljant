package org.briljantframework.evaluation.measure;

/**
 * Error rate, i.e. miss-classification rate, i.e. fraction of errors.
 *
 * @author Isak Karlsson
 */
public class ErrorRate extends AbstractMeasure {

  protected ErrorRate(AbstractMeasure.Builder<? extends Measure> builder) {
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

    @Override
    public ErrorRate build() {
      return new ErrorRate(this);
    }
  }
}

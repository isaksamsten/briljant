package org.briljantframework.evaluation.measure;

/**
 * @author Isak Karlsson
 */
public class FitTime extends AbstractMeasure {
  protected FitTime(Builder builder) {
    super(builder);
  }

  @Override
  public String getName() {
    return "Fit-time";
  }

  public static class Builder extends AbstractMeasure.Builder<FitTime> {

    @Override
    public FitTime build() {
      return new FitTime(this);
    }
  }
}

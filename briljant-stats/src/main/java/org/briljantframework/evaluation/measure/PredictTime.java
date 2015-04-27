package org.briljantframework.evaluation.measure;

/**
 * @author Isak Karlsson
 */
public class PredictTime extends AbstractMeasure {
  protected PredictTime(Builder builder) {
    super(builder);
  }

  @Override
  public String getName() {
    return "Predict-time";
  }

  public static class Builder extends AbstractMeasure.Builder<PredictTime> {

    @Override
    public PredictTime build() {
      return new PredictTime(this);
    }
  }
}

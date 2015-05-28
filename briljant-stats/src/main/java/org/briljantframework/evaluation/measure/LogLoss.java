package org.briljantframework.evaluation.measure;

/**
 * Created by isak on 27/05/15.
 */
public class LogLoss extends AbstractMeasure {

  protected LogLoss(Builder builder) {
    super(builder);
  }

  @Override
  public String getName() {
    return "LogLoss";
  }

  public static class Builder extends AbstractMeasure.Builder<LogLoss> {

    @Override
    public LogLoss build() {
      return new LogLoss(this);
    }
  }
}

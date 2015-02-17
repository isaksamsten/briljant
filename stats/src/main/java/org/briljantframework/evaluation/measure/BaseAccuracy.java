package org.briljantframework.evaluation.measure;

/**
 * @author Isak Karlsson
 */
public class BaseAccuracy extends AbstractMeasure {

  protected BaseAccuracy(AbstractMeasure.Builder<? extends Measure> builder) {
    super(builder);
  }

  @Override
  public String getName() {
    return "Base classifier accuracy";
  }

  public static final class Builder extends AbstractMeasure.Builder<BaseAccuracy> {

    @Override
    public BaseAccuracy build() {
      return new BaseAccuracy(this);
    }
  }
}

package org.briljantframework.evaluation.measure;

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

  public static class Builder extends AbstractMeasure.Builder<Brier> {

    public Builder() {
      super();
    }

    @Override
    public Brier build() {
      return new Brier(this);
    }
  }

}

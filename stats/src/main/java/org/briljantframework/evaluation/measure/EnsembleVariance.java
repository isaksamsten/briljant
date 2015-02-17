package org.briljantframework.evaluation.measure;

/**
 * @author Isak Karlsson
 */
public class EnsembleVariance extends AbstractMeasure {
  protected EnsembleVariance(Builder builder) {
    super(builder);
  }

  @Override
  public String getName() {
    return "Ensemble Variance";
  }

  public static class Builder extends AbstractMeasure.Builder<EnsembleVariance> {
    @Override
    public EnsembleVariance build() {
      return new EnsembleVariance(this);
    }
  }
}

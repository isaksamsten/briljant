package org.briljantframework.evaluation.measure;

/**
 * @author Isak Karlsson
 */
public class EnsembleBias extends AbstractMeasure {

  protected EnsembleBias(AbstractMeasure.Builder<? extends Measure> builder) {
    super(builder);
  }

  @Override
  public String getName() {
    return "Ensemble Bias";
  }

  public static class Builder extends AbstractMeasure.Builder<EnsembleBias> {

    @Override
    public EnsembleBias build() {
      return new EnsembleBias(this);
    }
  }
}

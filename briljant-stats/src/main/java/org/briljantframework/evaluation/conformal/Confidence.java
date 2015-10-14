package org.briljantframework.evaluation.conformal;

import org.briljantframework.evaluation.PointMeasure;

/**
 * @author Isak Karlsson <isak-kar@dsv.su.se>
 */
public final class Confidence extends PointMeasure {

  private Confidence(Builder producer) {
    super(producer);
  }

  @Override
  public String getName() {
    return "Confidence";
  }

  public static final class Builder extends PointMeasure.Builder<Confidence> {
    @Override
    public Confidence build() {
      return new Confidence(this);
    }
  }
}

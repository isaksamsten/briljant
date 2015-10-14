package org.briljantframework.evaluation.conformal;

import org.briljantframework.evaluation.PointMeasure;

/**
 * @author Isak Karlsson <isak-kar@dsv.su.se>
 */
public final class Credibility extends PointMeasure {

  private Credibility(Builder producer) {
    super(producer);
  }

  @Override
  public String getName() {
    return "Credibility";
  }

  public static final class Builder extends PointMeasure.Builder<Credibility> {
    @Override
    public Credibility build() {
      return new Credibility(this);
    }
  }
}

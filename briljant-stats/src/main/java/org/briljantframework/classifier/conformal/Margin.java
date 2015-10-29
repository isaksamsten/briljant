package org.briljantframework.classifier.conformal;

import java.util.Objects;

import org.briljantframework.Check;
import org.briljantframework.array.DoubleArray;

/**
 * @author Isak Karlsson <isak-kar@dsv.su.se>
 */
public class Margin implements ClassificationCostFunction {

  @Override
  public double apply(DoubleArray score, int yIndex) {
    Objects.requireNonNull(score, "Require predictions.");
    Check.argument(score.size() > yIndex && yIndex >= 0, "Illegal true class index");
    return 0.5 - (score.get(yIndex) - maxnot(score, yIndex)) / 2;
  }

  private double maxnot(DoubleArray array, int not) {
    Double max = Double.NEGATIVE_INFINITY;
    for (int i = 0; i < array.size(); i++) {
      if (i == not) {
        continue;
      }
      double m = array.get(i);
      if (m > max) {
        max = m;
      }
    }
    return max;
  }
}

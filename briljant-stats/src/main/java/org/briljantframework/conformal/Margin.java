package org.briljantframework.conformal;

import java.util.Objects;

import org.briljantframework.Check;
import org.briljantframework.array.DoubleArray;
import org.briljantframework.data.vector.Vector;

/**
 * @author Isak Karlsson <isak-kar@dsv.su.se>
 */
public class Margin implements ClassificationErrorFunction {

  @Override
  public double apply(DoubleArray prediction, Object label, Vector classes) {
    Objects.requireNonNull(prediction, "Require predictions.");
    Objects.requireNonNull(classes, "Require possible class values.");
    Check.argument(prediction.size() == classes.size(),
        "The size of prediction array and classes don't match");
    int yIndex = classes.loc().indexOf(label);
    if (yIndex < 0) {
      throw new IllegalArgumentException(String.format("Illegal class value: '%s' (not found)",
          label));
    }
    return 0.5 - (prediction.get(yIndex) - maxnot(prediction, yIndex)) / 2;
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

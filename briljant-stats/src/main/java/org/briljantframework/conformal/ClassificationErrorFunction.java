package org.briljantframework.conformal;

import org.briljantframework.Check;
import org.briljantframework.array.Arrays;
import org.briljantframework.array.DoubleArray;
import org.briljantframework.data.vector.Vector;
import org.briljantframework.data.vector.Vectors;

/**
 * @author Isak Karlsson <isak-kar@dsv.su.se>
 */
@FunctionalInterface
public interface ClassificationErrorFunction {

  default DoubleArray apply(DoubleArray predictions, Vector y, Vector classes) {
    Check.argument(classes.size() == predictions.columns(), "Illegal prediction matrix");
    DoubleArray probabilities = Arrays.newDoubleArray(y.size());
    for (int i = 0, size = y.size(); i < size; i++) {
      int yIndex = Vectors.find(classes, y, i);
      if (yIndex < 0) {
        throw new IllegalArgumentException(String.format("Illegal class value: '%s' (not found)", y
            .loc().get(i)));
      }
      double value = apply(predictions.getRow(i), y.loc().get(Object.class, i), classes);
      probabilities.set(i, value);
    }

    return probabilities;
  }

  double apply(DoubleArray prediction, Object label, Vector classes);
}

package org.briljantframework.dataframe.transform;

import org.briljantframework.Check;
import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.matrix.DoubleMatrix;
import org.briljantframework.vector.DoubleVector;
import org.briljantframework.vector.Is;

/**
 * @author Isak Karlsson
 */
public class MinMaxNormalization implements Transformation {

  private final DoubleMatrix min, max;

  public MinMaxNormalization(DoubleMatrix min, DoubleMatrix max) {
    Check.size(min, max);
    this.min = min;
    this.max = max;
  }

  @Override
  public DataFrame transform(DataFrame x) {
    Check.size(x.columns(), max);
    DataFrame.Builder builder = x.newBuilder();
    for (int j = 0; j < x.columns(); j++) {
      Check.requireType(DoubleVector.TYPE, x.getColumnType(j));

      double min = this.min.get(j);
      double max = this.max.get(j);
      for (int i = 0; i < x.rows(); i++) {
        if (x.isNA(i, j) || isSane(min) || isSane(max)) {
          builder.setNA(i, j);
        } else {
          builder.set(i, j, (x.getAsDouble(i, j) - min) / (max - min));
        }
      }
    }
    return builder.build();
  }

  private boolean isSane(double value) {
    return !Is.NA(value) && !Double.isNaN(value) && !Double.isInfinite(value);
  }
}

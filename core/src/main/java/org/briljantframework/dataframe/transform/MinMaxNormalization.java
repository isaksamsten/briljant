package org.briljantframework.dataframe.transform;

import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.exceptions.TypeMismatchException;
import org.briljantframework.vector.DoubleVector;

import com.google.common.base.Preconditions;

/**
 * @author Isak Karlsson
 */
public class MinMaxNormalization implements Transformation {

  private final double[] min, max;

  public MinMaxNormalization(double[] min, double[] max) {
    Preconditions.checkArgument(min.length == max.length);
    this.min = min;
    this.max = max;
  }

  @Override
  public DataFrame transform(DataFrame x) {
    Preconditions.checkArgument(x.columns() == min.length);

    DataFrame.Builder builder = x.newCopyBuilder();
    for (int j = 0; j < x.columns(); j++) {
      if (x.getColumnType(j) != DoubleVector.TYPE) {
        throw new TypeMismatchException(DoubleVector.TYPE, x.getColumnType(j));
      }
      double min = this.min[j];
      double max = this.max[j];
      for (int i = 0; i < x.rows(); i++) {
        builder.set(i, j, (x.getAsDouble(i, j) - min) / (max - min));
      }

    }
    return builder.build();
  }
}

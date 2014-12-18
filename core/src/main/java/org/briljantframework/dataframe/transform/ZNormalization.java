package org.briljantframework.dataframe.transform;

import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.exceptions.TypeMismatchException;
import org.briljantframework.matrix.Matrix;
import org.briljantframework.vector.DoubleVector;

import com.google.common.base.Preconditions;

/**
 *
 */
public class ZNormalization implements Transformation {

  private final Matrix sigma;
  private final Matrix mean;

  public ZNormalization(Matrix mean, Matrix sigma) {
    this.mean = mean;
    this.sigma = sigma;
  }

  @Override
  public DataFrame transform(DataFrame x) {
    Preconditions.checkArgument(x.columns() == mean.size());

    DataFrame.Builder builder = x.newCopyBuilder();
    for (int j = 0; j < x.columns(); j++) {
      if (x.getColumnType(j) != DoubleVector.TYPE) {
        throw new TypeMismatchException(DoubleVector.TYPE, x.getColumnType(j));
      }
      double mean = this.mean.get(j);
      double sigma = this.sigma.get(j);
      for (int i = 0; i < x.rows(); i++) {
        builder.set(i, j, (x.getAsDouble(i, j) - mean) / sigma);
      }

    }
    return builder.build();

  }
}

package org.briljantframework.dataframe.transform;

import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.matrix.DoubleMatrix;

import com.google.common.base.Preconditions;

/**
 *
 */
public class ZNormalization implements Transformation {

  private final DoubleMatrix sigma;
  private final DoubleMatrix mean;

  public ZNormalization(DoubleMatrix mean, DoubleMatrix sigma) {
    this.mean = mean;
    this.sigma = sigma;
  }

  @Override
  public DataFrame transform(DataFrame x) {
    Preconditions.checkArgument(x.columns() == mean.size());

    DataFrame.Builder builder = x.newCopyBuilder();
    for (int j = 0; j < x.columns(); j++) {

      double mean = this.mean.get(j);
      double sigma = this.sigma.get(j);
      for (int i = 0; i < x.rows(); i++) {
        builder.set(i, j, (x.getAsDouble(i, j) - mean) / sigma);
      }

    }
    return builder.build();

  }
}

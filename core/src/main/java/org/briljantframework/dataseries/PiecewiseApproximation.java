package org.briljantframework.dataseries;

import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.dataframe.transform.Transformation;
import org.briljantframework.vector.DoubleVector;

import com.google.common.base.Preconditions;

/**
 * Created by Isak Karlsson on 16/12/14.
 */
public class PiecewiseApproximation implements Transformation {

  private final Resampler resampler;

  public PiecewiseApproximation(Resampler resampler) {
    this.resampler = Preconditions.checkNotNull(resampler, "Requires a resampler.");
  }

  @Override
  public DataFrame transform(DataFrame x) {
    DataSeriesCollection.Builder builder = new DataSeriesCollection.Builder(DoubleVector.TYPE);
    for (int i = 0; i < x.rows(); i++) {
      builder.addRow(resampler.mutableTransform(x.getRow(i)));
    }
    return builder.build();
  }
}

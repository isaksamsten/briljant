package org.briljantframework.dataseries;

import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.dataframe.transform.Transformation;
import org.briljantframework.vector.Vector;
import org.briljantframework.vector.Vectors;

/**
 * Created by Isak Karlsson on 16/12/14.
 */
public class DataSeriesNormalization implements Transformation {
  @Override
  public DataFrame transform(DataFrame x) {
    DataFrame.Builder builder = x.newCopyBuilder();
    for (int i = 0; i < x.rows(); i++) {
      Vector row = x.getRow(i);
      double mean = Vectors.mean(row);
      double sigma = Vectors.std(row, mean);
      for (int j = 0; j < x.columns(); j++) {
        double value = row.getAsDouble(j);
        builder.set(i, j, (value - mean) / sigma);
      }
    }

    return builder.build();
  }
}

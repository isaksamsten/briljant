package org.briljantframework.dataseries;

import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.dataframe.transform.Transformation;
import org.briljantframework.vector.Vector;
import org.briljantframework.vector.Vectors;

/**
 * <p>
 * Normalizes the rows of a {@link DataFrame} as opposed to the columns.
 * </p>
 * 
 * <p>
 * This implementation performs a z-normalization of each {@code row} in a data frame.
 * Z-normalization ensures that all rows has approximately zero mean and unit variance.
 * </p>
 * 
 * @author Isak Karlsson
 */
public class DataSeriesNormalization implements Transformation {

  /**
   * <p>
   * Performs the transformation.
   * </p>
   *
   * <ul>
   * <li>Requires that {@link Vector#getAsDouble(int)} returns a valid double</li>
   * <li>Cannot handle {@code NA} values</li>
   * <li>Cannot handle {@link Double#NaN}</li>
   * </ul>
   *
   * @param x data frame to transform
   * @return a new data frame with normalized rows
   */
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

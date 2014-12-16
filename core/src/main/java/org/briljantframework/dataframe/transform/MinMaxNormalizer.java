/*
 * ADEB - machine learning pipelines made easy Copyright (C) 2014 Isak Karlsson
 * 
 * This program is free software; you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program; if
 * not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 */

package org.briljantframework.dataframe.transform;

import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.dataframe.exceptions.TypeMismatchException;
import org.briljantframework.vector.DoubleVector;

import com.google.common.base.Preconditions;

/**
 * Class to fit a min max normalizer to a data frame. Calculate, for each column {@code j}, the min
 * <i>min</i><sub>j</sub> and max <i>max</i><sub>j</sub>. Then, for each value x<sub>i,j</sub> is
 * given by (x<sub>i,j</sub>-min<sub>j</sub>)/(max<sub>j</sub> - min<sub>j</sub>). This normalizes
 * the data frame in the range {@code [0, 1]} (under the assumption that min and max are
 * representative for the transformed dataframe).
 *
 * @author Isak Karlsson
 */
public class MinMaxNormalizer implements Transformer {

  @Override
  public Transformation fit(DataFrame frame) {
    double[] min = new double[frame.columns()];
    double[] max = new double[frame.columns()];

    for (int j = 0; j < frame.columns(); j++) {
      if (frame.getColumnType(j) != DoubleVector.TYPE) {
        throw new TypeMismatchException(DoubleVector.TYPE, frame.getColumnType(j));
      }

      double minTemp = Double.POSITIVE_INFINITY, maxTemp = Double.NEGATIVE_INFINITY;
      for (int i = 0; i < frame.rows(); i++) {
        double value = frame.getAsDouble(i, j);
        if (value > maxTemp) {
          maxTemp = value;
        }

        if (value < minTemp) {
          minTemp = value;
        }
      }

      min[j] = minTemp;
      max[j] = maxTemp;
    }

    return new MinMaxNormalization(min, max);
  }

  /**
   * @author Isak Karlsson
   */
  public static class MinMaxNormalization implements Transformation {

    private final double[] min, max;

    public MinMaxNormalization(double[] min, double[] max) {
      Preconditions.checkArgument(min.length == max.length);
      this.min = min;
      this.max = max;
    }

    @Override
    public DataFrame transform(DataFrame frame) {
      Preconditions.checkArgument(frame.columns() == min.length);

      DataFrame.Builder builder = frame.newCopyBuilder();
      for (int j = 0; j < frame.columns(); j++) {
        if (frame.getColumnType(j) != DoubleVector.TYPE) {
          throw new TypeMismatchException(DoubleVector.TYPE, frame.getColumnType(j));
        }
        double min = this.min[j];
        double max = this.max[j];
        for (int i = 0; i < frame.rows(); i++) {
          builder.set(i, j, (frame.getAsDouble(i, j) - min) / (max - min));
        }

      }
      return builder.build();
    }
  }
}

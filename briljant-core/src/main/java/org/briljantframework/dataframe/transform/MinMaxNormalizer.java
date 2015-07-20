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

import org.briljantframework.Bj;
import org.briljantframework.Check;
import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.exceptions.TypeMismatchException;
import org.briljantframework.array.DoubleArray;
import org.briljantframework.vector.DoubleVector;
import org.briljantframework.vector.Is;
import org.briljantframework.vector.Vec;

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
    DoubleArray min = Bj.doubleArray(frame.columns());
    DoubleArray max = Bj.doubleArray(frame.columns());
    for (int j = 0; j < frame.columns(); j++) {
      if (!frame.getType(j).equals(Vec.DOUBLE)) {
        throw new TypeMismatchException(Vec.DOUBLE, frame.getType(j));
      }

      double minTemp = Double.POSITIVE_INFINITY, maxTemp = Double.NEGATIVE_INFINITY;
      for (int i = 0; i < frame.rows(); i++) {
        double value = frame.getAsDouble(i, j);
        if (Is.NA(value)) {
          continue;
        }
        if (value > maxTemp) {
          maxTemp = value;
        }

        if (value < minTemp) {
          minTemp = value;
        }
      }

      min.set(j, minTemp);
      max.set(j, maxTemp);
    }

    return x -> {
      Check.size(x.columns(), max.size());
      DataFrame.Builder builder = x.newBuilder();
      for (int j = 0; j < x.columns(); j++) {
        Check.type(x.getType(j), DoubleVector.TYPE);

        double mi = min.get(j);
        double ma = max.get(j);
        for (int i = 0; i < x.rows(); i++) {
          if (x.isNA(i, j) || isSane(mi) || isSane(ma)) {
            builder.setNA(i, j);
          } else {
            builder.set(i, j, (x.getAsDouble(i, j) - mi) / (ma - mi));
          }
        }
      }
      return builder.build();
    };
  }

  private static boolean isSane(double value) {
    return !Is.NA(value) && !Double.isNaN(value) && !Double.isInfinite(value);
  }

}

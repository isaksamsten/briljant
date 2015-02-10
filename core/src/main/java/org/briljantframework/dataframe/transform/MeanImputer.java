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

import org.briljantframework.Check;
import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.matrix.DoubleMatrix;
import org.briljantframework.matrix.Matrices;
import org.briljantframework.vector.Is;
import org.briljantframework.vector.Vectors;

/**
 * @author Isak Karlsson
 */
public class MeanImputer implements Transformer {

  @Override
  public Transformation fit(DataFrame frame) {
    DoubleMatrix means = Matrices.newDoubleVector(frame.columns());

    for (int j = 0; j < frame.columns(); j++) {
      double mean = 0.0;
      int rows = 0;
      for (int i = 0; i < frame.rows(); i++) {
        double value = frame.getAsDouble(i, j);
        if (!Is.NA(value)) {
          mean += value;
          rows += 1;
        }
      }
      means.set(j, mean / rows);
    }

    return new MeanImputation(means);
  }

  private static class MeanImputation implements Transformation {

    private final DoubleMatrix means;

    private MeanImputation(DoubleMatrix means) {
      this.means = means;
    }

    @Override
    public DataFrame transform(DataFrame x) {
      Check.size(x.columns(), means);
      DataFrame.Builder builder = x.newBuilder();
      for (int j = 0; j < x.columns(); j++) {
        Check.requireType(Vectors.NUMERIC, x.getColumnType(j));
        for (int i = 0; i < x.rows(); i++) {
          if (x.isNA(i, j)) {
            builder.set(i, j, means.get(j));
          }
        }
      }
      return builder.build();
    }
  }


}

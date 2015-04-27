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
import org.briljantframework.matrix.api.MatrixFactory;
import org.briljantframework.matrix.netlib.NetlibMatrixFactory;
import org.briljantframework.vector.Vectors;

/**
 * @author Isak Karlsson
 */
public class MeanImputer implements Transformer {

  private final MatrixFactory bj = NetlibMatrixFactory.getInstance();

  @Override
  public Transformation fit(DataFrame frame) {
    DoubleMatrix means = bj.doubleVector(frame.columns());
    for (int j = 0; j < frame.columns(); j++) {
      means.set(j, Vectors.mean(frame.getColumn(j)));
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
      builder.getColumnNames().putAll(x.getColumnNames());
      for (int j = 0; j < x.columns(); j++) {
        Check.requireType(Vectors.DOUBLE, x.getColumnType(j));
        for (int i = 0; i < x.rows(); i++) {
          if (x.isNA(i, j)) {
            builder.set(i, j, means.get(j));
          } else {
            builder.set(i, j, x, i, j);
          }
        }
      }
      return builder.build();
    }
  }


}

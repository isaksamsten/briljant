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

package org.briljantframework.transform;

import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.matrix.Matrix;
import org.briljantframework.matrix.MismatchException;

/**
 * Created by Isak Karlsson on 12/08/14.
 */
public class MeanImputer implements Transformer {

  @Override
  public Transformation fit(DataFrame frame) {
    Matrix matrix = frame.asMatrix();
    double[] means = new double[matrix.columns()];

    for (int j = 0; j < matrix.columns(); j++) {
      double mean = 0.0;
      int rows = 0;
      for (int i = 0; i < matrix.rows(); i++) {
        double value = matrix.get(i, j);
        if (!Double.isNaN(value)) {
          mean += value;
          rows += 1;
        }
      }
      means[j] = mean / rows;
    }

    return new MeanImputation(means);
  }

  private static class MeanImputation implements Transformation {

    private final double[] means;

    private MeanImputation(double[] means) {
      this.means = means;
    }

    @Override
    public DataFrame transform(DataFrame frame) {
      if (frame.columns() != means.length) {
        throw new MismatchException("transform", "can't impute missing values for "
            + "matrix with shape %s using %d values", frame.asMatrix().getShape(), means.length);
      }
      // for (int j = 0; j < matrix.columns(); j++) {
      // for (int i = 0; i < matrix.rows(); i++) {
      // if (Double.isNaN(frame.get(i, j))) {
      // matrix.put(i, j, means[j]);
      // }
      // }
      // }
      // return copy;
      return null;
    }
  }


}

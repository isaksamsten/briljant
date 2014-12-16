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
import org.briljantframework.matrix.ArrayMatrix;
import org.briljantframework.matrix.Axis;
import org.briljantframework.matrix.Matrices;
import org.briljantframework.matrix.Matrix;
import org.briljantframework.vector.DoubleVector;

import com.google.common.base.Preconditions;

/**
 * Z normalization is also known as "Normalization to Zero Mean and Unit of Energy" first mentioned
 * by found in Goldin & Kanellakis. It ensures that all elements of the input vector are transformed
 * into the output vector whose mean is approximately 0 while the standard deviation are in a range
 * close to 1.
 * 
 * @author Isak Karlsson
 */
public class ZNormalizer implements Transformer {

  @Override
  public Transformation fit(DataFrame frame) {
    Matrix mean = Matrices.mean(frame.asMatrix(), Axis.COLUMN);

    Matrix x = frame.asMatrix();
    Matrix xNorm = new ArrayMatrix(x.getShape());

    for (int i = 0; i < xNorm.rows(); i++) {
      for (int j = 0; j < xNorm.columns(); j++) {
        xNorm.put(i, j, (x.get(i, j) - mean.get(j)));
      }
    }

    Matrix sigma = Matrices.std(xNorm, Axis.COLUMN);
    return new ZNormalization(mean, sigma);
  }

  private static class ZNormalization implements Transformation {

    private final Matrix sigma;
    private final Matrix mean;

    public ZNormalization(Matrix mean, Matrix sigma) {
      this.mean = mean;
      this.sigma = sigma;
    }

    @Override
    public DataFrame transform(DataFrame frame) {
      Preconditions.checkArgument(frame.columns() == mean.size());

      DataFrame.Builder builder = frame.newCopyBuilder();
      for (int j = 0; j < frame.columns(); j++) {
        if (frame.getColumnType(j) != DoubleVector.TYPE) {
          throw new TypeMismatchException(DoubleVector.TYPE, frame.getColumnType(j));
        }
        double mean = this.mean.get(j);
        double sigma = this.sigma.get(j);
        for (int i = 0; i < frame.rows(); i++) {
          builder.set(i, j, (frame.getAsDouble(i, j) - mean) / sigma);
        }

      }
      return builder.build();

    }
  }
}

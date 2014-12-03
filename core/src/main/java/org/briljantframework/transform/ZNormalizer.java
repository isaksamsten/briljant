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
import org.briljantframework.matrix.Axis;
import org.briljantframework.matrix.RealArrayMatrix;
import org.briljantframework.matrix.RealMatrices;
import org.briljantframework.matrix.RealMatrix;

/**
 * Z normalization is also known as "Normalization to Zero Mean and Unit of Energy" first mentioned
 * by found in Goldin & Kanellakis. It ensures that all elements of the input vector are transformed
 * into the output vector whose mean is approximately 0 while the standard deviation are in a range
 * close to 1.
 * <p>
 * This normalizer, normalizes the input frame "row-wise",
 * <p>
 * Created by Isak Karlsson on 26/09/14.
 */
public class ZNormalizer implements Transformer {

  @Override
  public Transformation fit(DataFrame frame) {
    RealMatrix mean = RealMatrices.mean(RealArrayMatrix::new, frame.asMatrix(), Axis.COLUMN);

    RealMatrix x = frame.asMatrix();
    RealMatrix xNorm = new RealArrayMatrix(x.getShape());

    for (int i = 0; i < xNorm.rows(); i++) {
      for (int j = 0; j < xNorm.columns(); j++) {
        xNorm.put(i, j, (x.get(i, j) - mean.get(j)));
      }
    }

    RealMatrix sigma = RealMatrices.std(RealArrayMatrix::new, xNorm, Axis.COLUMN);
    return new ZNormalization(mean, sigma);
  }

  private static class ZNormalization implements Transformation {

    private final RealMatrix sigma;
    private final RealMatrix mean;

    public ZNormalization(RealMatrix mean, RealMatrix sigma) {
      this.mean = mean;
      this.sigma = sigma;
    }

    @Override
    public DataFrame transform(DataFrame frame) {
      // E newFrame = copyTo.newEmptyDataset(frame);
      //
      // Matrix x = frame.asMatrix();
      // Matrix xNorm = newFrame.asMatrix();
      //
      // for (int i = 0; i < xNorm.rows(); i++) {
      // for (int j = 0; j < xNorm.columns(); j++) {
      // xNorm.put(i, j, (x.get(i, j) - mean.get(j)) / sigma.get(j));
      // }
      // }

      return null;
    }
  }
}

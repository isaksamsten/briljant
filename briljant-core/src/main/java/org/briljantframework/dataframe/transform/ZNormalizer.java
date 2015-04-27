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
import org.briljantframework.matrix.Dim;
import org.briljantframework.matrix.DoubleMatrix;
import org.briljantframework.matrix.Matrices;
import org.briljantframework.matrix.api.MatrixFactory;
import org.briljantframework.matrix.netlib.NetlibMatrixFactory;

/**
 * Z normalization is also known as "Normalization to Zero Mean and Unit of Energy" first mentioned
 * by found in Goldin & Kanellakis. It ensures that all elements of the input vector are transformed
 * into the output vector whose mean is approximately 0 while the standard deviation are in a range
 * close to 1.
 * 
 * @author Isak Karlsson
 */
public class ZNormalizer implements Transformer {

  private final MatrixFactory bj = NetlibMatrixFactory.getInstance();

  @Override
  public Transformation fit(DataFrame frame) {
    DoubleMatrix mean = Matrices.mean(frame.toMatrix().asDoubleMatrix(), Dim.C);

    DoubleMatrix x = frame.toMatrix().asDoubleMatrix();
    DoubleMatrix xNorm = bj.doubleMatrix(x.rows(), x.columns());

    for (int i = 0; i < xNorm.rows(); i++) {
      for (int j = 0; j < xNorm.columns(); j++) {
        xNorm.set(i, j, (x.get(i, j) - mean.get(j)));
      }
    }

    DoubleMatrix sigma = Matrices.std(xNorm, Dim.C);
    return new ZNormalization(mean, sigma);
  }

}

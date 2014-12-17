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
import org.briljantframework.linalg.LinearAlgebra;
import org.briljantframework.matrix.Matrix;

/**
 * Transforms a frame to it's inverse
 * <p>
 * Created by Isak Karlsson on 11/08/14.
 */
public class PseudoInverseTransformer implements Transformer {

  @Override
  public Transformation fit(DataFrame container) {
    return new PinvTransformation();
  }

  private static class PinvTransformation implements Transformation {
    @Override
    public DataFrame transform(DataFrame x) {
      Matrix matrix = LinearAlgebra.pinv(x.asMatrix());
      DataFrame.Builder builder = x.newBuilder();
      for (int j = 0; j < x.columns(); j++) {
        for (int i = 0; i < x.rows(); i++) {
          builder.set(i, j, matrix.get(i, j));
        }
      }
      return builder.build();
    }
  }
}

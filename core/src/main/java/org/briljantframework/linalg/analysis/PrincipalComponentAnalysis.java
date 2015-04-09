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

package org.briljantframework.linalg.analysis;

import org.briljantframework.Check;
import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.dataframe.transform.InvertibleTransformation;
import org.briljantframework.matrix.DoubleMatrix;
import org.briljantframework.vector.Vectors;

/**
 * Represents the
 *
 * @author Isak Karlsson
 */
public class PrincipalComponentAnalysis implements Analysis, InvertibleTransformation {

  private final DoubleMatrix u;
  private int components;

  public PrincipalComponentAnalysis(DoubleMatrix principalComponents, int components) {
    this.u = principalComponents;
    this.components = components;
  }

  private int components(DoubleMatrix matrix) {
    return this.components > 0 ? this.components : Math.min(matrix.rows(), matrix.columns());
  }

  public int getComponents() {
    return components;
  }

  public DoubleMatrix getU() {
    return u;
  }

  @Override
  public DataFrame inverseTransform(DataFrame x) {
    Check.all(x.getColumns(), col -> col.getType() == Vectors.DOUBLE && !col.hasNA());
    DoubleMatrix matrix = x.toMatrix().asDoubleMatrix();

    // Matrix m = frame.toMatrix();
    // E copy = factory.copyDataset(frame);
    // Types types = Types.range(NumericType::new, components(m));
    // Matrix original = Matrices.mmul(DenseMatrix::new, m, Transpose.NO,
    // u.getColumns(Range.exclusive(0, components(m))), Transpose.YES);
    //
    // copy.setMatrix(types, original);
    throw new UnsupportedOperationException();
  }

  @Override
  public DataFrame transform(DataFrame x) {
    Check.all(x.getColumns(), col -> col.getType().equals(Vectors.DOUBLE) && !col.hasNA());
    DoubleMatrix m = x.toMatrix().asDoubleMatrix();
    DoubleMatrix pca = m.mmul(u.getView(0, 0, m.rows(), components(m)));

    DataFrame.Builder result = x.newBuilder();
    for (int j = 0; j < pca.columns(); j++) {
      result.addColumnBuilder(Vectors.DOUBLE);
      result.getColumnNames().put(j, String.format("Component %d", j));
      for (int i = 0; i < pca.rows(); i++) {
        result.set(i, j, pca.get(i, j));
      }
    }
    return result.build();
  }
}

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
import org.briljantframework.linalg.decomposition.SingularValueDecomposer;
import org.briljantframework.linalg.decomposition.SingularValueDecomposition;
import org.briljantframework.matrix.DoubleArray;
import org.briljantframework.matrix.Op;
import org.briljantframework.vector.Vec;

/**
 * Principal component analysis (PCA) is a statistical procedure that uses an orthogonal
 * transformation to convert a set of observations of possibly correlated variables into a set of
 * values of linearly uncorrelated variables called principal components. The number of principal
 * components is less than or equal to the number of original variables. This transformation is
 * defined in such a way that the first principal component has the largest possible variance (that
 * is, accounts for as much of the variability in the data as possible), and each succeeding
 * component in turn has the highest variance possible under the constraint that it is orthogonal
 * to
 * (i.e., uncorrelated with) the preceding components. Principal components are guaranteed to be
 * independent if the data set is jointly normally distributed. PCA is sensitive to the relative
 * scaling of the original variables.
 * <p>
 *
 * @author Isak Karlsson
 */
public class PcaTransformer implements InvertibleTransformer {

  private final SingularValueDecomposer decomposer;
  private final int components;

  public PcaTransformer(SingularValueDecomposer decomposer, int components) {
    this.decomposer = decomposer;
    this.components = components;
  }

  public PcaTransformer(int components) {
    this(new SingularValueDecomposer(), components);
  }

  public PcaTransformer() {
    this(-1);
  }

  private SingularValueDecomposition getSingularValueDecomposition(DoubleArray m) {
    DoubleArray sigma = m.mmul(1, Op.TRANSPOSE, m, Op.KEEP).update(v -> v / m.rows());
    return decomposer.decompose(sigma);
  }

  @Override
  public InvertibleTransformation fit(DataFrame x) {
    Check.all(x.getColumns(), col -> col.getType().equals(Vec.DOUBLE) && !col.hasNA());
    SingularValueDecomposition svd = getSingularValueDecomposition(x.toMatrix().asDoubleMatrix());
    DoubleArray u = svd.getLeftSingularValues();
    return new InvertibleTransformation() {
      @Override
      public DataFrame inverseTransform(DataFrame x) {
        Check.all(x.getColumns(), col -> col.getType() == Vec.DOUBLE && !col.hasNA());
        DoubleArray matrix = x.toMatrix().asDoubleMatrix();

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
        Check.all(x.getColumns(), col -> col.getType().equals(Vec.DOUBLE) && !col.hasNA());
        DoubleArray m = x.toMatrix().asDoubleMatrix();
        DoubleArray pca = m.mmul(u.getView(0, 0, m.rows(), components(m)));

        DataFrame.Builder result = x.newBuilder();
        for (int j = 0; j < pca.columns(); j++) {
          result.addColumnBuilder(Vec.DOUBLE);
          // TODO
//          result.getColumnNames().put(j, String.format("Component %d", j));
          for (int i = 0; i < pca.rows(); i++) {
            result.set(i, j, pca.get(i, j));
          }
        }
        return result.build();
      }
    };
  }

  private int components(DoubleArray matrix) {
    return this.components > 0 ? this.components : Math.min(matrix.rows(), matrix.columns());
  }
}

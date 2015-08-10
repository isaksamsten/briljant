/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Isak Karlsson
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.briljantframework.dataframe.transform;

import org.briljantframework.Check;
import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.linalg.decomposition.SingularValueDecomposer;
import org.briljantframework.linalg.decomposition.SingularValueDecomposition;
import org.briljantframework.array.DoubleArray;
import org.briljantframework.array.Op;
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
    Check.all(col -> col.getType().equals(Vec.DOUBLE) && !col.hasNA(), x.getColumns());
    SingularValueDecomposition svd = getSingularValueDecomposition(x.toArray().asDouble());
    DoubleArray u = svd.getLeftSingularValues();
    return new InvertibleTransformation() {
      @Override
      public DataFrame inverseTransform(DataFrame x) {
        Check.all(col -> col.getType() == Vec.DOUBLE && !col.hasNA(), x.getColumns());
        DoubleArray matrix = x.toArray().asDouble();

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
        Check.all(col -> col.getType().equals(Vec.DOUBLE) && !col.hasNA(), x.getColumns());
        DoubleArray m = x.toArray().asDouble();
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

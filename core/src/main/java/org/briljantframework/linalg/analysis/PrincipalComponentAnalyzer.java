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
import org.briljantframework.dataframe.transform.InvertibleTransformer;
import org.briljantframework.linalg.decomposition.SingularValueDecomposer;
import org.briljantframework.linalg.decomposition.SingularValueDecomposition;
import org.briljantframework.matrix.DoubleMatrix;
import org.briljantframework.matrix.Transpose;
import org.briljantframework.vector.Vectors;

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
public class PrincipalComponentAnalyzer
    implements Analyzer<PrincipalComponentAnalysis>, InvertibleTransformer {

  private final SingularValueDecomposer decomposer;
  private final int components;

  public PrincipalComponentAnalyzer(SingularValueDecomposer decomposer, int components) {
    this.decomposer = decomposer;
    this.components = components;
  }

  public PrincipalComponentAnalyzer(int components) {
    this(new SingularValueDecomposer(), components);
  }

  /**
   * Instantiates a new Principal component analyzer.
   */
  public PrincipalComponentAnalyzer() {
    this(-1);
  }

  @Override
  public PrincipalComponentAnalysis analyze(DoubleMatrix array) {
    SingularValueDecomposition svd = getSingularValueDecomposition(array);
    return new PrincipalComponentAnalysis(svd.getLeftSingularValues(), components);
  }

  private SingularValueDecomposition getSingularValueDecomposition(DoubleMatrix m) {
    DoubleMatrix sigma = m.mmul(1, Transpose.YES, m, Transpose.NO).update(v -> v / m.rows());
    return decomposer.decompose(sigma);
  }

  @Override
  public InvertibleTransformation fit(DataFrame x) {
    Check.all(x.getColumns(), col -> col.getType().equals(Vectors.DOUBLE) && !col.hasNA());
    SingularValueDecomposition svd = getSingularValueDecomposition(x.toMatrix().asDoubleMatrix());
    return new PrincipalComponentAnalysis(svd.getLeftSingularValues(), components);
  }
}

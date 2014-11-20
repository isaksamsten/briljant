/*
 * ADEB - machine learning pipelines made easy
 * Copyright (C) 2014  Isak Karlsson
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.briljantframework.matrix.analysis;

import org.briljantframework.data.DataFrame;
import org.briljantframework.data.transform.InvertibleTransformation;
import org.briljantframework.data.types.NumericType;
import org.briljantframework.data.types.Types;
import org.briljantframework.matrix.DenseMatrix;
import org.briljantframework.matrix.Matrices;
import org.briljantframework.matrix.Matrix;
import org.briljantframework.matrix.Transpose;
import org.briljantframework.matrix.dataset.MatrixDataFrame;
import org.briljantframework.matrix.slice.Range;

/**
 * Created by Isak Karlsson on 24/06/14.
 */
public class PrincipalComponentAnalysis<E extends MatrixDataFrame> implements Analysis, InvertibleTransformation<E> {

    private final Matrix u;
    private int components;

    public PrincipalComponentAnalysis(Matrix principalComponents, int components) {
        this.u = principalComponents;
        this.components = components;
    }

    private int components(Matrix matrix) {
        return this.components > 0 ? this.components : Math.min(matrix.rows(), matrix.columns());
    }

    public int getComponents() {
        return components;
    }

    public Matrix getU() {
        return u;
    }

    @Override
    public E inverseTransform(E frame, DataFrame.CopyTo<E> factory) {
        Matrix m = frame.asMatrix();
        E copy = factory.copyDataset(frame);

        Types types = Types.range(NumericType::new, components(m));
        Matrix original = Matrices.mmul(DenseMatrix::new, m, Transpose.NO,
                u.getColumns(Range.exclusive(0, components(m))), Transpose.YES);

        copy.setMatrix(types, original);
        return copy;
    }

    @Override
    public E transform(E frame, DataFrame.CopyTo<E> factory) {
        Matrix m = frame.asMatrix();
        E copy = factory.copyDataset(frame);
        Types types = Types.range(NumericType::new, components(m));

        DenseMatrix pca = Matrices.mmul(DenseMatrix::new, copy, u.getColumns(Range.exclusive(0, components(m))));
        copy.setMatrix(types, pca);
        return copy;
    }
}

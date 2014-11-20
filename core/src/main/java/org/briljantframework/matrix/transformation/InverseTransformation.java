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

package org.briljantframework.matrix.transformation;

import org.briljantframework.data.DataFrame;
import org.briljantframework.data.transform.Transformation;
import org.briljantframework.matrix.DenseMatrix;
import org.briljantframework.matrix.Matrix;
import org.briljantframework.matrix.dataset.MatrixDataFrame;
import org.briljantframework.matrix.natives.BlasException;
import org.briljantframework.matrix.natives.Lapack;

/**
 * Created by Isak Karlsson on 11/08/14.
 */
public class InverseTransformation<E extends MatrixDataFrame> implements Transformation<E> {


    /**
     * Transform dense matrix.
     *
     * @param matrix the matrix
     * @return the dense matrix
     */
    public DenseMatrix transform(Matrix matrix) {
        DenseMatrix out = new DenseMatrix(matrix);
        invert(out);
        return out;
    }

    private void invert(Matrix out) {
        int n = out.rows();
        int[] ipiv = new int[n];
        int error;
        if ((error = Lapack.LAPACKE_dgetrf(Lapack.LAPACK_COL_MAJOR, n, n, out.toArray(), n, ipiv)) != 0) {
            throw new BlasException("LAPACKE_dgtref", error, "LU decomposition failed.");
        }

        if ((error = Lapack.LAPACKE_dgetri(Lapack.LAPACK_COL_MAJOR, n, out.toArray(), n, ipiv)) != 0) {
            throw new BlasException("LAPACKE_dgetri", error, "Inverse failed, the matrix is singular.");
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public E transform(E frame, DataFrame.CopyTo<E> copyTo) {
        if (!frame.isSquare()) {
            throw new IllegalArgumentException("Square matrix is required.");
        }

        E out = copyTo.copyDataset(frame);
        invert(out.asMatrix());

        return out;
    }
}

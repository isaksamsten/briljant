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

package org.briljantframework.matrix.decomposition;

import org.briljantframework.matrix.DenseMatrix;
import org.briljantframework.matrix.Matrix;
import org.briljantframework.matrix.MatrixLike;
import org.briljantframework.matrix.natives.BlasException;

import static org.briljantframework.matrix.natives.Lapack.LAPACKE_dgetrf;
import static org.briljantframework.matrix.natives.Lapack.LAPACK_COL_MAJOR;

/**
 * Created by Isak Karlsson on 11/08/14.
 */
public class LuDecomposer implements Decomposer<LuDecomposition> {
    @Override
    public LuDecomposition decompose(MatrixLike matrix) {
        int m = matrix.rows(), n = matrix.columns();
        int[] pivots = new int[Math.min(m, n)];

        Matrix lu = new DenseMatrix(matrix);
        int error;
        if ((error = LAPACKE_dgetrf(LAPACK_COL_MAJOR, m, n, lu.toArray(), m, pivots)) != 0) {
            throw new BlasException("LAPACKE_dgetrf", error, "LU decomposition failed.");
        }

        return new LuDecomposition(lu, pivots);
    }
}

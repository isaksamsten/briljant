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

package org.briljantframework.matrix.solve;

import com.sun.jna.ptr.IntByReference;
import org.briljantframework.matrix.DenseMatrix;
import org.briljantframework.matrix.Matrix;
import org.briljantframework.matrix.natives.BlasException;

import static org.briljantframework.matrix.natives.Lapack.LAPACKE_dgelsy;
import static org.briljantframework.matrix.natives.Lapack.LAPACK_COL_MAJOR;

/**
 * Solve LLS using complete orthogonal factorization
 * <p>
 * Created by Isak Karlsson on 08/09/14.
 */
public class LeastLinearSquaresSolver extends AbstractSolver {

    /**
     * Instantiates a new Least linear squares solver.
     *
     * @param matrix the matrix
     */
    public LeastLinearSquaresSolver(Matrix matrix) {
        super(matrix);
    }

    @Override
    public Matrix solve(Matrix b) {
        // TODO(isak): check correct columns

        int m = matrix.rows(), n = matrix.columns(), nrhs = b.columns();
        int[] jpvt = new int[n];

        double[] result = new double[m];
        System.arraycopy(b.toArray(), 0, result, 0, result.length);

        IntByReference out = new IntByReference();
        int error;
        if ((error = LAPACKE_dgelsy(LAPACK_COL_MAJOR, m, n, nrhs, matrix.copy().toArray(),
                m, result, m, jpvt, 0.01, out)) != 0) {
            throw new BlasException("LAPAKE_dgelsy", error, "failed to solve equation");
        }
        double[] array = new double[n];
        System.arraycopy(result, 0, array, 0, n);
        return DenseMatrix.rowVector(array);
    }

}

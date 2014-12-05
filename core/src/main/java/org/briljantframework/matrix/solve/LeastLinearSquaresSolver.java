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

package org.briljantframework.matrix.solve;

import java.util.Arrays;

import org.briljantframework.exception.BlasException;
import org.briljantframework.matrix.ArrayMatrix;
import org.briljantframework.matrix.Matrix;
import org.netlib.util.intW;

import com.github.fommil.netlib.LAPACK;

/**
 * Solve LLS using complete orthogonal factorization
 * <p>
 * Created by Isak Karlsson on 08/09/14.
 */
public class LeastLinearSquaresSolver extends AbstractSolver {

  public static final LAPACK lapack = LAPACK.getInstance();

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
    int m = matrix.rows(), n = matrix.columns(), nrhs = b.columns();
    int[] jpvt = new int[n];

    double[] result = b.asDoubleArray().clone();

    int lwork = -1;
    double[] work = new double[1];
    double[] a = matrix.copy().asDoubleArray();

    intW rank = new intW(0), info = new intW(0);
    lapack.dgelsy(m, n, nrhs, a, m, result, m, jpvt, 0.01, rank, work, lwork, info);
    if (info.val != 0) {
      throw new BlasException("dgelsy", info.val, "failed to query work");
    }

    lwork = (int) work[0];
    work = new double[lwork];
    lapack.dgelsy(m, n, nrhs, a, m, result, m, jpvt, 0.01, rank, work, lwork, info);
    if (info.val != 0) {
      throw new BlasException("dgelsy", info.val, "fail");
    }

    double[] array = Arrays.copyOf(result, n);
    return ArrayMatrix.rowVector(array);
  }
}

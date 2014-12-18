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

package org.briljantframework.linalg.solve;

import java.util.Arrays;

import org.briljantframework.exceptions.BlasException;
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


    Matrix result = b.copy();

    int lwork = -1;
    double[] work = new double[1];

    // TODO(isak): make decision based on isArrayBased()
    Matrix aCopy = matrix.copy();


    intW rank = new intW(0), info = new intW(0);
    double[] aCopyArray = aCopy.asDoubleArray();
    double[] resultArray = result.asDoubleArray();
    lapack.dgelsy(m, n, nrhs, aCopyArray, m, resultArray, m, jpvt, 0.01, rank, work, lwork, info);
    if (info.val != 0) {
      throw new BlasException("dgelsy", info.val, "failed to query work");
    }

    lwork = (int) work[0];
    work = new double[lwork];
    final int finalLwork1 = lwork;
    final double[] finalWork1 = work;
    lapack.dgelsy(m, n, nrhs, aCopyArray, m, resultArray, m, jpvt, 0.01, rank, finalWork1,
        finalLwork1, info);

    if (info.val != 0) {
      throw new BlasException("dgelsy", info.val, "fail");
    }

    double[] array = Arrays.copyOf(resultArray, n);
    // ArrayMatrix r = new ArrayMatrix(1, n);
    // for (int i = 0; i < n; i++) {
    // r.put(i, result.get(i));
    // }

    return ArrayMatrix.columnVector(array);
  }
}

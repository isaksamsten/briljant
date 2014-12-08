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

package org.briljantframework.matrix.decomposition;

import java.util.Optional;

import org.briljantframework.exception.BlasException;
import org.briljantframework.matrix.ArrayMatrix;
import org.briljantframework.matrix.Matrices;
import org.briljantframework.matrix.Matrix;
import org.netlib.util.intW;

import com.github.fommil.netlib.LAPACK;

/**
 * Created by isak on 02/07/14.
 */
public class LuDecomposition implements Decomposition {

  private final Matrix lu;
  private final int[] pivots;
  private Optional<Boolean> nonSingular = Optional.empty();
  private Optional<Matrix> lower = Optional.empty();
  private Optional<Matrix> upper = Optional.empty();

  private double det = Double.NaN;

  /**
   * Instantiates a new Lu decomposition.
   *
   * @param lu the lu
   * @param pivots the pivots
   */
  public LuDecomposition(Matrix lu, int[] pivots) {
    this.lu = lu;
    this.pivots = pivots;
  }

  /**
   * Decomposition matrix.
   *
   * @return the matrix
   */
  public Matrix decomposition() {
    return lu;
  }

  /**
   * Inverse matrix.
   *
   * @return the inverse of the matrix
   */
  public Matrix inverse() {
    if (!lu.isSquare()) {
      throw new IllegalStateException("Matrix must be square.");
    }
    Matrix inv = lu.copy();
    int n = inv.rows(), error;
    int lwork = -1;
    double[] work = new double[1];
    intW err = new intW(0);
    final int finalLwork = lwork;
    final double[] finalWork = work;
    inv.unsafe(x -> LAPACK.getInstance().dgetri(n, x, n, pivots, finalWork, finalLwork, err));
    if (err.val != 0) {
      throw new BlasException("LAPACKE_dgetri", err.val, "Querying failed");
    }

    lwork = (int) work[0];
    work = new double[lwork];
    final double[] finalWork1 = work;
    final int finalLwork1 = lwork;
    inv.unsafe(x -> LAPACK.getInstance().dgetri(n, x, n, pivots, finalWork1, finalLwork1, err));
    if (err.val != 0) {
      throw new BlasException("LAPACKE_dgetri", err.val, "Inverse failed.");
    }

    return inv;
  }

  /**
   * Gets determinant.
   *
   * @return the determinant
   */
  public double getDeterminant() {
    if (Double.isNaN(det)) {
      if (!lu.isSquare()) {
        throw new IllegalStateException("Matrix must be square.");
      }

      double det = 1;
      int[] pivots = getPivot();
      for (int i = 0; i < lu.rows(); i++) {
        if (pivots[i] != i) {
          det = det * lu.get(i, i);
        } else {
          det = -det * lu.get(i, i);
        }

        this.det = det;
      }
    }
    return det;
  }

  /**
   * Is non singular.
   *
   * @return the boolean
   */
  public boolean isNonSingular() {
    if (!this.nonSingular.isPresent()) {
      if (!lu.isSquare()) {
        throw new IllegalStateException("Matrix must be square.");
      }

      boolean nonSingular = true;
      for (int i = 0; i < lu.rows(); i++) {
        if (lu.get(i, i) == 0) {
          nonSingular = false;
          break;
        }
      }
      this.nonSingular = Optional.of(nonSingular);
    }
    return this.nonSingular.orElse(false);
  }

  /**
   * Gets upper.
   *
   * @return the upper
   */
  public Matrix getUpper() {
    return upper.orElseGet(this::computeUpper);
  }

  private Matrix computeUpper() {
    ArrayMatrix upperMatrix = new ArrayMatrix(lu.rows(), lu.columns());
    for (int i = 0; i < lu.rows(); i++) {
      for (int j = i; j < lu.columns(); j++) {
        upperMatrix.put(i, j, lu.get(i, j));
      }
    }
    upper = Optional.of(upperMatrix);
    return upperMatrix;
  }

  /**
   * Gets lower.
   *
   * @return the lower
   */
  public Matrix getLower() {
    return lower.orElseGet(this::computeLower);
  }

  private Matrix computeLower() {
    Matrix lowerMatrix = Matrices.zeros(lu.rows(), lu.columns());
    for (int i = 0; i < lu.rows(); i++) {
      for (int j = i; j < lu.columns(); j++) {
        int ii = lu.rows() - 1 - i;
        int jj = lu.columns() - 1 - j;
        if (ii == jj) {
          lowerMatrix.put(i, jj, 1.0);
        } else {
          lowerMatrix.put(i, jj, lu.get(ii, jj));
        }
      }
    }
    lower = Optional.of(lowerMatrix);
    return lowerMatrix;
  }

  /**
   * Get pivot.
   *
   * @return the int [ ]
   */
  public int[] getPivot() {
    return pivots;
  }
}

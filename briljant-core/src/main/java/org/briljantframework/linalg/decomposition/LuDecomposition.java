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

package org.briljantframework.linalg.decomposition;

import org.briljantframework.Bj;
import org.briljantframework.matrix.DoubleMatrix;
import org.briljantframework.matrix.IntMatrix;
import org.briljantframework.matrix.netlib.NetlibLapackException;
import org.netlib.util.intW;

import java.util.Optional;

/**
 * Created by isak on 02/07/14.
 */
public class LuDecomposition {

  private final DoubleMatrix lu;
  private final IntMatrix pivots;
  private Optional<Boolean> nonSingular = Optional.empty();
  private Optional<DoubleMatrix> lower = Optional.empty();
  private Optional<DoubleMatrix> upper = Optional.empty();

  private double det = Double.NaN;

  /**
   * Instantiates a new Lu decomposition.
   *
   * @param lu     the lu
   * @param pivots the pivots
   */
  public LuDecomposition(DoubleMatrix lu, IntMatrix pivots) {
    this.lu = lu;
    this.pivots = pivots;
  }

  /**
   * Decomposition matrix.
   *
   * @return the matrix
   */
  public DoubleMatrix decomposition() {
    return lu;
  }

  /**
   * Inverse matrix.
   *
   * @return the inverse of the matrix
   */
  public DoubleMatrix inverse() {
    if (!lu.isSquare()) {
      throw new IllegalStateException("Matrix must be square.");
    }
//    DoubleMatrix inv = lu.copy();
    int n = lu.rows();
    int lwork = -1;
    double[] work = new double[1];
    intW err = new intW(0);
//    DoubleStorage invs = (DoubleStorage) inv.getStorage();
    double[] invs = lu.array();
//    LAPACK.getInstance().dgetri(n, invs, n, pivots, work, lwork, err);
    if (err.val != 0) {
      throw new NetlibLapackException(err.val, "Querying failed");
    }

    lwork = (int) work[0];
    work = new double[lwork];
    // TODO (implement in linalg)
//    LAPACK.getInstance().dgetri(n, invs, n, pivots, work, lwork, err);
    if (err.val != 0) {
      throw new NetlibLapackException(err.val, "Inverse failed.");
    }

    return Bj.matrix(invs).reshape(lu.rows(), lu.columns());
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
      IntMatrix pivots = getPivot();
      for (int i = 0; i < lu.rows(); i++) {
        if (pivots.get(i) != i) {
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
  public DoubleMatrix getUpper() {
    return upper.orElseGet(this::computeUpper);
  }

  private DoubleMatrix computeUpper() {
    DoubleMatrix upperMatrix = Bj.doubleMatrix(lu.rows(), lu.columns());
    for (int i = 0; i < lu.rows(); i++) {
      for (int j = i; j < lu.columns(); j++) {
        upperMatrix.set(i, j, lu.get(i, j));
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
  public DoubleMatrix getLower() {
    return lower.orElseGet(this::computeLower);
  }

  private DoubleMatrix computeLower() {
    DoubleMatrix lowerMatrix = Bj.doubleMatrix(lu.rows(), lu.columns());
    for (int i = 0; i < lu.rows(); i++) {
      for (int j = i; j < lu.columns(); j++) {
        int ii = lu.rows() - 1 - i;
        int jj = lu.columns() - 1 - j;
        if (ii == jj) {
          lowerMatrix.set(i, jj, 1.0);
        } else {
          lowerMatrix.set(i, jj, lu.get(ii, jj));
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
  public IntMatrix getPivot() {
    return pivots;
  }
}

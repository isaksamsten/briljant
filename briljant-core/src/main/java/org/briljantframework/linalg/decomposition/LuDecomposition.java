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
import org.briljantframework.array.DoubleArray;
import org.briljantframework.array.IntArray;
import org.briljantframework.array.netlib.NetlibLapackException;
import org.netlib.util.intW;

import java.util.Optional;

/**
 * Created by isak on 02/07/14.
 */
public class LuDecomposition {

  private final DoubleArray lu;
  private final IntArray pivots;
  private Optional<Boolean> nonSingular = Optional.empty();
  private Optional<DoubleArray> lower = Optional.empty();
  private Optional<DoubleArray> upper = Optional.empty();

  private double det = Double.NaN;

  /**
   * Instantiates a new Lu decomposition.
   *
   * @param lu     the lu
   * @param pivots the pivots
   */
  public LuDecomposition(DoubleArray lu, IntArray pivots) {
    this.lu = lu;
    this.pivots = pivots;
  }

  /**
   * Decomposition matrix.
   *
   * @return the matrix
   */
  public DoubleArray decomposition() {
    return lu;
  }

  /**
   * Inverse matrix.
   *
   * @return the inverse of the matrix
   */
  public DoubleArray inverse() {
    if (!lu.isSquare()) {
      throw new IllegalStateException("Matrix must be square.");
    }
//    DoubleMatrix inv = lu.copy();
    int n = lu.rows();
    int lwork = -1;
    double[] work = new double[1];
    intW err = new intW(0);
//    DoubleStorage invs = (DoubleStorage) inv.getStorage();
    double[] invs = lu.data();
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

    return Bj.array(invs).reshape(lu.rows(), lu.columns());
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
      IntArray pivots = getPivot();
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
  public DoubleArray getUpper() {
    return upper.orElseGet(this::computeUpper);
  }

  private DoubleArray computeUpper() {
    DoubleArray upperMatrix = Bj.doubleArray(lu.rows(), lu.columns());
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
  public DoubleArray getLower() {
    return lower.orElseGet(this::computeLower);
  }

  private DoubleArray computeLower() {
    DoubleArray lowerMatrix = Bj.doubleArray(lu.rows(), lu.columns());
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
  public IntArray getPivot() {
    return pivots;
  }
}

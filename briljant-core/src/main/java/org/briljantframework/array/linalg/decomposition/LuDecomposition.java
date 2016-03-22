/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Isak Karlsson
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.briljantframework.array.linalg.decomposition;

import java.util.Optional;

import org.briljantframework.array.Arrays;
import org.briljantframework.array.DoubleArray;
import org.briljantframework.array.IntArray;

/**
 * Represents the LUP-decomposition of a square matrix.
 *
 * @author Isak Karlsson
 */
public class LuDecomposition {

  // TODO: 02/12/15 require refactoring to remove optionals
  private final DoubleArray lu;
  private final IntArray pivots;
  private Optional<Boolean> nonSingular = Optional.empty();
  private Optional<DoubleArray> lower = Optional.empty();
  private Optional<DoubleArray> upper = Optional.empty();

  private double det = Double.NaN;

  public LuDecomposition(DoubleArray lu, IntArray pivots) {
    this.lu = lu;
    this.pivots = pivots;
  }

  public DoubleArray getDecomposition() {
    return lu;
  }

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

  public IntArray getPivot() {
    return pivots;
  }

  public DoubleArray getPermutation() {
    DoubleArray perm = Arrays.doubleArray(pivots.size(), pivots.size());
    for (int i = 0; i < pivots.size(); ++i) {
      perm.set(i, pivots.get(i), 1.0);
    }
    return perm;
  }

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

  public DoubleArray getUpper() {
    return upper.orElseGet(this::computeUpper);
  }

  private DoubleArray computeUpper() {
    DoubleArray upperMatrix = Arrays.doubleArray(lu.rows(), lu.columns());
    for (int i = 0; i < lu.rows(); i++) {
      for (int j = i; j < lu.columns(); j++) {
        upperMatrix.set(i, j, lu.get(i, j));
      }
    }
    upper = Optional.of(upperMatrix);
    return upperMatrix;
  }

  public DoubleArray getLower() {
    return lower.orElseGet(this::computeLower);
  }

  private DoubleArray computeLower() {
    DoubleArray lowerMatrix = Arrays.doubleArray(lu.rows(), lu.columns());
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
}

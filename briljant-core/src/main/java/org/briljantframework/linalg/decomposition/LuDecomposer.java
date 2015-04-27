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

import org.briljantframework.Briljant;
import org.briljantframework.matrix.DoubleMatrix;
import org.briljantframework.matrix.IntMatrix;

/**
 * @author Isak Karlsson
 */
public class LuDecomposer implements Decomposer<LuDecomposition> {

  @Override
  public LuDecomposition decompose(DoubleMatrix matrix) {
    int m = matrix.rows(), n = matrix.columns();
    IntMatrix pivots = Briljant.intVector(Math.min(m, n));
    DoubleMatrix lu = matrix.copy();
    Briljant.linalg.getrf(lu, pivots);
//    intW error = new intW(0);
//    LAPACK.getInstance()
//        .dgetrf(m, n, ((DoubleStorage) lu.getStorage()).doubleArray(), n, pivots, error);
//    if (error.val != 0) {
//      throw new BlasException("dgtref", error.val, "LU decomposition failed.");
//    }

    return new LuDecomposition(lu, pivots);
  }
}
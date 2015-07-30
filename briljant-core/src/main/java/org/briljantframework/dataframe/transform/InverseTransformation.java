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

package org.briljantframework.dataframe.transform;

import com.github.fommil.netlib.LAPACK;

import org.briljantframework.Bj;
import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.array.IntArray;
import org.briljantframework.array.DoubleArray;

/**
 * Created by Isak Karlsson on 11/08/14.
 */
public class InverseTransformation implements Transformation {


  protected static final LAPACK lapack = LAPACK.getInstance();

  /**
   * Transform dense matrix.
   *
   * @param matrix the matrix
   * @return the dense matrix
   */
  public DoubleArray transform(DoubleArray matrix) {
    return invert(matrix); // TODO(isak) refactor
  }

  private DoubleArray invert(DoubleArray in) {
    int n = in.rows();
    DoubleArray out = in.copy();
    IntArray ipiv = Bj.intArray(n);
    Bj.linalg.getrf(out, ipiv);
//    Bj.linalg.getri(out, ipiv);
//    int[] ipiv = new int[n];
//    intW error = new intW(0);
//    double[] outArray = in.array();
//    lapack.dgetrf(n, n, outArray, n, ipiv, error);
//    if (error.val != 0) {
//      throw new NetlibLapackException(error.val, "LU decomposition failed.");
//    }
//
//    double[] work = new double[1];
//    int lwork = -1;
//    lapack.dgetri(n, outArray, n, ipiv, work, lwork, error);
//
//    if (error.val != 0) {
//      throw new NetlibLapackException(error.val, "Query failed");
//    }
//
//    lwork = (int) work[0];
//    work = new double[lwork];
//    lapack.dgetri(n, outArray, n, ipiv, work, lwork, error);
//    if (error.val != 0) {
//      throw new NetlibLapackException(error.val, "Inverse failed. The matrix is singular.");
//    }

    return out;
  }

  @Override
  public DataFrame transform(DataFrame x) {
    // if (!frame.isSquare()) {
    // throw new IllegalArgumentException("Square matrix is required.");
    // }
    //
    // E out = copyTo.copyDataset(frame);
    // invert(out.toMatrix());
    //
    // return out;
    return null;
  }
}

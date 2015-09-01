/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Isak Karlsson
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.briljantframework.data.dataframe.transform;

import com.github.fommil.netlib.LAPACK;

import org.briljantframework.Bj;
import org.briljantframework.data.dataframe.DataFrame;
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

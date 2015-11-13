/*
 * The MIT License (MIT)
 * 
 * Copyright (c) 2015 Isak Karlsson
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

package org.briljantframework.linalg;

import org.briljantframework.array.Arrays;
import org.briljantframework.array.DoubleArray;
import org.briljantframework.linalg.decomposition.SingularValueDecomposition;

/**
 * Created by isak on 23/06/14.
 */
@Deprecated
public class LinearAlgebra {

  /**
   * The constant MACHINE_EPSILON.
   */
  public final static double MACHINE_EPSILON = Math.ulp(1);


  /**
   * In linear algebra, the rank of a matrix A is the size of the largest collection of linearly
   * independent columns of A (the column rank) or the size of the largest collection of linearly
   * independent rows of A (the row rank). For every matrix, the column rank is equal to the row
   * rank.[1] It is a measure of the "nondegenerateness" of the system of linear equations and
   * linear transformation encoded by A. There are multiple definitions of rank. The rank is one of
   * the fundamental pieces of data associated with a matrix.
   *
   * @param x a matrix
   * @return the rank
   */
  public static double rank(DoubleArray x) {
    SingularValueDecomposition svd = Arrays.linalg.svd(x);
    DoubleArray singular = svd.getDiagonal();
    // int rank = 0;
    // for (int i = 0; i < singular.diagonalSize(); i++) {
    // if (singular.getDiagonal(i) > 0) {
    // rank += 1;
    // }
    // }
    // return rank;
    return Double.NaN;
  }

}

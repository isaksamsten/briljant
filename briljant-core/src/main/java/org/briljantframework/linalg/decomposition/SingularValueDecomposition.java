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

package org.briljantframework.linalg.decomposition;

import org.briljantframework.array.DoubleArray;

/**
 * Created by isak on 23/06/14.
 */
public class SingularValueDecomposition {

  /**
   * \Sigma is an m×n rectangular diagonal matrix with nonnegative real numbers on the diagonal. The
   * diagonal entries \Sigma_{i,i} of \Sigma are known as the singular values of M
   */
  public final DoubleArray s;

  /**
   * U is a m×m real or complex unitary matrix
   */
  public final DoubleArray u;

  /**
   * V* (or simply the transpose of V if V is real) is an n×n real unitary matrix
   */
  public final DoubleArray v;

  /**
   * Instantiates a new Singular value decomposition.
   * 
   * @param s the s
   * @param u the u
   * @param v the v
   */
  public SingularValueDecomposition(DoubleArray s, DoubleArray u, DoubleArray v) {
    this.s = s;
    this.u = u;
    this.v = v;
  }

  /**
   * Gets diagonal.
   *
   * @return the diagonal
   */
  public DoubleArray getDiagonal() {
    return s;
  }

  /**
   * Gets left singular values.
   *
   * @return the left singular values
   */
  public DoubleArray getLeftSingularValues() {
    return u;
  }

  /**
   * Gets right singular values.
   *
   * @return the right singular values
   */
  public DoubleArray getRightSingularValues() {
    return v;
  }

  @Override
  public String toString() {
    StringBuilder builder =
        new StringBuilder("SingularValueDecomposition\n").append(u).append("\n").append(s)
            .append("\n").append(v).append("\n");

    return builder.toString();
  }
}

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

package org.briljantframework.linalg.api;

import java.util.Objects;

import org.briljantframework.array.DoubleArray;
import org.briljantframework.array.api.ArrayBackend;

/**
 * @author Isak Karlsson
 */
public abstract class AbstractLinearAlgebraRoutines implements LinearAlgebraRoutines {

  private final ArrayBackend arrayBackend;

  protected AbstractLinearAlgebraRoutines(ArrayBackend matrixFactory) {
    this.arrayBackend = Objects.requireNonNull(matrixFactory);
  }

  protected ArrayBackend getArrayBackend() {
    return arrayBackend;
  }

  /**
   * In linear algebra, the determinant is a value associated with a square matrix. It can be
   * computed from the entries of the matrix by a specific arithmetic expression, while other ways
   * to determine its value exist as well. The determinant provides important information about a
   * matrix of coefficients of a system of linear equations, or about a matrix that corresponds to a
   * linear transformation of a vector space.
   *
   * @param x a square mutable array
   * @return the determinant
   */
  @Override
  public double det(DoubleArray x) {
    if (x.isSquare()) {
      return lu(x).getDeterminant();
    } else {
      throw new IllegalArgumentException("argument must be a square array");
    }
  }
}

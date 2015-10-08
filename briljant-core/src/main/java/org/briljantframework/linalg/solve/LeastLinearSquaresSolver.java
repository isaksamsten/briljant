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

package org.briljantframework.linalg.solve;

import org.briljantframework.array.Arrays;
import org.briljantframework.array.DoubleArray;
import org.briljantframework.array.IntArray;

/**
 * Solve LLS using complete orthogonal factorization
 * <p>
 * Created by Isak Karlsson on 08/09/14.
 */
public class LeastLinearSquaresSolver extends AbstractSolver {

  /**
   * Instantiates a new Least linear squares solver.
   *
   * @param a the matrix
   */
  public LeastLinearSquaresSolver(DoubleArray a) {
    super(a);
  }

  @Override
  public DoubleArray solve(DoubleArray b) {
    DoubleArray aCopy = a.copy();
    DoubleArray bCopy = b.copy();
    IntArray jpvt = Arrays.intArray(a.columns());
    Arrays.linalg.gelsy(aCopy, bCopy, jpvt, 0.01);
    return bCopy;
  }
}

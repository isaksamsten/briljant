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

package org.briljantframework.linalg.solve;

import com.github.fommil.netlib.LAPACK;

import org.briljantframework.Bj;
import org.briljantframework.array.DoubleArray;
import org.briljantframework.array.IntArray;

//import org.briljantframework.matrix.DefaultDoubleMatrix;

/**
 * Solve LLS using complete orthogonal factorization
 * <p>
 * Created by Isak Karlsson on 08/09/14.
 */
public class LeastLinearSquaresSolver extends AbstractSolver {

  public static final LAPACK lapack = LAPACK.getInstance();

  /**
   * Instantiates a new Least linear squares solver.
   *
   * @param matrix the matrix
   */
  public LeastLinearSquaresSolver(DoubleArray matrix) {
    super(matrix);
  }

  @Override
  public DoubleArray solve(DoubleArray b) {
    DoubleArray aCopy = a.copy();
    DoubleArray bCopy = b.copy();
    IntArray jpvt = Bj.intArray(a.columns());
    Bj.linalg.gelsy(aCopy, bCopy, jpvt, 0.01);
    return bCopy;
  }
}

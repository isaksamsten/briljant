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
import org.briljantframework.matrix.DoubleMatrix;
import org.briljantframework.matrix.IntMatrix;

/**
 * @author Isak Karlsson
 */
public class LuDecomposer {

  public LuDecomposition decompose(DoubleMatrix matrix) {
    int m = matrix.rows(), n = matrix.columns();
    IntMatrix pivots = Bj.intVector(Math.min(m, n));
    DoubleMatrix lu = matrix.copy();
    Bj.linalg.getrf(lu, pivots);
    return new LuDecomposition(lu, pivots);
  }
}

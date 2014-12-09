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

package org.briljantframework.matrix;

/**
 * Created by Isak Karlsson on 28/08/14.
 */
public interface VectorLike {

  /**
   * Get value at row i and column j
   *
   * @param i row
   * @param j column
   * @return value double
   */
  double get(int i, int j);

  /**
   * Flattens the traversal of the matrix in column-major order. If {@link #rows()} == 1 or
   * {@link #columns()} == 1, <code>get(index)</code> behaves as expected. If not, the matrix is
   * traversed in column-major order. For example, given the following matrix
   *
   * <pre>
   *     1 2 3
   *     4 5 6
   * </pre>
   *
   * this code
   * 
   * <pre>
   * for (int i = 0; i &lt; x.size(); i++) {
   *   System.out.print(x.get(i));
   * }
   * </pre>
   * 
   * prints
   * 
   * <pre>
   * 142536
   * </pre>
   *
   * @param index the index
   * @return the double
   */
  double get(int index);

  /**
   * Returns the linearized size of this matrix.
   *
   * <pre>
   * {@code rows() * columns() == @code size()}
   * </pre>
   *
   * @return the int
   */
  int size();
}

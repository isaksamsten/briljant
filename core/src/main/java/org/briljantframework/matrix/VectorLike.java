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
   * @return returns whether {@code this} is a {@link Axis#COLUMN} or {@link Axis#ROW} vector
   */
  Axis getAxis();

  /**
   * Flattens the traversal of the matrix in column-major order. The matrix is traversed in
   * column-major order. For example, given the following matrix
   * <p>
   * 
   * <pre>
   *     1 2 3
   *     4 5 6
   * </pre>
   * <p>
   * this code
   * <p>
   * 
   * <pre>
   * for (int i = 0; i &lt; x.size(); i++) {
   *   System.out.print(x.get(i));
   * }
   * </pre>
   * <p>
   * prints
   * <p>
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
   * <p>
   * 
   * <pre>
   * {@code rows() * columns() == @code size()}
   * </pre>
   *
   * @return the int
   */
  int size();
}

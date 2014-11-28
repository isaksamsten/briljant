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
 * Created by Isak Karlsson on 03/09/14.
 */
public class Shape {

  /**
   * The Rows.
   */
  public final int rows;
  /**
   * The Columns.
   */
  public final int columns;

  private Shape(int rows, int columns) {
    this.rows = rows;
    this.columns = columns;
  }

  /**
   * Of shape.
   *
   * @param rows the rows
   * @param cols the cols
   * @return the shape
   */
  public static Shape of(int rows, int cols) {
    return new Shape(rows, cols);
  }

  /**
   * Length int.
   *
   * @return the int
   */
  public int size() {
    return rows * columns;
  }

  public double[] getArrayOfShape() {
    return new double[size()];
  }

  @Override
  public int hashCode() {
    return rows + columns + 17;
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof Shape && ((Shape) obj).rows == rows && ((Shape) obj).columns == columns;
  }

  @Override
  public String toString() {
    return String.format("%dx%d", rows, columns);
  }
}

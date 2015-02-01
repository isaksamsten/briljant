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
public final class Shape {

  public final long rows, columns;

  private Shape(long rows, long columns) {
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
  public static Shape of(long rows, long cols) {
    return new Shape(rows, cols);
  }

  /**
   * Length int.
   *
   * @return the int
   */
  public long size() {
    return Math.multiplyExact(rows, columns);
  }

  public double[] getArrayOfShape() {
    return new double[(int) size()];
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;

    Shape shape = (Shape) o;

    if (columns != shape.columns)
      return false;
    if (rows != shape.rows)
      return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = (int) (rows ^ (rows >>> 32));
    result = 31 * result + (int) (columns ^ (columns >>> 32));
    return result;
  }

  @Override
  public String toString() {
    return String.format("%dx%d", rows, columns);
  }
}

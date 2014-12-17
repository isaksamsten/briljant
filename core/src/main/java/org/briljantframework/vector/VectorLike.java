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

package org.briljantframework.vector;

/**
 * Created by Isak Karlsson on 28/08/14.
 */
public interface VectorLike {

  public static VectorLike wrap(double... values) {
    return new VectorLike() {
      @Override
      public double get(int index) {
        return values[index];
      }

      @Override
      public int size() {
        return values.length;
      }
    };
  }

  /**
   * Get value at {@code index}
   * 
   * @param index the index
   * @return the double
   */
  double get(int index);

  default double getAsDouble(int index) {
    return get(index);
  }

  default int getAsInt(int index) {
    return (int) get(index);
  }

  default Binary getAsBinary(int index) {
    return Binary.valueOf(getAsInt(index));
  }

  default Complex getAsComplex(int index) {
    double value = get(index);
    return Double.isNaN(value) ? Complex.NaN : new Complex(value);
  }

  default String getAsString(int index) {
    return String.valueOf(get(index));
  }

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

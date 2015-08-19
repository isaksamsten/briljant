/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Isak Karlsson
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.briljantframework.index;

import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.vector.Vector;

/**
 * @author Isak Karlsson
 */
public interface DataFrameLocationGetter {

  /**
   * Get value at {@code row} and {@code column} as an instance of {@code T}. If conversion fails,
   * return {@code NA} as defined by {@link org.briljantframework.vector.Na#from(Class)}. The
   * conversion is performed according to the convention found in {@link
   * org.briljantframework.vector.Convert#to(Class, Object)}
   *
   * @param cls the class
   * @param r   the row
   * @param c   the column
   * @param <T> the type of the returned value
   * @return an instance of {@code T}
   */
  <T> T get(Class<T> cls, int r, int c);

  /**
   * Get value at {@code row} and {@code column} as {@code double}.
   *
   * @param r the row
   * @param c the column
   * @return the value
   */
  double getAsDouble(int r, int c);

  /**
   * Get value at {@code row} and {@code column} as {@code int}.
   *
   * @param r the row
   * @param c the column
   * @return the value
   */
  int getAsInt(int r, int c);

  /**
   * Returns string representation of value at {@code row, column}. In most cases this is
   * equivalent to {@code get(Object.class, row, column).toString()}, but it handles {@code NA}
   * values, i.e. the returned {@linkplain String string} is never {@code null}.
   *
   * @param r the row
   * @param c the column
   * @return the representation
   */
  String toString(int r, int c);

  /**
   * Returns true if value at {@code row, column} is {@code NA}.
   *
   * @param r the row
   * @param c the column
   * @return true or false
   */
  boolean isNA(int r, int c);

  Vector get(int c);

  DataFrame get(int... columns);

  DataFrame drop(int index);

  DataFrame drop(int... columns);

  Vector getRecord(int r);

  DataFrame getRecord(int... records);
}

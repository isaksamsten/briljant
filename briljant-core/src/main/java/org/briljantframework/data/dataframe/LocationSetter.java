/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Isak Karlsson
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
package org.briljantframework.data.dataframe;

import org.briljantframework.data.series.Series;

/**
 * Provides location based setting of values, records and columns.
 * 
 * @author Isak Karlsson
 * @see LocationGetter
 */
public interface LocationSetter {

  /**
   * Set the value at the specified position to <tt>NA</tt>. If the row or column position exceeds
   * the current number or rows or columns, the resulting data frame will be filled with appropriate
   * <tt>NA</tt> values.
   *
   * @param r the row
   * @param c the column
   */
  void setNA(int r, int c);

  /**
   * Set the value at the specified position. If the row or column position exceeds the current
   * number or rows or columns, the resulting data frame will be filled with appropriate <tt>NA</tt>
   * values.
   *
   * @param r the row
   * @param c the column
   * @param value the value
   */
  void set(int r, int c, Object value);

  /**
   * Set the value at the specified row and column position to the value from the specified row and
   * column in the given <tt>DataFrame</tt>. If the row or column position exceeds the current
   * number or rows or columns, the resulting data frame will be filled with appropriate <tt>NA</tt>
   * values.
   * 
   * @param tr the row
   * @param tc the column
   * @param df the series
   * @param fr the row
   * @param fc the column
   */
  void set(int tr, int tc, DataFrame df, int fr, int fc);

  /**
   * Set the value at the specified row and column position to the value from the specified position
   * in the given <tt>Series</tt>. If the row or column position exceeds the current number or rows
   * or columns, the resulting data frame will be filled with appropriate <tt>NA</tt> values.
   *
   * @param tr the row in this
   * @param tc the column in this
   * @param v the series
   * @param i the value in the supplied series indexer
   */
  void set(int tr, int tc, Series v, int i);

  /**
   * Set the column at the specified position in this data frame. If the specified position is
   * larger the the the current number of columns, empty columns (filled with <tt>NA</tt>) are added
   * in between.
   *
   * <p/>
   * Note that is not safe to call {@link Series.Builder#build()} on the supplied
   * <tt>Series.Builder</tt> after this method has finished.
   *
   * @param c the index {@code index < columns()}
   * @param columnBuilder the builder
   */
  void set(int c, Series.Builder columnBuilder);

  /**
   * Set the column at the specified position in this data frame. If the specified position is
   * larger the the the current number of columns, empty columns (filled with <tt>NA</tt>) are added
   * in between.
   *
   * @param c the position
   * @param column the column
   */
  default void set(int c, Series column) {
    set(c, column.newCopyBuilder());
  }

  /**
   * Removes the column at the specified position in this data frame builder. Shifts any subsequent
   * columns to the left (subtracts one from their indices).
   *
   * @param c the index
   */
  void remove(int c);

  /**
   * Set the column at the specified position in this data frame. If the specified position is
   * larger the the the current number of rows, empty rows (filled with <tt>NA</tt>) are added in
   * between.
   *
   * @param r the index
   * @param row the builder
   */
  void setRow(int r, Series.Builder row);

  /**
   * Set the column at the specified position in this data frame. If the specified position is
   * larger the the the current number of rows, empty rows (filled with <tt>NA</tt>) are added in
   * between.
   * 
   * @param pos the position
   * @param row the row
   */
  default void setRow(int pos, Series row) {
    setRow(pos, row.newCopyBuilder());
  }

  /**
   * Removes the row at the specified position in this data frame builder. Shifts any subsequent
   * rows up (subtracts one from their indices).
   *
   * @param pos the index
   */
  void removeRow(int pos);

  /**
   * Swaps column series {@code a} and {@code b}.
   *
   * @param a an index
   * @param b an index
   */
  void swap(int a, int b);

  /**
   * Swap row at index {@code a} with {@code b}.
   *
   * @param a the first row
   * @param b the second row
   */
  void swapRows(int a, int b);
}

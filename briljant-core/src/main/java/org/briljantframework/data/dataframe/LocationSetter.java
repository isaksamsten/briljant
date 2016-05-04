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
   * Set value at {@code row} in {@code column} to NA. If {@code column >= columns()} adds empty
   * {@link org.briljantframework.data.series.ObjectSeries} of all {@code NA} from {@code
   * columns()
   * ... column}.
   *
   * @param r the row
   * @param c the column
   */
  void setNA(int r, int c);

  /**
   * Set value at {@code row, column} to {@code value}. If {@code toCol >= columns()}, adds empty
   * {@link org.briljantframework.data.series.ObjectSeries} columns from {@code columns() ...
   * column
   * - 1}, inferring the type at {@code toCol} using {@code Vectors.getInstance(object)}
   *
   * @param r the row
   * @param c the column
   * @param value the value
   */
  void set(int r, int c, Object value);

  /**
   * Set value at {@code row, toCol} using the value at {@code fromRow, fromCol} in {@code from}. If
   * {@code toCol >= columns()}, adds empty {@link org.briljantframework.data.series.ObjectSeries} x
   * columns from {@code columns() ... column - 1}, inferring the type at {@code toCol} using
   * {@code from.getColumnType(fromCol)}
   *
   * @param tr the row
   * @param tc the column
   * @param df the series
   * @param fr the row
   * @param fc the column
   */
  void set(int tr, int tc, DataFrame df, int fr, int fc);

  /**
   * Add the value {@code fromRow} from {@code from} to {@code toCol} and {@code toRow}. If
   * {@code toCol >= columns()}, adds empty {@link org.briljantframework.data.series.ObjectSeries}
   * columns from {@code columns() ... column - 1}, inferring the type at {@code toCol} using
   * {@code from.getType(index)}
   *
   * @param tr the row in this
   * @param tc the column in this
   * @param v the series
   * @param i the value in the supplied series indexer
   */
  void set(int tr, int tc, Series v, int i);

  /**
   * Sets the column at {@code index} to {@code builder}. If {@code index >= columns()} adds empty
   * {@link org.briljantframework.data.series.ObjectSeries} columns from {@code columns()
   * ...
   * column - 1}. If {@code index < columns()} each column is shifted to the right.
   *
   * <p/>
   * Note that is not safe to call {@link Series.Builder#build()} after this method.
   *
   * @param c the index {@code index < columns()}
   * @param columnBuilder the builder
   */
  void set(int c, Series.Builder columnBuilder);

  default void set(int c, Series column) {
    set(c, column.newCopyBuilder());
  }

  void remove(int c);

  /**
   * Sets the {@code builder} at {@code index}. *
   * <p/>
   * Note that is not safe to call {@link Series.Builder#build()} after this method.
   *
   * @param r the index
   * @param recordBuilder the builder
   */
  void setRecord(int r, Series.Builder recordBuilder);

  default void setRecord(int index, Series series) {
    setRecord(index, series.newCopyBuilder());
  }

  void removeRecord(int r);

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
  void swapRecords(int a, int b);
}

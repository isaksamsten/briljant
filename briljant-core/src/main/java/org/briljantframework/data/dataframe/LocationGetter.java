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

import org.briljantframework.array.IntArray;
import org.briljantframework.data.series.Series;

/**
 * This class provides location-based indexing capabilities to the {@link DataFrame}.
 *
 * <p>
 * Location-based indexing closely follows the semantics of
 * {@link org.briljantframework.array.Array} and {@link java.util.List}. That is indexing starts
 * with {@code 0} and ends with {@code rows() - 1} or {@code columns() - 1}
 *
 * <pre>
 * {
 *   &#064;code
 *   DataFrame df = MixedDataFrame.of(&quot;a&quot;, Series.of(1, 2, 3));
 *   df.loc().get(String.class, 0, 0); // =&gt; 1
 * }
 * </pre>
 *
 * @author Isak Karlsson
 * @see org.briljantframework.data.series.LocationGetter
 * @see LocationSetter
 */
public interface LocationGetter {

  default Object get(int r, int c) {
    return get(Object.class, r, c);
  }

  /**
   * Get value at {@code row} and {@code column} as an instance of {@code T}. If conversion fails,
   * return {@code NA} as defined by {@link org.briljantframework.data.Na#of(Class)}. The conversion
   * is performed according to the convention found in
   * {@link org.briljantframework.data.series.Convert#to(Class, Object)}
   *
   * @param cls the class
   * @param r the row
   * @param c the column
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
  double getDouble(int r, int c);

  /**
   * Get value at {@code row} and {@code column} as {@code int}.
   *
   * @param r the row
   * @param c the column
   * @return the value
   */
  int getInt(int r, int c);

  void set(int pos, Series column);

  void setRow(int pos, Series row);

  void set(int r, int c, Object value);

  /**
   * Returns true if value at {@code row, column} is {@code NA}.
   *
   * @param r the row
   * @param c the column
   * @return true or false
   */
  boolean isNA(int r, int c);

  Series get(int c);

  DataFrame get(IntArray columns);

  DataFrame drop(int index);

  DataFrame drop(IntArray columns);

  Series getRow(int r);

  DataFrame getRow(IntArray records);
}

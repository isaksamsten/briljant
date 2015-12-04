/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Isak Karlsson
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
package org.briljantframework.data;

import java.util.Comparator;

/**
 * Enum defining the sort order as either increasing or decreasing.
 * 
 * @author Isak Karlsson
 */
public enum SortOrder {
  /**
   * Ascending order
   */
  ASC,

  /**
   * Descending order
   */
  DESC;

  /**
   * Appropriately reverses a comparator to sort in the order defined by this enum.
   *
   * <p>
   * If {@code this == ASC} return an unmodified comparator and otherwise a
   * {@linkplain java.util.Comparator#reversed() reversed} comparator
   *
   * @param cmp the comparator
   * @return a possibly reversed comparator
   */
  public Comparator<Object> orderComparator(Comparator<Object> cmp) {
    return this == ASC ? cmp : cmp.reversed();
  }
}

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
package org.briljantframework.data.dataframe.join;

import org.briljantframework.array.IntArray;

import java.util.Collection;
import java.util.Collections;

/**
 * Represent a set of join-keys for two indexed collections
 *
 * @author Isak Karlsson
 */
public class JoinKeys {
  private final IntArray left;
  private final IntArray right;
  private final int maxGroups;
  private final Collection<?> columnKeys;

  JoinKeys(IntArray left, IntArray right, int maxGroups, Collection<?> columnKeys) {
    this.left = left;
    this.right = right;
    this.maxGroups = maxGroups;
    this.columnKeys = columnKeys;
  }

  JoinKeys(IntArray left, IntArray right, int maxGroups) {
    this(left, right, maxGroups, Collections.emptyList());
  }

  public Collection<?> getColumnKeys() {
    return Collections.unmodifiableCollection(columnKeys);
  }

  /**
   * Returns the left join keys
   *
   * @return the left join keys
   */
  public IntArray getLeft() {
    return left;
  }

  /**
   * Returns the right join keys
   *
   * @return the right join keys
   */
  public IntArray getRight() {
    return right;
  }

  /**
   * Return the number of possible keys (i.e. {@code |unique(getLeft()) U unique(getRight())|})
   *
   * @return the number of possible keys
   */
  public int getMaxGroups() {
    return maxGroups;
  }
}

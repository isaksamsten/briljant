/**
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
package org.briljantframework.data.dataframe.join;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.briljantframework.array.IntArray;
import org.briljantframework.data.Na;
import org.briljantframework.data.dataframe.DataFrame;
import org.briljantframework.data.vector.Vector;

/**
 * @author Isak Karlsson
 */
public class JoinUtils {

  /**
   * @return retVal[0] := indexer, retVal[1] := counts
   */
  public static IntArray[] groupSortIndexer(IntArray index, int maxGroups) {
    IntArray counts = IntArray.zeros(maxGroups + 1);
    int n = index.size();
    for (int i = 0; i < n; i++) {
      int idx = index.get(i) + 1;
      counts.set(idx, counts.get(idx) + 1);
    }

    int[] where = new int[maxGroups + 1];
    for (int i = 1; i < maxGroups + 1; i++) {
      where[i] = where[i - 1] + counts.get(i - 1);
    }

    IntArray results = IntArray.zeros(n);
    for (int i = 0; i < n; i++) {
      int label = index.get(i) + 1;
      results.set(where[label], i);
      where[label] += 1;
    }

    return new IntArray[] {results, counts};
  }

  /**
   * Create {@code JoinKeys} using the index
   *
   * @param a the left data frame
   * @param b the right data frame
   * @return a new join key
   */
  public static JoinKeys createJoinKeys(DataFrame a, DataFrame b) {
    return createJoinKeys(a.getIndex(), b.getIndex());
  }

  public static JoinKeys createJoinKeys(List<?> a, List<?> b) {
    int[] left = new int[a.size()];
    int[] right = new int[b.size()];
    Map<Object, Integer> pool = new HashMap<>(Math.max(a.size(), b.size()));

    int j = computeKeys(a, left, pool, 0);
    computeKeys(b, right, pool, j);
    return new JoinKeys(IntArray.of(left), IntArray.of(right), pool.size());
  }

  private static int computeKeys(List<?> a, int[] left, Map<Object, Integer> pool, int j) {
    for (int i = 0; i < a.size(); i++) {
      Object val = a.get(i);
      int ref = pool.getOrDefault(val, Na.BOXED_INT);
      if (ref != Na.INT) {
        left[i] = ref;
      } else {
        left[i] = j;
        pool.put(val, j);
        j += 1;
      }
    }
    return j;
  }

  public static JoinKeys createJoinKeys(DataFrame a, DataFrame b, Collection<?> on) {
    IntArray left = null;
    IntArray right = null;

    int noGroups = 1;
    for (Object columnKey : on) {
      JoinKeys pool = createJoinKeys(a.get(columnKey), b.get(columnKey));

      if (noGroups > 1) {
        IntArray lt = pool.getLeft();
        IntArray rt = pool.getRight();
        for (int i = 0; i < lt.size(); i++) {
          left.set(i, left.get(i) + lt.get(i) * noGroups);
        }

        for (int i = 0; i < rt.size(); i++) {
          right.set(i, right.get(i) + rt.get(i) * noGroups);
        }
      } else {
        left = pool.getLeft();
        right = pool.getRight();
      }
      noGroups = noGroups * (pool.getMaxGroups() + 1);
    }
    return new JoinKeys(left, right, noGroups);
  }

  public static JoinKeys createJoinKeys(Vector a, Vector b) {
    return createJoinKeys(a.toList(Object.class), b.toList(Object.class));
  }

}

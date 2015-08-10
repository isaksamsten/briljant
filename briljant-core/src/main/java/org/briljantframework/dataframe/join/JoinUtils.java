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

package org.briljantframework.dataframe.join;

import com.carrotsearch.hppc.ObjectIntMap;
import com.carrotsearch.hppc.ObjectIntOpenHashMap;

import org.briljantframework.Bj;
import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.array.IntArray;
import org.briljantframework.vector.Vector;

import java.util.Collection;
import java.util.List;

/**
 * Created by Isak Karlsson on 09/01/15.
 */
public class JoinUtils {

  public static final int MISSING = Integer.MIN_VALUE;

  /**
   * @return retVal[0] := indexer, retVal[1] := counts
   */
  public static IntArray[] groupSortIndexer(IntArray index, int maxGroups) {
    IntArray counts = Bj.intArray(maxGroups + 1);
    int n = index.size();
    for (int i = 0; i < n; i++) {
      int idx = index.get(i) + 1;
      counts.set(idx, counts.get(idx) + 1);
    }

    int[] where = new int[maxGroups + 1];
    for (int i = 1; i < maxGroups + 1; i++) {
      where[i] = where[i - 1] + counts.get(i - 1);
    }

    IntArray results = Bj.intArray(n);
    for (int i = 0; i < n; i++) {
      int label = index.get(i) + 1;
      results.set(where[label], i);
      where[label] += 1;
    }

    return new IntArray[]{results, counts};
  }

  /**
   * Create {@code JoinKeys} using the index
   *
   * @param a the left data frame
   * @param b the right data frame
   * @return a new join key
   */
  public static JoinKeys createJoinKeys(DataFrame a, DataFrame b) {
    return createJoinKeys(a.getRecordIndex(), b.getRecordIndex());
  }

  public static JoinKeys createJoinKeys(DataFrame a, DataFrame b, Collection<Integer> on) {
    IntArray left = null;
    IntArray right = null;

    int noGroups = 1;
    for (int column : on) {
      JoinKeys pool = createJoinKeys(a.get(column), b.get(column));

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

  public static JoinKeys createJoinKeys(List<?> a, List<?> b) {
    int aSize = a.size();
    int bSize = b.size();
    int[] left = new int[aSize];
    int[] right = new int[bSize];
    ObjectIntMap<Object> pool = new ObjectIntOpenHashMap<>();

    int j = 0;
    for (int i = 0; i < aSize; i++) {
      Object val = a.get(i);
      int ref = pool.getOrDefault(val, MISSING);
      if (ref != MISSING) {
        left[i] = ref;
      } else {
        left[i] = j;
        pool.put(val, j);
        j += 1;
      }
    }

    for (int i = 0; i < bSize; i++) {
      Object val = b.get(i);
      int ref = pool.getOrDefault(val, MISSING);
      if (ref != MISSING) {
        right[i] = ref;
      } else {
        right[i] = j;
        pool.put(val, j);
        j += 1;
      }
    }

    return new JoinKeys(Bj.array(left), Bj.array(right), pool.size());
  }

  public static JoinKeys createJoinKeys(Vector a, Vector b) {
    return createJoinKeys(a.asList(Object.class), b.asList(Object.class));
  }

}

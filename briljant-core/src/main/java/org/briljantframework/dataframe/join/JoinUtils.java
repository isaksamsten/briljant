package org.briljantframework.dataframe.join;

import com.carrotsearch.hppc.IntIntMap;
import com.carrotsearch.hppc.IntIntOpenHashMap;
import com.carrotsearch.hppc.ObjectIntMap;
import com.carrotsearch.hppc.ObjectIntOpenHashMap;

import org.briljantframework.Briljant;
import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.matrix.IntMatrix;
import org.briljantframework.vector.Vector;

import java.util.Collection;

/**
 * Created by Isak Karlsson on 09/01/15.
 */
public class JoinUtils {

  public static final int MISSING = Integer.MIN_VALUE;

  /**
   * @return retVal[0] := indexer, retVal[1] := counts
   */
  public static IntMatrix[] groupSortIndexer(IntMatrix index, int maxGroups) {
    IntMatrix counts = Briljant.intVector(maxGroups + 1);
    int n = index.size();
    for (int i = 0; i < n; i++) {
      int idx = index.get(i) + 1;
      counts.set(idx, counts.get(idx) + 1);
    }

    int[] where = new int[maxGroups + 1];
    for (int i = 1; i < maxGroups + 1; i++) {
      where[i] = where[i - 1] + counts.get(i - 1);
    }

    IntMatrix results = Briljant.intVector(n);
    for (int i = 0; i < n; i++) {
      int label = index.get(i) + 1;
      results.set(where[label], i);
      where[label] += 1;
    }

    return new IntMatrix[]{results, counts};
  }

  public static JoinKeys createJoinKeys(DataFrame a, DataFrame b, Collection<String> columns) {
    IntMatrix left = null;
    IntMatrix right = null;

    int noGroups = 1;
    for (String column : columns) {
      JoinKeys pool = createJoinKeys(a.getColumn(column), b.getColumn(column));

      if (noGroups > 1) {
        IntMatrix lt = pool.getLeft();
        IntMatrix rt = pool.getRight();
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

  private static int downMap(IntMatrix left, IntMatrix right) {
    IntIntMap pool = new IntIntOpenHashMap();
    int j = 0;
    int lSize = left.size();
    for (int i = 0; i < lSize; i++) {
      int value = left.get(i);
      int ref = pool.getOrDefault(value, -1);
      if (ref != -1) {
        left.set(i, ref);
      } else {
        left.set(i, j);
        pool.put(value, j);
        j += 1;
      }
    }

    int rSize = right.size();
    for (int i = 0; i < rSize; i++) {
      int value = right.get(i);
      int ref = pool.getOrDefault(value, -1);
      if (ref != -1) {
        right.set(i, ref);
      } else {
        right.set(i, j);
        pool.put(value, j);
        j += 1;
      }
    }

    return pool.size();
  }

  public static JoinKeys createJoinKeys(Vector a, Vector b) {
    int aSize = a.size();
    int bSize = b.size();
    int[] left = new int[aSize];
    int[] right = new int[bSize];
    ObjectIntMap<Object> pool = new ObjectIntOpenHashMap<>();

    int j = 0;
    for (int i = 0; i < aSize; i++) {
      Object val = a.get(Object.class, i);
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
      Object val = b.get(Object.class, i);
      int ref = pool.getOrDefault(val, MISSING);
      if (ref != MISSING) {
        right[i] = ref;
      } else {
        right[i] = j;
        pool.put(val, j);
        j += 1;
      }
    }

    return new JoinKeys(Briljant.matrix(left), Briljant.matrix(right), pool.size());
  }

}

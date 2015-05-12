package org.briljantframework.dataframe.join;

import com.carrotsearch.hppc.ObjectIntMap;
import com.carrotsearch.hppc.ObjectIntOpenHashMap;

import org.briljantframework.Bj;
import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.matrix.IntMatrix;
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
  public static IntMatrix[] groupSortIndexer(IntMatrix index, int maxGroups) {
    IntMatrix counts = Bj.intVector(maxGroups + 1);
    int n = index.size();
    for (int i = 0; i < n; i++) {
      int idx = index.get(i) + 1;
      counts.set(idx, counts.get(idx) + 1);
    }

    int[] where = new int[maxGroups + 1];
    for (int i = 1; i < maxGroups + 1; i++) {
      where[i] = where[i - 1] + counts.get(i - 1);
    }

    IntMatrix results = Bj.intVector(n);
    for (int i = 0; i < n; i++) {
      int label = index.get(i) + 1;
      results.set(where[label], i);
      where[label] += 1;
    }

    return new IntMatrix[]{results, counts};
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
    IntMatrix left = null;
    IntMatrix right = null;

    int noGroups = 1;
    for (int column : on) {
      JoinKeys pool = createJoinKeys(a.get(column), b.get(column));

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

    return new JoinKeys(Bj.matrix(left), Bj.matrix(right), pool.size());
  }

  public static JoinKeys createJoinKeys(Vector a, Vector b) {
    return createJoinKeys(a.asList(Object.class), b.asList(Object.class));
//    int aSize = a.size();
//    int bSize = b.size();
//    int[] left = new int[aSize];
//    int[] right = new int[bSize];
//    ObjectIntMap<Object> pool = new ObjectIntOpenHashMap<>();
//
//    int j = 0;
//    for (int i = 0; i < aSize; i++) {
//      Object val = a.get(Object.class, i);
//      int ref = pool.getOrDefault(val, MISSING);
//      if (ref != MISSING) {
//        left[i] = ref;
//      } else {
//        left[i] = j;
//        pool.put(val, j);
//        j += 1;
//      }
//    }
//
//    for (int i = 0; i < bSize; i++) {
//      Object val = b.get(Object.class, i);
//      int ref = pool.getOrDefault(val, MISSING);
//      if (ref != MISSING) {
//        right[i] = ref;
//      } else {
//        right[i] = j;
//        pool.put(val, j);
//        j += 1;
//      }
//    }
//
//    return new JoinKeys(Bj.matrix(left), Bj.matrix(right), pool.size());
  }

}

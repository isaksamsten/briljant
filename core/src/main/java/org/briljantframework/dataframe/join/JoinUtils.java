package org.briljantframework.dataframe.join;

import java.util.Collection;

import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.matrix.DefaultIntMatrix;
import org.briljantframework.matrix.IntMatrix;
import org.briljantframework.matrix.Matrices;
import org.briljantframework.vector.Value;
import org.briljantframework.vector.Vector;

import com.carrotsearch.hppc.IntIntMap;
import com.carrotsearch.hppc.IntIntOpenHashMap;
import com.carrotsearch.hppc.ObjectIntMap;
import com.carrotsearch.hppc.ObjectIntOpenHashMap;

/**
 * Created by Isak Karlsson on 09/01/15.
 */
public class JoinUtils {

  public static final int MISSING = Integer.MIN_VALUE;

  /**
   * @return retVal[0] := indexer, retVal[1] := counts
   */
  public static IntMatrix[] groupSortIndexer(IntMatrix index, int maxGroups) {
    IntMatrix counts = Matrices.newIntVector(maxGroups + 1);
    int n = index.size();
    for (int i = 0; i < n; i++) {
      counts.update(index.get(i) + 1, x -> x + 1);
    }

    int[] where = new int[maxGroups + 1];
    for (int i = 1; i < maxGroups + 1; i++) {
      where[i] = where[i - 1] + counts.get(i - 1);
    }


    IntMatrix results = Matrices.newIntVector(n);
    for (int i = 0; i < n; i++) {
      int label = index.get(i) + 1;
      results.set(where[label], i);
      // result[where[label]] = i;
      where[label] += 1;
    }

    return new IntMatrix[] {results, counts};
  }

  public static JoinKeys createJoinKeys(DataFrame a, DataFrame b, Collection<String> columns) {
    int[] newLeftPool = new int[a.rows()];
    int[] newRightPool = new int[b.rows()];

    int noGroups = 1;
    for (String column : columns) {
      JoinKeys pool = createJoinKeys(a.getColumn(column), b.getColumn(column));

      IntMatrix left = pool.getLeft();
      IntMatrix right = pool.getRight();
      for (int i = 0; i < newLeftPool.length; i++) {
        newLeftPool[i] += left.get(i) * noGroups;
      }

      for (int i = 0; i < newRightPool.length; i++) {
        newRightPool[i] += right.get(i) * noGroups;
      }
      noGroups = noGroups * (pool.getMaxGroups() + 1);
    }

    IntMatrix left = new DefaultIntMatrix(newLeftPool); // AbstractIntMatrix.wrap(newLeftPool);
    IntMatrix right = new DefaultIntMatrix(newRightPool); // AbstractIntMatrix.wrap(newRightPool);
    if (left.size() + right.size() > noGroups) {
      noGroups = downMap(left, right);
    }
    return new JoinKeys(left, right, noGroups);
  }

  private static int downMap(IntMatrix left, IntMatrix right) {
    IntIntMap pool = new IntIntOpenHashMap();
    int j = 0;
    for (int i = 0; i < left.size(); i++) {
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

    for (int i = 0; i < right.size(); i++) {
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
    int[] left = new int[a.size()];
    int[] right = new int[b.size()];
    ObjectIntMap<Value> pool = new ObjectIntOpenHashMap<>();

    int j = 0;
    for (int i = 0; i < a.size(); i++) {
      Value val = a.getAsValue(i);
      int ref = pool.getOrDefault(val, MISSING);
      if (ref != MISSING) {
        left[i] = pool.get(val);
      } else {
        left[i] = j;
        pool.put(val, j);
        j += 1;
      }
    }

    for (int i = 0; i < b.size(); i++) {
      Value val = b.getAsValue(i);
      int ref = pool.getOrDefault(val, MISSING);
      if (ref != MISSING) {
        right[i] = pool.get(val);
      } else {
        right[i] = j;
        pool.put(val, j);
        j += 1;
      }
    }

    return new JoinKeys(new DefaultIntMatrix(left), new DefaultIntMatrix(right), pool.size());
  }

}

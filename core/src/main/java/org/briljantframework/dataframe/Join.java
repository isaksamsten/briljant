package org.briljantframework.dataframe;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import org.briljantframework.vector.Value;
import org.briljantframework.vector.Vector;

import com.carrotsearch.hppc.ObjectIntMap;
import com.carrotsearch.hppc.ObjectIntOpenHashMap;
import com.google.common.primitives.Ints;

/**
 * Created by Isak on 2015-01-08.
 */
public class Join {

  private final Collection<Integer> on;

  public Join(int... on) {
    this.on = new HashSet<>(Ints.asList(on));
  }

  public DataFrame join(DataFrame a, DataFrame b) {
    PoolRef[] refs = getPoolRef(a, b);

    JoinIdx idx = innerJoin(refs[0].refs, refs[1].refs, refs[0].noGroups);

    int[] leftIndexer = idx.leftIndexer;
    int[] rightIndexer = idx.rightIndex;

    DataFrame.Builder builder = a.newBuilder();
    for (int i = 0; i < leftIndexer.length; i++) {
      int aRow = leftIndexer[i];
      int bRow = rightIndexer[i];
      int column = 0;
      for (int j = 0; j < a.columns(); j++) {
        builder.set(i, column, a, aRow, column);
        column += 1;
      }
      for (int j = 0; j < b.columns(); j++) {
        if (!on.contains(j)) {
          if (i == 0) {
            builder.addColumn(b.getColumnType(j).newBuilder());
            builder.setColumnName(column, b.getColumnName(j));
          }
          builder.set(i, column, a, bRow, j);
          column += 1;
        }
      }
    }

    return builder.build();
  }

  public PoolRef[] getPoolRef(DataFrame a, DataFrame b) {
    Iterator<Integer> it = on.iterator();
    int index = it.next();

    PoolRef[] refs = getPoolRef(a.getColumn(index), b.getColumn(index));
    int[] arefs = refs[0].refs;
    int[] brefs = refs[1].refs;
    int noGroups = refs[0].noGroups + 1;
    while (it.hasNext()) {
      index = it.next();
      PoolRef[] reref = getPoolRef(a.getColumn(index), b.getColumn(index));
      int[] arerefs = reref[0].refs;
      int[] brerefs = reref[1].refs;
      for (int i = 0; i < arefs.length; i++) {
        arefs[i] += arerefs[i] * noGroups;
      }

      for (int i = 0; i < brefs.length; i++) {
        brefs[i] += brerefs[i] * noGroups;
      }

      noGroups = noGroups * (reref[0].noGroups + 1);
    }
    refs[0].noGroups = noGroups;
    return refs;
  }

  public PoolRef[] getPoolRef(Vector a, Vector b) {
    int[] arefs = new int[a.size()];
    int[] brefs = new int[b.size()];
    ObjectIntMap<Value> poolrefs = new ObjectIntOpenHashMap<>();
    int j = 0;
    for (int i = 0; i < a.size(); i++) {
      Value aval = a.getAsValue(i);
      int aref = poolrefs.getOrDefault(aval, Integer.MIN_VALUE);
      if (aref != Integer.MIN_VALUE) {
        arefs[i] = poolrefs.get(aval);
      } else {
        arefs[i] = j;
        poolrefs.put(aval, j);
        j += 1;
      }
    }

    for (int i = 0; i < b.size(); i++) {
      Value bval = b.getAsValue(i);
      int bref = poolrefs.getOrDefault(bval, Integer.MIN_VALUE);
      if (bref != Integer.MIN_VALUE) {
        brefs[i] = poolrefs.get(bval);
      } else {
        brefs[i] = j;
        poolrefs.put(bval, j);
        j += 1;
      }
    }

    return new PoolRef[] {new PoolRef(arefs, poolrefs.size()), new PoolRef(brefs, poolrefs.size())};
  }

  private JoinIdx innerJoin(int[] left, int[] right, int noGroups) {
    int[][] l = groupSort(left, noGroups);
    int[][] r = groupSort(right, noGroups);

    int[] leftSorter = l[0];
    int[] leftCount = l[1];

    int[] rightSorter = r[0];
    int[] rightCount = r[1];

    int count = 0;
    for (int i = 1; i < noGroups + 1; i++) {
      int lc = leftCount[i];
      int rc = rightCount[i];

      if (rc > 0 && lc > 0) {
        count += rc * lc;
      }
    }

    int pos = 0;
    int leftPos = leftCount[0], rightPos = rightCount[0];

    int[] leftIndexer = new int[count];
    int[] rightIndexer = new int[count];

    for (int i = 1; i < noGroups + 1; i++) {
      int lc = leftCount[i];
      int rc = rightCount[i];

      if (lc > 0 && rc > 0) {
        for (int j = 0; j < lc; j++) {
          int offset = pos + j * rc;
          for (int k = 0; k < rc; k++) {
            leftIndexer[offset + k] = leftPos + j;
            rightIndexer[offset + k] = rightPos + k;
          }
        }
        pos += rc * lc;
      }
      leftPos += lc;
      rightPos += rc;
    }

    int[] leftSorted = new int[leftIndexer.length];
    int[] rightSorted = new int[rightIndexer.length];
    for (int i = 0; i < leftSorted.length; i++) {
      leftSorted[i] = leftSorter[leftIndexer[i]];
      rightSorted[i] = rightSorter[rightIndexer[i]];
    }

    return new JoinIdx(leftSorted, rightSorted, null, null);
  }

  private int[][] groupSort(int[] index, int noGroups) {
    int[] counts = new int[noGroups + 1];
    int n = index.length;
    for (int i = 0; i < n; i++) {
      counts[index[i] + 1] += 1;
    }

    int[] where = new int[noGroups + 1];
    for (int i = 1; i < noGroups + 1; i++) {
      where[i] = where[i - 1] + counts[i - 1];
    }

    int[] result = new int[n];
    for (int i = 0; i < n; i++) {
      int label = index[i] + 1;
      result[where[label]] = i;
      where[label] += 1;
    }

    return new int[][] {result, counts};
  }

  class PoolRef {
    int[] refs;
    int noGroups;

    public PoolRef(int[] refs, int noGroups) {
      this.refs = refs;
      this.noGroups = noGroups;
    }
  }

  class JoinIdx {

    int[] leftIndexer, rightIndex, leftOnly, rightOnly;

    public JoinIdx(int[] leftIndexer, int[] rightIndex, int[] leftOnly, int[] rightOnly) {
      this.leftIndexer = leftIndexer;
      this.rightIndex = rightIndex;
      this.leftOnly = leftOnly;
      this.rightOnly = rightOnly;
    }

    @Override
    public String toString() {
      return "JoinIdx{" + "leftIndexer=" + Arrays.toString(leftIndexer) + ", rightIndex="
          + Arrays.toString(rightIndex) + ", leftOnly=" + Arrays.toString(leftOnly)
          + ", rightOnly=" + Arrays.toString(rightOnly) + '}';
    }
  }
}

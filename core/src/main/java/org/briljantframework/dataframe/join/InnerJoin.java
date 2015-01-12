package org.briljantframework.dataframe.join;

import org.briljantframework.matrix.IntMatrix;

/**
 * Created by Isak on 2015-01-08.
 */
public class InnerJoin implements JoinOperation {

  private static Joiner innerJoin(IntMatrix left, IntMatrix right, int noGroups) {
    IntMatrix[] l = JoinUtils.groupSortIndexer(left, noGroups);
    IntMatrix[] r = JoinUtils.groupSortIndexer(right, noGroups);

    IntMatrix leftSorter = l[0];
    IntMatrix leftCount = l[1];

    IntMatrix rightSorter = r[0];
    IntMatrix rightCount = r[1];

    int count = 0;
    for (int i = 1; i < noGroups + 1; i++) {
      int lc = leftCount.get(i);
      int rc = rightCount.get(i);

      if (rc > 0 && lc > 0) {
        count += rc * lc;
      }
    }

    int pos = 0;
    int leftPos = leftCount.get(0);
    int rightPos = rightCount.get(0);

    int[] leftIndexer = new int[count];
    int[] rightIndexer = new int[count];

    for (int i = 1; i < noGroups + 1; i++) {
      int lc = leftCount.get(i);
      int rc = rightCount.get(i);

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
      leftSorted[i] = leftSorter.get(leftIndexer[i]);
      rightSorted[i] = rightSorter.get(rightIndexer[i]);
    }

    return new Joiner() {
      @Override
      public int size() {
        return leftSorted.length;
      }

      @Override
      public int getLeftIndex(int i) {
        return leftSorted[i];
      }

      @Override
      public int getRightIndex(int i) {
        return rightSorted[i];
      }
    };
  }

  @Override
  public Joiner createJoiner(JoinKeys keys) {
    return innerJoin(keys.getLeft(), keys.getRight(), keys.getMaxGroups());
  }

}

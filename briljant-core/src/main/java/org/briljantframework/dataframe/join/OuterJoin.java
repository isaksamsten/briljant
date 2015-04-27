package org.briljantframework.dataframe.join;

import org.briljantframework.matrix.IntMatrix;

/**
 * Created by Isak Karlsson on 11/01/15.
 */
public class OuterJoin implements JoinOperation {

  private static final OuterJoin INSTANCE = new OuterJoin();

  public static JoinOperation getInstance() {
    return INSTANCE;
  }

  @Override
  public Joiner createJoiner(JoinKeys keys) {
    int maxGroups = keys.getMaxGroups();
    IntMatrix[] l = JoinUtils.groupSortIndexer(keys.getLeft(), maxGroups);
    IntMatrix[] r = JoinUtils.groupSortIndexer(keys.getRight(), maxGroups);

    IntMatrix leftSorter = l[0];
    IntMatrix rightSorter = r[0];
    IntMatrix leftCount = l[1];
    IntMatrix rightCount = r[1];

    int count = 0;
    for (int i = 1; i < maxGroups + 1; i++) {
      int lc = leftCount.get(i);
      int rc = rightCount.get(i);

      if (rc > 0 && lc > 0) {
        count += lc * rc;
      } else {
        count += lc + rc;
      }
    }

    int pos = 0;
    int leftPos = leftCount.get(0);
    int rightPos = rightCount.get(0);
    int[] leftIndexer = new int[count];
    int[] rightIndexer = new int[count];
    for (int i = 1; i < maxGroups + 1; i++) {
      int lc = leftCount.get(i);
      int rc = rightCount.get(i);
      if (rc == 0) {
        for (int j = 0; j < lc; j++) {
          leftIndexer[pos + j] = leftPos + j;
          rightIndexer[pos + j] = -1;
        }
        pos += lc;
      } else if (lc == 0) {
        for (int j = 0; j < rc; j++) {
          leftIndexer[pos + j] = -1;
          rightIndexer[pos + j] = rightPos + j;
        }
        pos += rc;
      } else {
        for (int j = 0; j < lc; j++) {
          int offset = pos + j * rc;
          for (int k = 0; k < rc; k++) {
            leftIndexer[offset + k] = leftPos + j;
            rightIndexer[offset + k] = rightPos + k;
          }
        }
        pos += lc * rc;
      }
      leftPos += lc;
      rightPos += rc;
    }
    int[] leftSorted = new int[leftIndexer.length];
    int[] rightSorted = new int[rightIndexer.length];
    for (int i = 0; i < leftSorted.length; i++) {
      int li = leftIndexer[i];
      if (li < 0) {
        leftSorted[i] = -1;
      } else {
        leftSorted[i] = leftSorter.get(li);
      }
      int ri = rightIndexer[i];
      if (ri < 0) {
        rightSorted[i] = -1;
      } else {
        rightSorted[i] = rightSorter.get(ri);
      }
    }
    return new ArrayJoiner(leftSorted, rightSorted);
  }
}

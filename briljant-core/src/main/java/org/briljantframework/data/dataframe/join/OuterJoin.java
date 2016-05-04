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

/**
 * @author Isak Karlsson
 */
public class OuterJoin implements JoinOperation {

  private static final OuterJoin INSTANCE = new OuterJoin();

  public static JoinOperation getInstance() {
    return INSTANCE;
  }

  @Override
  public Joiner createJoiner(JoinKeys keys) {
    int maxGroups = keys.getMaxGroups();
    IntArray[] l = JoinUtils.groupSortIndexer(keys.getLeft(), maxGroups);
    IntArray[] r = JoinUtils.groupSortIndexer(keys.getRight(), maxGroups);

    IntArray leftSorter = l[0];
    IntArray rightSorter = r[0];
    IntArray leftCount = l[1];
    IntArray rightCount = r[1];

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
    return new ArrayJoiner(keys.getColumnKeys(), leftSorted, rightSorted);
  }
}

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

import org.briljantframework.array.IntArray;

/**
 * @author Isak Karlsson
 */
public class LeftOuterJoin implements JoinOperation {

  public static final LeftOuterJoin INSTANCE = new LeftOuterJoin();

  private LeftOuterJoin() {}

  public static LeftOuterJoin getInstance() {
    return INSTANCE;
  }

  @Override
  public Joiner createJoiner(JoinKeys keys) {
    int noGroups = keys.getMaxGroups();
    IntArray[] l = JoinUtils.groupSortIndexer(keys.getLeft(), noGroups);
    IntArray[] r = JoinUtils.groupSortIndexer(keys.getRight(), noGroups);

    IntArray leftSorter = l[0], leftCount = l[1];
    IntArray rightSorter = r[0], rightCount = r[1];

    int count = 0;
    for (int i = 1; i < noGroups + 1; i++) {
      if (rightCount.get(i) > 0) {
        count += rightCount.get(i) * leftCount.get(i);
      } else {
        count += leftCount.get(i);
      }
    }

    int pos = 0, leftPos = leftCount.get(0), rightPos = rightCount.get(0);
    int[] leftIndexer = new int[count], rightIndexer = new int[count];
    for (int i = 1; i < noGroups + 1; i++) {
      int lc = leftCount.get(i);
      int rc = rightCount.get(i);

      if (rc == 0) {
        for (int j = 0; j < lc; j++) {
          leftIndexer[pos + j] = leftPos + j;
          rightIndexer[pos + j] = -1;
        }
        pos += lc;
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
      leftSorted[i] = leftSorter.get(leftIndexer[i]);
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

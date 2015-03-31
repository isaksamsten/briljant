package org.briljantframework.dataframe.join;

/**
 * Created by isak on 28/03/15.
 */
class ArrayJoiner extends Joiner {

  private final int[] leftSorted;
  private final int[] rightSorted;

  public ArrayJoiner(int[] leftSorted, int[] rightSorted) {
    this.leftSorted = leftSorted;
    this.rightSorted = rightSorted;
  }

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
}

package org.briljantframework.dataframe.join;

import org.briljantframework.vector.IntVector;

/**
 * Created by Isak Karlsson on 09/01/15.
 */
public class JoinKeys {
  private final IntVector left, right;
  private final int maxGroups;

  JoinKeys(IntVector left, IntVector right, int maxGroups) {
    this.left = left;
    this.right = right;
    this.maxGroups = maxGroups;
  }

  public IntVector getLeft() {
    return left;
  }

  public IntVector getRight() {
    return right;
  }

  public int getMaxGroups() {
    return maxGroups;
  }
}

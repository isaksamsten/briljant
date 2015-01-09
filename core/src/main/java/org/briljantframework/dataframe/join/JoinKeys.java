package org.briljantframework.dataframe.join;

import org.briljantframework.matrix.IntMatrix;

/**
 * Created by Isak Karlsson on 09/01/15.
 */
public class JoinKeys {
  private final IntMatrix left;
  private final IntMatrix right;
  private final int maxGroups;

  JoinKeys(IntMatrix left, IntMatrix right, int maxGroups) {
    this.left = left;
    this.right = right;
    this.maxGroups = maxGroups;
  }

  public IntMatrix getLeft() {
    return left;
  }

  public IntMatrix getRight() {
    return right;
  }

  public int getMaxGroups() {
    return maxGroups;
  }
}

package org.briljantframework.dataframe.join;

import org.briljantframework.matrix.IntArray;

/**
 * Represent a set of join-keys for two indexed collections
 *
 * @author Isak Karlsson
 */
public class JoinKeys {

  private final IntArray left;
  private final IntArray right;
  private final int maxGroups;

  JoinKeys(IntArray left, IntArray right, int maxGroups) {
    this.left = left;
    this.right = right;
    this.maxGroups = maxGroups;
  }

  /**
   * Returns the left join keys
   *
   * @return the left join keys
   */
  public IntArray getLeft() {
    return left;
  }

  /**
   * Returns the right join keys
   *
   * @return the right join keys
   */
  public IntArray getRight() {
    return right;
  }

  /**
   * Return the number of possible keys (i.e. {@code |unique(getLeft()) U unique(getRight())|})
   *
   * @return the number of possible keys
   */
  public int getMaxGroups() {
    return maxGroups;
  }
}

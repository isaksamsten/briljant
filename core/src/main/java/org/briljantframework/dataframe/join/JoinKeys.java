package org.briljantframework.dataframe.join;

import org.briljantframework.matrix.IntMatrix;

/**
 * Represent a set of join-keys for two indexed collections
 *
 * @author Isak Karlsson
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

  /**
   * Returns the left join keys
   *
   * @return the left join keys
   */
  public IntMatrix getLeft() {
    return left;
  }

  /**
   * Returns the right join keys
   *
   * @return the right join keys
   */
  public IntMatrix getRight() {
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

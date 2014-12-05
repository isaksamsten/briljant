package org.briljantframework.shapelet;

import org.briljantframework.matrix.Matrices;
import org.briljantframework.matrix.MatrixLike;

/**
 * Created by Isak Karlsson on 27/10/14.
 */
public class IndexSortedNormalizedShapelet extends NormalizedShapelet {

  /**
   * The Order.
   */
  protected final int[] order;

  /**
   * Instantiates a new Index sorted normalized shapelet.
   *
   * @param start the start
   * @param length the length
   * @param vector the vector
   */
  public IndexSortedNormalizedShapelet(int start, int length, MatrixLike vector) {
    super(start, length, vector);
    this.order =
        Matrices.sortIndex(this,
            (i, j) -> Double.compare(Math.abs(this.get(j)), Math.abs(this.get(i))));
  }

  /**
   * Get order.
   *
   * @return the int [ ]
   */
  public int[] getOrder() {
    return order;
  }
}

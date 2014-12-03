package org.briljantframework.classification.shapelet;

import org.briljantframework.matrix.RealMatrices;
import org.briljantframework.matrix.RealMatrixLike;

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
  public IndexSortedNormalizedShapelet(int start, int length, RealMatrixLike vector) {
    super(start, length, vector);
    this.order =
        RealMatrices.sortIndex(this,
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

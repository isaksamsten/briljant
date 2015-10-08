package org.briljantframework.conformal;

import org.briljantframework.array.DoubleArray;
import org.briljantframework.data.dataframe.DataFrame;
import org.briljantframework.data.vector.Vector;

/**
 * @author Isak Karlsson <isak-kar@dsv.su.se>
 */
public interface Nonconformity {

  DoubleArray estimate(DataFrame x, Vector y);

  double estimate(Vector example, Object label);

  /**
   * Learn a {@linkplain Nonconformity nonconformity score function} using the given data and
   * targets.
   *
   * @author Isak Karlsson <isak-kar@dsv.su.se>
   */
  interface Learner {

    /**
     * Fit a {@linkplain Nonconformity nonconformity score function} using the given data.
     *
     * @param x the input data
     * @param y the input target
     * @return a nonconformity score function
     */
    Nonconformity fit(DataFrame x, Vector y);
  }
}

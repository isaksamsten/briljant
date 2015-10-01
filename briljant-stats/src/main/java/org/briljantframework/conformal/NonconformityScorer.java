package org.briljantframework.conformal;

import org.briljantframework.array.DoubleArray;
import org.briljantframework.data.dataframe.DataFrame;
import org.briljantframework.data.vector.Vector;

/**
 * @author Isak Karlsson <isak-kar@dsv.su.se>
 */
public interface NonconformityScorer {

  double nonconformity(Vector example, Object label);

  DoubleArray nonconformity(DataFrame x, Vector y);
}

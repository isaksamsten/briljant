package org.briljantframework.regression;

import org.briljantframework.Check;
import org.briljantframework.array.DoubleArray;
import org.briljantframework.data.Is;
import org.briljantframework.data.dataframe.DataFrame;
import org.briljantframework.data.vector.Vector;

/**
 * @author Isak Karlsson <isak-kar@dsv.su.se>
 */
public interface RegressionLearner {

  default Regression fit(DataFrame x, Vector y) {
    Check.argument(x.rows() == y.size(), "Size of input data and input target don't match");
    Check.argument(x.getColumns().stream().allMatch(Is::numeric), "Only supports numerical data.");
    Check.argument(Is.numeric(y), "Only support numerical target");
    return fit(x.toDoubleArray(), y.toDoubleArray());
  }

  Regression fit(DoubleArray x, DoubleArray y);
}

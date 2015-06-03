package org.briljantframework.classification;

import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.vector.Vector;

/**
 * Created by isak on 29/05/15.
 */
public interface Regressor {

  Object fit(DataFrame df, Vector l);
}

package org.briljantframework.conformal;

import org.briljantframework.classification.Classifier;
import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.vector.Vector;

/**
 * @author Isak Karlsson
 */
public interface ConformalClassifier extends Classifier {

  void calibrate(DataFrame x, Vector y);
}

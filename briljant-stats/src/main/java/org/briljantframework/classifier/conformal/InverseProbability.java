package org.briljantframework.classifier.conformal;

import org.briljantframework.array.DoubleArray;

/**
 * @author Isak Karlsson <isak-kar@dsv.su.se>
 */
public class InverseProbability implements ClassificationCostFunction {

  @Override
  public double apply(DoubleArray score, int trueClassIndex) {
    return 1 - score.get(trueClassIndex);
  }
}

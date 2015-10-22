package org.briljantframework.classification;

import org.briljantframework.data.dataframe.DataFrame;
import org.briljantframework.data.vector.Vector;
import org.briljantframework.evaluation.PredictionMeasure;

/**
 * @author Isak Karlsson <isak-kar@dsv.su.se>
 */
public enum RandomShapeletForestMeasure implements PredictionMeasure<RandomShapeletForest> {
  DEPTH {
    @Override
    public double compute(RandomShapeletForest predictor, DataFrame x, Vector t) {
      return predictor.getAverageDepth();
    }
  };

}

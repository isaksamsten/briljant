package org.briljantframework.classifier.conformal.evaluation;

import org.briljantframework.Check;
import org.briljantframework.array.BooleanArray;
import org.briljantframework.array.DoubleArray;
import org.briljantframework.classifier.conformal.ConformalClassifier;
import org.briljantframework.data.dataframe.DataFrame;
import org.briljantframework.data.vector.Vector;
import org.briljantframework.data.vector.Vectors;
import org.briljantframework.evaluation.PredictionMeasure;

/**
 * @author Isak Karlsson <isak-kar@dsv.su.se>
 */
public enum ConformalClassifierMeasure implements PredictionMeasure<ConformalClassifier> {
  CONFIDENCE, CREDIBILITY;

  @Override
  public double compute(ConformalClassifier predictor, DataFrame x, Vector t) {
    return 0;
  }

  public static double accuracy(DoubleArray pvalue, Vector y, double confidence, Vector classes) {
    BooleanArray predictions = pvalue.where(v -> v >= confidence);
    double correct = 0;
    for (int i = 0; i < predictions.rows(); i++) {
      int j = Vectors.find(classes, y, i);
      Check.state(j >= 0, "Class not found.");
      if (predictions.get(i, j)) {
        correct++;
      }
    }
    return correct / predictions.rows();
  }
}

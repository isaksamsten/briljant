package org.briljantframework.evaluation.result;

import java.util.Map;

import org.briljantframework.classification.Predictor;
import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.evaluation.measure.AreaUnderCurve;
import org.briljantframework.evaluation.measure.Brier;
import org.briljantframework.matrix.DoubleMatrix;
import org.briljantframework.vector.Value;
import org.briljantframework.vector.Vector;

/**
 * @author Isak Karlsson
 */
public class ProbabilityEvaluator implements Evaluator {
  @Override
  public void accept(EvaluationContext ctx) {
    Vector actual = ctx.getPartition().getValidationTarget();
    Vector predicted = ctx.getPredictions(Sample.OUT);
    DataFrame validationData = ctx.getPartition().getValidationData();
    Predictor predictor = ctx.getPredictor();

    DoubleMatrix probabilities = predictor.estimate(validationData);
    Vector classes = predictor.getClasses();

    Map<Value, Double> auc = Measures.auc(predicted, probabilities, actual, classes);
    double brier = Measures.brier(predicted, probabilities, actual, classes);
    ctx.getOrDefault(AreaUnderCurve.class, AreaUnderCurve.Builder::new).add(Sample.OUT, auc);
    ctx.getOrDefault(Brier.class, Brier.Builder::new).add(Sample.OUT, brier);
  }
}

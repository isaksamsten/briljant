package org.briljantframework.evaluation.result;

import org.briljantframework.classification.Predictor;
import org.briljantframework.evaluation.measure.AreaUnderCurve;
import org.briljantframework.evaluation.measure.Brier;
import org.briljantframework.array.DoubleArray;
import org.briljantframework.vector.Vec;
import org.briljantframework.vector.Vector;

import java.util.Map;

/**
 * @author Isak Karlsson
 */
public class ProbabilityEvaluator implements Evaluator {

  @Override
  public void accept(EvaluationContext ctx) {
    if (!ctx.getPredictor().getCharacteristics().contains(Predictor.Characteristics.ESTIMATOR)) {
      return;
    }
    Vector actual = ctx.getPartition().getValidationTarget();
    Vector predicted = ctx.getPredictions(Sample.OUT);
    Predictor predictor = ctx.getPredictor();

    DoubleArray probabilities = ctx.getEstimation(Sample.OUT);
    Vector classes = predictor.getClasses();

    Map<Object, Double> auc = Measures.auc(predicted, probabilities, actual, classes);
    double brier = Measures.brier(predicted, probabilities, actual, classes);
    ctx.getOrDefault(AreaUnderCurve.class, AreaUnderCurve.Builder::new).add(Sample.OUT, auc);

    Map<Object, Integer> classDistribution = Vec.count(actual);
    double averageAuc = 0;
    for (Map.Entry<Object, Double> aucEntry : auc.entrySet()) {
      if (classDistribution.containsKey(aucEntry.getKey())) {
        int classCount = classDistribution.get(aucEntry.getKey());
        averageAuc += aucEntry.getValue() * (classCount / (double) actual.size());
      }
    }
    ctx.get(AreaUnderCurve.class).add(Sample.OUT, averageAuc);
    ctx.getOrDefault(Brier.class, Brier.Builder::new).add(Sample.OUT, brier);
  }
}

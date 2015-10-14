/*
 * The MIT License (MIT)
 * 
 * Copyright (c) 2015 Isak Karlsson
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.briljantframework.evaluation.classification;

import static org.briljantframework.evaluation.classification.ClassificationMeasures.areaUnderRocCurve;
import static org.briljantframework.evaluation.classification.ClassificationMeasures.brierScore;

import java.util.Map;

import org.briljantframework.Check;
import org.briljantframework.array.DoubleArray;
import org.briljantframework.classification.Classifier;
import org.briljantframework.classification.ClassifierCharacteristic;
import org.briljantframework.data.vector.Vector;
import org.briljantframework.data.vector.Vectors;
import org.briljantframework.evaluation.EvaluationContext;
import org.briljantframework.evaluation.Evaluator;
import org.briljantframework.evaluation.Sample;

/**
 * @author Isak Karlsson
 */
public class ProbabilityEvaluator implements Evaluator {

  @Override
  public void accept(EvaluationContext ctx) {
    Check.argument(ctx.getPredictor() instanceof Classifier, "requires a classifier");
    // Ignore classifiers without the ESTIMATOR characteristics
    if (!ctx.getPredictor().getCharacteristics().contains(ClassifierCharacteristic.ESTIMATOR)) {
      return;
    }
    Vector actual = ctx.getPartition().getValidationTarget();
    Vector predicted = ctx.getPredictions();
    DoubleArray probabilities = ctx.getEstimation();

    Classifier classifier = (Classifier) ctx.getPredictor();
    Vector classes = classifier.getClasses();

    Vector auc = areaUnderRocCurve(predicted, actual, probabilities, classes);
    double brier = brierScore(predicted, actual, probabilities, classes);
    ctx.getOrDefault(AreaUnderCurve.class, AreaUnderCurve.Builder::new).add(Sample.OUT, auc);

    Map<Object, Integer> classDistribution = Vectors.count(actual);
    double averageAuc = 0;
    for (Object classKey : auc) {
      if (classDistribution.containsKey(classKey)) {
        int classCount = classDistribution.get(classKey);
        averageAuc += auc.getAsDouble(classKey) * (classCount / (double) actual.size());
      } else {
        throw new IllegalStateException("Unexpected class " + classKey);
      }
    }
    ctx.getOrDefault(AreaUnderCurve.class, AreaUnderCurve.Builder::new).add(Sample.OUT, averageAuc);
    ctx.getOrDefault(Brier.class, Brier.Builder::new).add(Sample.OUT, brier);
  }

  @Override
  public String toString() {
    return "Probability evaluator";
  }
}

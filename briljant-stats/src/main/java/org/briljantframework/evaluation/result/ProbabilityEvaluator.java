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

package org.briljantframework.evaluation.result;

import java.util.Map;

import org.briljantframework.array.DoubleArray;
import org.briljantframework.classification.Classifier;
import org.briljantframework.data.vector.Vector;
import org.briljantframework.data.vector.Vectors;
import org.briljantframework.evaluation.measure.AreaUnderCurve;
import org.briljantframework.evaluation.measure.Brier;

/**
 * @author Isak Karlsson
 */
public class ProbabilityEvaluator implements Evaluator {

  @Override
  public void accept(EvaluationContext ctx) {
    if (!ctx.getPredictor().getCharacteristics().contains(Classifier.Characteristics.ESTIMATOR)) {
      return;
    }
    Vector actual = ctx.getPartition().getValidationTarget();
    Vector predicted = ctx.getPredictions(Sample.OUT);
    DoubleArray probabilities = ctx.getEstimation(Sample.OUT);

    Classifier classifier = ctx.getPredictor();
    Vector classes = classifier.getClasses();
    
    Vector auc = ClassificationMeasures.areaUnderRocCurve(predicted, actual, probabilities, classes);
    double brier = ClassificationMeasures.brier(predicted, actual, probabilities, classes);
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
    ctx.get(AreaUnderCurve.class).add(Sample.OUT, averageAuc);
    ctx.getOrDefault(Brier.class, Brier.Builder::new).add(Sample.OUT, brier);
  }

  @Override
  public String toString() {
    return "Probability evaluator";
  }
}

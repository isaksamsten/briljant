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

package org.briljantframework.classifier.evaluation;

import static org.briljantframework.classification.ClassifierMeasure.AUCROC;
import static org.briljantframework.classification.ClassifierMeasure.BRIER_SCORE;
import static org.briljantframework.classification.ClassifierMeasure.averageAreaUnderRocCurve;
import static org.briljantframework.classification.ClassifierMeasure.brierScore;

import org.briljantframework.Check;
import org.briljantframework.array.DoubleArray;
import org.briljantframework.classification.Classifier;
import org.briljantframework.classification.ClassifierCharacteristic;
import org.briljantframework.data.vector.Vector;
import org.briljantframework.evaluation.EvaluationContext;
import org.briljantframework.evaluation.Evaluator;
import org.briljantframework.evaluation.MeasureCollection;

/**
 * @author Isak Karlsson
 */
public class ProbabilityEvaluator implements Evaluator<Classifier> {

  public static final ProbabilityEvaluator INSTANCE = new ProbabilityEvaluator();

  private ProbabilityEvaluator() {}

  public static ProbabilityEvaluator getInstance() {
    return INSTANCE;
  }

  @Override
  public void accept(EvaluationContext<? extends Classifier> ctx) {
    // Ignore classifiers without the ESTIMATOR characteristics
    if (!ctx.getPredictor().getCharacteristics().contains(ClassifierCharacteristic.ESTIMATOR)) {
      return;
    }

    MeasureCollection<? extends Classifier> measures = ctx.getMeasureCollection();
    Vector actual = ctx.getPartition().getValidationTarget();
    Vector predicted = ctx.getPredictions();
    DoubleArray probabilities = ctx.getEstimates();
    Check.state(probabilities != null, "Classifier reports ESTIMATOR but the "
        + "EvaluationContext contains no probability estimates.");

    Classifier classifier = ctx.getPredictor();
    Vector classes = classifier.getClasses();

    double brier = brierScore(predicted, actual, probabilities, classes);
    double averageAuc = averageAreaUnderRocCurve(predicted, actual, probabilities, classes);
    measures.add(AUCROC, averageAuc);
    measures.add(BRIER_SCORE, brier);
  }

  @Override
  public String toString() {
    return "Probability evaluator";
  }
}

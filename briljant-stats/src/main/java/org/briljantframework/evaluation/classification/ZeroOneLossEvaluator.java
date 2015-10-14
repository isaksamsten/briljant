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

import static org.briljantframework.evaluation.classification.ClassificationMeasures.accuracy;

import org.briljantframework.Check;
import org.briljantframework.classification.Classifier;
import org.briljantframework.evaluation.EvaluationContext;
import org.briljantframework.evaluation.Evaluator;
import org.briljantframework.evaluation.Sample;

/**
 * @author Isak Karlsson
 */
public class ZeroOneLossEvaluator implements Evaluator {

  @Override
  public void accept(EvaluationContext ctx) {
    Check.argument(ctx.getPredictor() instanceof Classifier, "requires a classifier");
    double a = accuracy(ctx.getPredictions(), ctx.getPartition().getValidationTarget());
    ctx.getOrDefault(ErrorRate.class, ErrorRate.Builder::new).add(Sample.OUT, 1 - a);
    ctx.getOrDefault(Accuracy.class, Accuracy.Builder::new).add(Sample.OUT, a);
  }

  @Override
  public String toString() {
    return "0/1-loss evaluator";
  }
}

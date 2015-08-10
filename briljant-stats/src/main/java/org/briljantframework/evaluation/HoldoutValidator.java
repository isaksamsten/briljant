/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Isak Karlsson
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.briljantframework.evaluation;

import org.briljantframework.classification.Classifier;
import org.briljantframework.classification.Predictor;
import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.evaluation.result.ConfusionMatrix;
import org.briljantframework.evaluation.result.EvaluationContext;
import org.briljantframework.evaluation.result.Evaluator;
import org.briljantframework.evaluation.result.Result;
import org.briljantframework.vector.Vector;

import java.util.Collections;
import java.util.List;

/**
 * @author Isak Karlsson
 */
public class HoldoutValidator extends AbstractValidator {

  private final DataFrame holdoutX;
  private final Vector holdoutY;

  public HoldoutValidator(List<Evaluator> consumers, DataFrame holdoutX, Vector holdoutY) {
    super(consumers, null);
    this.holdoutX = holdoutX;
    this.holdoutY = holdoutY;
  }

  public static HoldoutValidator withHoldout(DataFrame x, Vector y) {
    return new HoldoutValidator(Evaluator.getDefaultClassificationEvaluators(), x, y);
  }

  @Override
  public Result test(Classifier classifier, DataFrame x, Vector y) {
    Predictor model = classifier.fit(x, y);
    return evaluate(model, x, y);
  }

  public Result evaluate(Predictor predictor, DataFrame x, Vector y) {
    Vector classes = predictor.getClasses();
    EvaluationContext ctx = new EvaluationContext();

    Vector holdOutPredictions = computeClassLabels(holdoutX, predictor, y.getType(), ctx);
    ConfusionMatrix confusionMatrix =
        ConfusionMatrix.compute(holdOutPredictions, holdoutY, classes);
    ctx.setPredictor(predictor);
    ctx.setPredictions(holdOutPredictions);
    ctx.setPartition(new Partition(x, holdoutX, y, holdoutY));

    getEvaluators().forEach(mc -> mc.accept(ctx));
    predictor.evaluation(ctx);
    return Result.create(collect(ctx.builders()), Collections.singletonList(confusionMatrix));
  }

}

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

package org.briljantframework.evaluation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.briljantframework.data.dataframe.DataFrame;
import org.briljantframework.data.vector.Vector;
import org.briljantframework.evaluation.partition.Partition;
import org.briljantframework.evaluation.partition.Partitioner;
import org.briljantframework.supervised.Predictor;

public abstract class AbstractValidator<P extends Predictor> implements Validator<P> {

  private final List<Evaluator<? super P>> evaluators;
  private final Partitioner partitioner;

  public AbstractValidator(List<? extends Evaluator<? super P>> evaluators, Partitioner partitioner) {
    this.evaluators = new ArrayList<>(evaluators);
    this.partitioner = partitioner;
  }

  @Override
  public Result<P> test(Predictor.Learner<? extends P> learner, DataFrame x, Vector y) {
    Collection<Partition> partitions = getPartitioner().partition(x, y);
    MutableEvaluationContext<P> ctx = new MutableEvaluationContext<>();
    for (Partition partition : partitions) {
      DataFrame trainingData = partition.getTrainingData();
      Vector trainingTarget = partition.getTrainingTarget();
      DataFrame validationData = partition.getValidationData();

      // Step 1: Fit the classifier using the training data
      long start = System.nanoTime();
      P predictor = learner.fit(trainingData, trainingTarget);
      double fitTime = (System.nanoTime() - start) / 1e6;

      // Step 2: Update the evaluation context
      ctx.setPredictor(predictor);
      ctx.setPartition(partition);

      // Step 3: Make predictions on the validation data
      start = System.nanoTime();
      predict(ctx);
      double predictTime = (System.nanoTime() - start) / 1e6;

      // Step 4: Compute the given measures
      EvaluationContext<P> evaluationContext = ctx.getEvaluationContext();
      evaluators.forEach(evaluator -> evaluator.accept(evaluationContext));
      // predictor.evaluate(evaluationContext);
      // predictor.evaluate(evaluationContext);
      // learner.evaluate(evaluationContext);
      // learner.evaluate(evaluationContext);
      // predictor.evaluate(evaluationContext);

      // These are evaluated for all predictors no matter what
      MeasureCollection<P> measureCollection = evaluationContext.getMeasureCollection();
      measureCollection.add(DefaultMeasure.FIT_TIME, fitTime);
      measureCollection.add(DefaultMeasure.PREDICT_TIME, predictTime);
      measureCollection.add(DefaultMeasure.TRAINING_SIZE, trainingData.rows());
      measureCollection.add(DefaultMeasure.VALIDATION_SIZE, validationData.rows());
    }


    // immutableContext.getOrDefault(TrainingSetSize.class, TrainingSetSize.Builder::new).add(
    // Sample.OUT, trainingData.rows());
    // immutableContext.getOrDefault(ValidationSetSize.class, ValidationSetSize.Builder::new).add(
    // Sample.OUT, validationData.rows());
    // immutableContext.getOrDefault(FitTime.class, FitTime.Builder::new).add(Sample.OUT, fitTime);
    // immutableContext.getOrDefault(PredictTime.class, PredictTime.Builder::new).add(Sample.OUT,
    // predictTime);
    // }
    return new Result<>(ctx.getEvaluationContext());
  }

  /**
   * Given the {@link MutableEvaluationContext#getEvaluationContext()}, set the prediction
   * 
   * @param ctx the evaluation context
   */
  protected abstract void predict(MutableEvaluationContext<P> ctx);

  @Override
  public void add(Evaluator<? super P> evaluator) {
    this.evaluators.add(evaluator);
  }

  @Override
  public Partitioner getPartitioner() {
    return partitioner;
  }
}

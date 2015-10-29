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

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.briljantframework.data.dataframe.DataFrame;
import org.briljantframework.data.vector.Vector;
import org.briljantframework.evaluation.partition.Partition;
import org.briljantframework.evaluation.partition.Partitioner;
import org.briljantframework.supervised.Predictor;

/**
 * A validator evaluates the performance of a given
 * {@linkplain org.briljantframework.supervised.Predictor.Learner learning algorithm} using a
 * specified data set. The dataset is partition using a specified {@link Partitioner} into
 * {@linkplain Partition partitions}. Finally, the validator can also be given a set of
 * {@linkplain Evaluator evaluators} responsible for measuring the performance of the given
 * predictor.
 *
 * <pre>
 * // We use 10 train and test partitions
 * Partitioner partitioner = new FoldPartitioner(10);
 * LogisticRegression.Learner learner = new LogisticRegression.Learner();
 * DataFrame iris = Datasets.loadIris();
 * DataFrame x = iris.drop(&quot;Class&quot;).apply(v -&gt; v.set(v.where(Object.class, Is::NA), v.mean()));
 * Vector y = iris.get(&quot;Class&quot;);
 * 
 * Result&lt;Classifier&gt; result = validator.test(learner, x, y);
 * DataFrame measures = result.getMeasures();
 * measures.mean();
 * </pre>
 *
 * produces, something like:
 *
 * <pre>
 * ACCURACY         0.96
 * AUCROC           0.19999999999999998
 * BRIER_SCORE      0.032292661080829614
 * ERROR            0.03999999999999999
 * FIT_TIME         42.444722999999996
 * PREDICT_TIME     0.9753513000000001
 * TRAINING_SIZE    135.0
 * VALIDATION_SIZE  15.0
 * type: double
 * </pre>
 *
 * The above specified validator can be used to evaluate any classifier (i.e. any class implementing
 * the {@link org.briljantframework.classification.Classifier} interface).
 * 
 * @param <P> the type of classifier
 */
public abstract class Validator<P extends Predictor> {

  private final Set<Evaluator<? super P>> evaluators;

  private final Partitioner partitioner;

  public Validator(Set<? extends Evaluator<? super P>> evaluators, Partitioner partitioner) {
    this.evaluators = new HashSet<>(evaluators);
    this.partitioner = partitioner;
  }

  public Validator(Partitioner partitioner) {
    this(Collections.emptySet(), partitioner);
  }

  /**
   * Evaluate {@code classifier} using the data {@code x} and {@code y}
   *
   * @param learner classifier to use for classification
   * @param x the data frame to use during evaluation
   */
  public Result<P> test(Predictor.Learner<? extends P> learner, DataFrame x, Vector y) {
    Collection<Partition> partitions = getPartitioner().partition(x, y);
    MutableEvaluationContext<P> ctx = new MutableEvaluationContext<>();
    Vector.Builder actual = y.newBuilder();
    Vector.Builder predictions = y.newBuilder();
    for (Partition partition : partitions) {
      DataFrame trainingData = partition.getTrainingData();
      Vector trainingTarget = partition.getTrainingTarget();
      DataFrame validationData = partition.getValidationData();
      ctx.setPartition(partition);

      // Step 1: Fit the classifier using the training data
      long start = System.nanoTime();
      fit(learner, ctx);
      double fitTime = (System.nanoTime() - start) / 1e6;

      // Step 3: Make predictions on the validation data
      start = System.nanoTime();
      predict(ctx);
      double predictTime = (System.nanoTime() - start) / 1e6;

      // Step 4: Compute the given measures
      EvaluationContext<P> evaluationContext = ctx.getEvaluationContext();
      evaluators.forEach(evaluator -> evaluator.accept(evaluationContext));

      actual.addAll(partition.getValidationTarget());
      predictions.addAll(evaluationContext.getPredictions());

      // These are evaluated for all predictors no matter what
      MeasureCollection<P> measureCollection = evaluationContext.getMeasureCollection();
      measureCollection.add(DefaultMeasure.FIT_TIME, fitTime);
      measureCollection.add(DefaultMeasure.PREDICT_TIME, predictTime);
      measureCollection.add(DefaultMeasure.TRAINING_SIZE, trainingData.rows());
      measureCollection.add(DefaultMeasure.VALIDATION_SIZE, validationData.rows());
    }

    return new Result<>(ctx.getEvaluationContext(), actual.build(), predictions.build());
  }

  protected void fit(Predictor.Learner<? extends P> learner, MutableEvaluationContext<? super P> ctx) {
    Partition p = ctx.getPartition();
    ctx.setPredictor(learner.fit(p.getTrainingData(), p.getTrainingTarget()));
  }

  protected void predict(MutableEvaluationContext<? extends P> ctx) {
    ctx.setPredictions(ctx.getPredictor().predict(ctx.getPartition().getValidationData()));
  }

  /**
   * Returns true if the validator contains the specified evaluator
   * 
   * @param evaluator the evaluator
   * @return true if the validator contains the specified evaluator
   */
  public final boolean contains(Evaluator<? super P> evaluator) {
    return evaluators.contains(evaluator);
  }

  /**
   * Remove the specified evaluator from this validator
   * 
   * @param evaluator the evaluator to remove
   * @return boolean if the validator contained the specified evaluator
   */
  public final boolean remove(Evaluator<? super P> evaluator) {
    return evaluators.remove(evaluator);
  }

  /**
   * Remove all evaluator in this validator
   */
  public final void clear() {
    evaluators.clear();
  }

  /**
   * Add an evaluator to the validator for computing additional measures.
   *
   * <pre>
   * Validator&lt;Classifier&gt; cv = ClassifierValidator.crossValidation(10);
   * cv.add((ctx) -&gt; System.out.println(&quot;New round&quot;));
   * // For each fold, print &quot;New round&quot; to std-out
   * </pre>
   *
   * @param evaluator the evaluator
   */
  public final void add(Evaluator<? super P> evaluator) {
    this.evaluators.add(evaluator);
  }

  /**
   * Gets the partitioner used for this validator. The partitioner partitions the data into training
   * and validation folds. For example,
   * {@link org.briljantframework.evaluation.partition.FoldPartitioner} partitions the data into
   * {@code k} folds and {@link org.briljantframework.evaluation.partition.SplitPartitioner}
   * partitions the data into one fold.
   *
   * @return the partitioner used by this validator
   */
  public final Partitioner getPartitioner() {
    return partitioner;
  }
}

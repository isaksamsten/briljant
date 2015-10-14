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

import org.briljantframework.array.Arrays;
import org.briljantframework.array.DoubleArray;
import org.briljantframework.classification.Classifier;
import org.briljantframework.classification.ClassifierCharacteristic;
import org.briljantframework.data.dataframe.DataFrame;
import org.briljantframework.data.vector.Vector;
import org.briljantframework.data.vector.VectorType;
import org.briljantframework.evaluation.partition.Partition;
import org.briljantframework.evaluation.partition.Partitioner;
import org.briljantframework.supervised.Predictor;

public class DefaultValidator implements Validator {

  private final List<Evaluator> evaluators;
  private final Partitioner partitioner;

  public DefaultValidator(List<Evaluator> evaluators, Partitioner partitioner) {
    this.evaluators = new ArrayList<>(evaluators);
    this.partitioner = partitioner;
  }

  public DefaultValidator(Partitioner partitioner) {
    this(new ArrayList<>(), partitioner);
  }

  @Override
  public Result test(Predictor.Learner classifier, DataFrame x, Vector y) {
    Collection<Partition> partitions = getPartitioner().partition(x, y);
    EvaluationContextImpl ctx = new EvaluationContextImpl();
    for (Partition partition : partitions) {
      DataFrame trainingData = partition.getTrainingData();
      Vector trainingTarget = partition.getTrainingTarget();
      DataFrame validationData = partition.getValidationData();

      // Step 1: Fit the classifier using the training data
      long start = System.nanoTime();
      Predictor predictor = classifier.fit(trainingData, trainingTarget);
      double fitTime = (System.nanoTime() - start) / 1e6;

      // Step 2: Make predictions on the validation data
      start = System.nanoTime();
      // TODO: refactor to an injected dependency
      predictOrEstimate(validationData, predictor, y.getType(), ctx);
      double predictTime = (System.nanoTime() - start) / 1e6;

      // Step 3: Update the evaluation context
      ctx.setPredictor(predictor);
      ctx.setPartition(partition);

      // Step 4: Compute the given measures
      getEvaluators().forEach(evaluator -> evaluator.accept(ctx));
      predictor.evaluate(ctx);

      // These are evaluated for all predictors no matter what
      ctx.getOrDefault(TrainingSetSize.class, TrainingSetSize.Builder::new).add(Sample.OUT,
          trainingData.rows());
      ctx.getOrDefault(ValidationSetSize.class, ValidationSetSize.Builder::new).add(Sample.OUT,
          validationData.rows());
      ctx.getOrDefault(FitTime.class, FitTime.Builder::new).add(Sample.OUT, fitTime);
      ctx.getOrDefault(PredictTime.class, PredictTime.Builder::new).add(Sample.OUT, predictTime);
    }
    return new Result(ctx);
  }

  /**
   * Chooses the 'best' strategy to avoid computing the probability estimation array twice. If the
   * given classifier provides the {@link ClassifierCharacteristic#ESTIMATOR} characteristic, the
   * {@link EvaluationContextImpl#setEstimation(DoubleArray)} method is called with the probability
   * estimates and the predictions are computed and returned. If not, only the predictions are set.
   *
   * @param predictor the classifier
   * @param type the the resulting vector type
   * @param ctx the evaluation context
   */
  protected void predictOrEstimate(DataFrame x, Predictor predictor, VectorType type,
      EvaluationContextImpl ctx) {
    Vector.Builder builder = type.newBuilder();

    // For the case where the classifier reports the ESTIMATOR characteristic
    // improve the performance by avoiding to recompute the classifications twice.
    if (predictor instanceof Classifier
        && predictor.getCharacteristics().contains(ClassifierCharacteristic.ESTIMATOR)) {
      Classifier classifier1 = (Classifier) predictor;
      Vector classes = classifier1.getClasses();
      DoubleArray estimate = classifier1.estimate(x);
      ctx.setEstimation(estimate);
      for (int i = 0; i < estimate.rows(); i++) {
        builder.loc().set(i, classes, Arrays.argmax(estimate.getRow(i)));
      }
      ctx.setPredictions(builder.build());
    } else {
      ctx.setPredictions(predictor.predict(x));
    }
  }

  @Override
  public void add(Evaluator evaluator) {
    this.evaluators.add(evaluator);
  }

  protected List<Evaluator> getEvaluators() {
    return evaluators;
  }

  @Override
  public Partitioner getPartitioner() {
    return partitioner;
  }
}

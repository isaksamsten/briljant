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
import java.util.List;

import org.briljantframework.array.Arrays;
import org.briljantframework.array.DoubleArray;
import org.briljantframework.classification.Classifier;
import org.briljantframework.data.dataframe.DataFrame;
import org.briljantframework.data.vector.Vector;
import org.briljantframework.data.vector.VectorType;
import org.briljantframework.evaluation.partition.Partitioner;
import org.briljantframework.evaluation.result.EvaluationContext;
import org.briljantframework.evaluation.result.Evaluator;

/**
 * @author Isak Karlsson
 */
public abstract class AbstractValidator implements Validator {

  private final List<Evaluator> evaluators;
  private final Partitioner partitioner;

  protected AbstractValidator(List<Evaluator> evaluators, Partitioner partitioner) {
    this.evaluators = new ArrayList<>(evaluators);
    this.partitioner = partitioner;
  }

  @Override
  public List<Evaluator> getEvaluators() {
    return evaluators;
  }

  /**
   * Gets the partition strategy
   *
   * @return the partition strategy
   */
  @Override
  public Partitioner getPartitioner() {
    return partitioner;
  }

  /**
   * Computes the class labels. Chooses the 'best' strategy to avoid computing the probability
   * estimation matrix twice.
   *
   * @param classifier the classifier
   * @param type the the resulting vector type
   * @param ctx the evaluation context
   * @return a vector of class-labels produced for {@code classifier} using the hold-out dataset
   */
  protected Vector computeClassLabels(DataFrame x, Classifier classifier, VectorType type,
      EvaluationContext ctx) {
    Vector classes = classifier.getClasses();
    Vector.Builder builder = type.newBuilder();

    // For the case where the classifier reports the ESTIMATOR characteristic
    // improve the performance by avoiding to recompute the classifications twice.
    if (classifier.getCharacteristics().contains(Classifier.Characteristics.ESTIMATOR)) {
      DoubleArray estimate = classifier.estimate(x);
      ctx.setEstimation(estimate);
      for (int i = 0; i < estimate.rows(); i++) {
        builder.loc().set(i, classes, Arrays.argmax(estimate.getRow(i)));
      }
      return builder.build();
    } else {
      return classifier.predict(x);
    }
  }

  @Override
  public String toString() {
    return "AbstractValidator{" + "evaluators=" + evaluators + ", partitioner=" + partitioner + '}';
  }
}

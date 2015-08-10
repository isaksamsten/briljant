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

import org.briljantframework.classification.Predictor;
import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.evaluation.measure.Measure;
import org.briljantframework.evaluation.result.EvaluationContext;
import org.briljantframework.evaluation.result.Evaluator;
import org.briljantframework.array.DoubleArray;
import org.briljantframework.vector.Vector;
import org.briljantframework.vector.VectorType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.briljantframework.Bj.argmax;

/**
 * Created by isak on 03/10/14.
 * <p>
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
   * Collect metric producers.
   *
   * @param builders the producers
   * @return the array list
   */
  protected List<Measure> collect(Collection<Measure.Builder<?>> builders) {
    List<Measure> measures = new ArrayList<>();
    for (Measure.Builder<?> builder : builders) {
      measures.add(builder.build());
    }
    return measures;
  }

  /**
   * Computes the class labels. Chooses the 'best' strategy to avoid computing the probability
   * estimation matrix twice.
   *
   * @param predictor the predictor
   * @param type      the the resulting vector type
   * @param ctx       the evaluation context
   * @return a vector of class-labels produced for {@code predictor} using the hold-out dataset
   */
  protected Vector computeClassLabels(DataFrame holdoutX, Predictor predictor, VectorType type,
                                      EvaluationContext ctx) {
    Vector classes = predictor.getClasses();
    Vector.Builder builder = type.newBuilder();
    if (predictor.getCharacteristics().contains(Predictor.Characteristics.ESTIMATOR)) {
      DoubleArray estimate = predictor.estimate(holdoutX);
      ctx.setEstimation(estimate);
      for (int i = 0; i < estimate.rows(); i++) {
        builder.set(i, classes, argmax(estimate.getRow(i)));
      }
      return builder.build();
    } else {
      return predictor.predict(holdoutX);
    }
  }
}

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

package org.briljantframework.evaluation.result;

import org.briljantframework.array.DoubleArray;
import org.briljantframework.classification.Predictor;
import org.briljantframework.evaluation.Partition;
import org.briljantframework.evaluation.measure.Measure;
import org.briljantframework.vector.Vector;

import java.util.Collection;
import java.util.HashMap;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * @author Isak Karlsson
 */
public class EvaluationContext {

  private final HashMap<Class<?>, Measure.Builder<?>> builders = new HashMap<>();

  private Vector predictions;
  private Predictor predictor;
  private Partition partition;
  private DoubleArray estimation;

  public EvaluationContext() {
  }

  public void setPredictions(Vector predictions) {
    this.predictions = Objects.requireNonNull(predictions);
  }

  /**
   * Get the partition representing the training and the evaluation data.
   *
   * @return the partitions
   */
  public Partition getPartition() {
    return partition;
  }

  public void setPartition(Partition partition) {
    this.partition = Objects.requireNonNull(partition);
  }

  /**
   * Get the predictions made by {@link #getPredictor()}.
   *
   * @param sample if {@link Sample#IN}, returns the predictions on
   *               {@link org.briljantframework.evaluation.Partition#getTrainingData()}; if
   *               {@link Sample#OUT}, returns the predictions on
   *               {@link org.briljantframework.evaluation.Partition#getTrainingData()}.
   * @return the predictions
   */
  public Vector getPredictions(Sample sample) {
    return sample == Sample.OUT ? predictions : getPredictor().predict(
        getPartition().getTrainingData());
  }

  public void setEstimation(DoubleArray estimation) {
    this.estimation = Objects.requireNonNull(estimation);
  }

  /**
   * If the predictor returned by {@link #getPredictor()} has the
   * {@link org.briljantframework.classification.Predictor.Characteristics#ESTIMATOR}
   * characteristic
   *
   * @param sample the sample
   * @return the probability estimations made by predictor; shape [no samples, domain]
   */
  public DoubleArray getEstimation(Sample sample) {
    return sample == Sample.OUT ? estimation : getPredictor().estimate(
        getPartition().getTrainingData());
  }

  /**
   * Returns the evaluated predictor
   *
   * @return the predictor under evaluation
   */
  public Predictor getPredictor() {
    return predictor;
  }

  public void setPredictor(Predictor predictor) {
    this.predictor = Objects.requireNonNull(predictor);
  }

  /**
   * Get the measure builder for {@code measure}. Prefer,
   * <p>
   *
   * <pre>
   *     // Good
   *     ctx.getOrDefault(Accuracy.class, Accuracy.Builder::new).add(0.3);
   *
   *     // Bad
   *     Measure.Builder b = ctx.get(Accuracy.class);
   *     if(b == null) {
   *         b = new Accuracy.Builder();
   *         ctx.put(Accuracy.class, b);
   *     }
   *     b.add(0.3)
   * </pre>
   *
   * @param measure the measure
   * @return a measure builder; or {@code null}.
   */
  @SuppressWarnings("unchecked")
  public <T extends Measure> Measure.Builder<T> get(Class<T> measure) {
    return (Measure.Builder<T>) builders.get(measure);
  }

  public <T extends Measure> Measure.Builder<T> getOrDefault(Class<T> measure,
                                                             Supplier<? extends Measure.Builder<T>> supplier) {
    Measure.Builder<T> builder = get(measure);
    if (builder == null) {
      builder = supplier.get();
      builders.put(measure, builder);
    }

    return builder;
  }

  public boolean containsKey(Class<? extends Measure> measure) {
    return builders.containsKey(measure);
  }

  public <T extends Measure> void put(Class<T> measure, Measure.Builder<T> builder) {
    builders.put(measure, builder);
  }

  public Collection<Measure.Builder<?>> builders() {
    return builders.values();
  }
}

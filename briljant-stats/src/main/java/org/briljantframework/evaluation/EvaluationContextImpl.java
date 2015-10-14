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
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

import org.briljantframework.Check;
import org.briljantframework.array.DoubleArray;
import org.briljantframework.data.vector.Vector;
import org.briljantframework.evaluation.partition.Partition;
import org.briljantframework.supervised.Predictor;

/**
 * @author Isak Karlsson
 */
class EvaluationContextImpl implements EvaluationContext {

  private final HashMap<Class<?>, Measure.Builder<?>> builders = new HashMap<>();

  private Vector predictions;
  private Predictor predictor;
  private Partition partition;
  private DoubleArray estimation;

  public EvaluationContextImpl() {}

  /**
   * Get the partition representing the training and the evaluation data.
   *
   * @return the partitions
   */
  @Override
  public Partition getPartition() {
    Check.state(partition != null, "no partition");
    return partition;
  }

  public void setPartition(Partition partition) {
    this.partition = Objects.requireNonNull(partition, "requires a partition");
  }

  /**
   * Set the out of sample predictions made by the predictor
   * 
   * @param predictions the out of sample predictions
   */
  public void setPredictions(Vector predictions) {
    this.predictions = Objects.requireNonNull(predictions, "requires predictions");
  }

  /**
   * Get the predictions made by {@link #getPredictor()}.
   *
   * @return the predictions
   */
  @Override
  public Vector getPredictions() {
    Check.state(predictions != null, "no predictions");
    return predictions;
  }

  /**
   * Set the out-of-sample probability estimates
   * 
   * @param estimation the probability estimates
   */
  public void setEstimation(DoubleArray estimation) {
    this.estimation = Objects.requireNonNull(estimation, "requires an estimation");
  }

  /**
   * Get the probability estimates from this
   * 
   * @return the probability estimations made by classifier; shape [no samples, domain]
   */
  @Override
  public DoubleArray getEstimation() {
    Check.state(estimation != null, "no probability estimates");
    return estimation;
  }

  /**
   * Returns the evaluated classifier
   *
   * @return the classifier under evaluation
   */
  @Override
  public Predictor getPredictor() {
    Check.state(predictor != null, "no predictor");
    return predictor;
  }

  /**
   * Set the predictor
   * 
   * @param predictor the predictor
   */
  public void setPredictor(Predictor predictor) {
    this.predictor = Objects.requireNonNull(predictor, "requires a predictor");
  }

  /**
   * Get the measure builder for {@code measure}. Prefer,
   * <p>
   *
   * <pre>
   * // Good
   * ctx.getOrDefault(Accuracy.class, Accuracy.Builder::new).add(0.3);
   * </pre>
   *
   * @param measure the measure
   * @return a measure builder; or {@code null}.
   */
  @SuppressWarnings("unchecked")
  private <T extends Measure, C extends Measure.Builder<T>> C get(Class<T> measure) {
    return (C) builders.get(measure);
  }

  /**
   * Get the builder for the key or the default value produced by the supplier
   *
   * TODO: this method is unsafe. We need an alternative here.
   * 
   * @param measure the measure
   * @param supplier the supplier
   * @param <T> the type of measure
   * @return a builder for the measure
   */
  @Override
  public <T extends Measure, C extends Measure.Builder<T>> C getOrDefault(Class<T> measure,
      Supplier<C> supplier) {
    C builder = get(measure);
    if (builder == null) {
      builder = supplier.get();
      builders.put(measure, builder);
    }

    return builder;
  }

  private Collection<Measure.Builder<?>> getMeasureBuilders() {
    return builders.values();
  }

  /**
   * Get a list of populated measures from this evaluation context
   *
   * <pre>
   * {@code
   *  EvaluationContextImpl ctx = new EvaluationContextImpl();
   *  ctx.getOrDefault(Accuracy.class, Accuracy.Builder::new).add(0.9);
   *  ctx.append(Accuracy.class, 0.9);
   *  ctx.append(Accuracy.class, 0.8);
   *  ....
   *  List<Measure> measures = ctx.getMeasures();
   * }
   * </pre>
   *
   * @return a list of measures
   */
  @Override
  public List<Measure> getMeasures() {
    List<Measure> measures = new ArrayList<>();
    getMeasureBuilders().forEach(v -> measures.add(v.build()));
    return measures;
  }
}

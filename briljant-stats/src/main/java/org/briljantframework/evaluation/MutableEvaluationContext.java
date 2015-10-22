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

import java.util.Objects;

import org.briljantframework.array.DoubleArray;
import org.briljantframework.data.vector.Vector;
import org.briljantframework.evaluation.partition.Partition;
import org.briljantframework.supervised.Predictor;

/**
 * @author Isak Karlsson
 */
public class MutableEvaluationContext<P extends Predictor> {

  private final ImmutableEvaluationContext evaluationContext = new ImmutableEvaluationContext();

  private Vector predictions;
  private P predictor;
  private Partition partition;
  private DoubleArray estimation;

  public MutableEvaluationContext() {}

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
   * Set the out-of-sample probability estimates
   * 
   * @param estimation the probability estimates
   */
  public void setEstimation(DoubleArray estimation) {
    this.estimation = Objects.requireNonNull(estimation, "requires an estimation");
  }

  /**
   * Set the predictor
   * 
   * @param predictor the predictor
   */
  public void setPredictor(P predictor) {
    this.predictor = Objects.requireNonNull(predictor, "requires a predictor");
  }

  /**
   * Get the predictor
   * 
   * @return the predictor
   */
  public P getPredictor() {
    return predictor;
  }

//  /**
//   * Get the measure builder for {@code measure}. Prefer,
//   * <p>
//   *
//   * <pre>
//   * // Good
//   * ctx.getOrDefault(Accuracy.class, Accuracy.Builder::new).add(0.3);
//   * </pre>
//   *
//   * @param measure the measure
//   * @return a measure builder; or {@code null}.
//   */
//  @SuppressWarnings("unchecked")
//  private <T extends Measure, C extends Measure.Builder<T>> C get(Class<T> measure) {
//    return (C) builders.get(measure);
//  }

  public EvaluationContext<P> getEvaluationContext() {
    return evaluationContext;
  }

//  private Collection<Measure.Builder<?>> getMeasureBuilders() {
//    return builders.values();
//  }

  private class ImmutableEvaluationContext implements EvaluationContext<P> {

    private final MeasureCollection<P> measureCollection = new MeasureCollection<>();

    @Override
    public Partition getPartition() {
      return partition;
    }

    @Override
    public Vector getPredictions() {
      return predictions;
    }

    @Override
    public DoubleArray getEstimates() {
      return estimation;
    }

    // @Override
    // public <T extends Measure, C extends Measure.Builder<T>> C getOrDefault(Class<T> measure,
    // Supplier<C> supplier) {
    // C builder = get(measure);
    // if (builder == null) {
    // builder = supplier.get();
    // builders.put(measure, builder);
    // }
    //
    // return builder;
    // }

    @Override
    public P getPredictor() {
      return predictor;
    }

    // @Override
    // public List<Measure> getMeasures() {
    // List<Measure> measures = new ArrayList<>();
    // getMeasureBuilders().forEach(v -> measures.add(v.build()));
    // return measures;
    // }

    @Override
    public MeasureCollection<P> getMeasureCollection() {
      return measureCollection;
    }
  }
}

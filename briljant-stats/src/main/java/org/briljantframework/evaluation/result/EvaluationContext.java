package org.briljantframework.evaluation.result;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.function.Supplier;

import org.briljantframework.classification.Predictor;
import org.briljantframework.evaluation.Partition;
import org.briljantframework.evaluation.measure.Measure;
import org.briljantframework.array.DoubleArray;
import org.briljantframework.vector.Vector;

/**
 * @author Isak Karlsson
 */
public class EvaluationContext {

  private final HashMap<Class<?>, Measure.Builder<?>> builders = new HashMap<>();

  private Vector predictions;
  private Predictor predictor;
  private Partition partition;
  private DoubleArray estimation;

  public EvaluationContext() {}

  public void setPredictions(Vector predictions) {
    this.predictions = checkNotNull(predictions);
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
    this.partition = checkNotNull(partition);
  }

  /**
   * Get the predictions made by {@link #getPredictor()}.
   *
   * @param sample if {@link Sample#IN}, returns the predictions on
   *        {@link org.briljantframework.evaluation.Partition#getTrainingData()}; if
   *        {@link Sample#OUT}, returns the predictions on
   *        {@link org.briljantframework.evaluation.Partition#getTrainingData()}.
   * @return the predictions
   */
  public Vector getPredictions(Sample sample) {
    return sample == Sample.OUT ? predictions : getPredictor().predict(
        getPartition().getTrainingData());
  }

  public void setEstimation(DoubleArray estimation) {
    this.estimation = checkNotNull(estimation);
  }

  /**
   * If the predictor returned by {@link #getPredictor()} has the
   * {@link org.briljantframework.classification.Predictor.Characteristics#ESTIMATOR} characteristic
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
    this.predictor = checkNotNull(predictor);
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

package org.briljantframework.evaluation;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.briljantframework.evaluation.result.Measure;
import org.briljantframework.evaluation.result.MeasureProvider;

/**
 * Created by isak on 03/10/14.
 * <p>
 */
public abstract class AbstractClassificationEvaluator implements ClassificationEvaluator {

  private final MeasureProvider measureProvider;
  private final Partitioner partitioner;

  protected AbstractClassificationEvaluator(MeasureProvider measureProvider, Partitioner partitioner) {
    this.measureProvider = measureProvider;
    this.partitioner = partitioner;
  }

  public MeasureProvider getMeasureProvider() {
    return measureProvider;
  }

  /**
   * Gets the partition strategy
   * 
   * @return the partition strategy
   */
  protected Partitioner getPartitioner() {
    return partitioner;
  }

  /**
   * Collect metric producers.
   *
   * @param builders the producers
   * @return the array list
   */
  protected ArrayList<Measure> collect(List<Measure.Builder> builders) {
    return collect(builders, ArrayList::new);
  }

  protected <T extends List<Measure>> T collect(List<Measure.Builder> builders, Supplier<T> supplier) {
    return builders.stream().map(Measure.Builder::build).collect(Collectors.toCollection(supplier));
  }
}

package org.briljantframework.evaluation;

import java.util.List;
import java.util.stream.Collectors;

import org.briljantframework.evaluation.result.Metric;

/**
 * Created by isak on 03/10/14.
 * <p>
 */
public abstract class AbstractEvaluator implements Evaluator {
  private final List<? extends Metric.Factory> producers;
  private final Partitioner partitioner;

  protected AbstractEvaluator(List<? extends Metric.Factory> producers, Partitioner partitioner) {
    this.producers = producers;
    this.partitioner = partitioner;
  }

  /**
   * Gets metric producers.
   *
   * @return the metric producers
   */
  protected List<Metric.Producer> getMetricProducers() {
    return getMetricFactories().stream().map(Metric.Factory::newProducer)
        .collect(Collectors.toList());
  }

  /**
   * Gets metric factories.
   *
   * @return the metric factories
   */
  protected List<? extends Metric.Factory> getMetricFactories() {
    return producers;
  }

  /**
   * Gets the partition strategy
   * 
   * @return the partition strategy
   */
  protected Partitioner getPartitioner() {
    return partitioner;
  }
}

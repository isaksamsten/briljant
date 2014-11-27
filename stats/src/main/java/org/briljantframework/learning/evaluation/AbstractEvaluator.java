package org.briljantframework.learning.evaluation;

import org.briljantframework.learning.evaluation.result.Metric;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by isak on 03/10/14.
 * <p>
 */
public abstract class AbstractEvaluator implements Evaluator {
    private final List<Metric.Factory> producers;

    protected AbstractEvaluator(List<Metric.Factory> producers) {
        this.producers = producers;
    }

    /**
     * Gets metric producers.
     *
     * @return the metric producers
     */
    protected List<Metric.Producer> getMetricProducers() {
        return getMetricFactories()
                .stream()
                .map(Metric.Factory::newProducer)
                .collect(Collectors.toList());
    }

    /**
     * Gets metric factories.
     *
     * @return the metric factories
     */
    protected List<Metric.Factory> getMetricFactories() {
        return producers;
    }
}

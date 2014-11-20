package org.briljantframework.learning.evaluation;

import org.briljantframework.data.DataFrame;
import org.briljantframework.data.column.Column;
import org.briljantframework.learning.evaluation.result.Metric;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by isak on 03/10/14.
 * <p>
 * TODO(isak) - cannot reuse (it relies on mutable state FIXME)
 *
 * @param <T> the type parameter
 */
public abstract class AbstractEvaluator<D extends DataFrame<?>, T extends Column> implements Evaluator<D, T> {
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

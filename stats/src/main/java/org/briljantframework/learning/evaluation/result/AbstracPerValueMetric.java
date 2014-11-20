/*
 * ADEB - machine learning pipelines made easy
 * Copyright (C) 2014  Isak Karlsson
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.briljantframework.learning.evaluation.result;

import org.briljantframework.data.column.Column;
import org.briljantframework.data.values.Value;
import org.briljantframework.learning.Predictions;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Isak Karlsson on 08/10/14.
 */
public abstract class AbstracPerValueMetric extends AbstractMetric implements PerValueMetric {

    /**
     * The Labels.
     */
    protected final EnumMap<Sample, Set<Value>> labels;
    /**
     * The Value for value.
     */
    protected final EnumMap<Sample, List<Map<Value, Double>>> valueForValue;


    /**
     * Instantiates a new Abstract metric.
     *
     * @param producer the producer
     */

    protected AbstracPerValueMetric(AbstracPerValueMetric.Producer producer) {
        super(producer);
        this.labels = producer.sampleLabels;
        this.valueForValue = producer.sampleMetricValues;
    }

    public JFreeChart getPerValueChart() {
        JFreeChart chart = new JFreeChart(getName(), getPerValuePlot());
        ChartFactory.getChartTheme().apply(chart);
        return chart;
    }

    public Plot getPerValuePlot() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (Value label : getLabels(Sample.OUT)) {
            for (int i = 0; i < size(); i++) {
                List<Double> outValues = get(Sample.OUT, label);
                dataset.addValue(outValues.get(i), label, String.valueOf(i));
            }
            dataset.addValue(getAverage(Sample.OUT, label), label, "Average");
        }

        NumberAxis numberAxis = new NumberAxis(getName());
        BarRenderer barRenderer = new BarRenderer();
        return new CategoryPlot(dataset, new CategoryAxis("Result"), numberAxis, barRenderer);
    }

    @Override
    public List<Double> get(Sample sample, Value value) {
        List<Map<Value, Double>> valueForValue = this.valueForValue.get(sample);

        return valueForValue.stream()
                .map(x -> x.getOrDefault(value, 0.0))
                .collect(Collectors.toList());
    }

    @Override
    public double getAverage(Sample sample, Value value) {
        if (!getLabels(sample).contains(value)) {
            throw new IllegalArgumentException(String.format("Average not calculate for value %s", value));
        }
        List<Map<Value, Double>> valueForValue = this.valueForValue.get(sample);
        return valueForValue.stream()
                .mapToDouble(x -> x.getOrDefault(value, 0.0))
                .summaryStatistics()
                .getAverage();
    }

    @Override
    public double getStandardDeviation(Sample sample, Value value) {
        double mean = getAverage(sample, value);
        double std = 0.0;
        for (double d : get(sample, value)) {
            double r = d - mean;
            std += r * r;
        }

        return size() > 1 ? Math.sqrt(std / size() - 1) : 0.0;
    }

    @Override
    public double getMin(Sample sample, Value value) {
        if (!getLabels().contains(value)) {
            throw new IllegalArgumentException(String.format("Min not calculate for value %s", value));
        }
        List<Map<Value, Double>> valueForValue = this.valueForValue.get(sample);
        return valueForValue.stream()
                .mapToDouble(x -> x.getOrDefault(value, 0.0))
                .summaryStatistics()
                .getMin();
    }

    @Override
    public double getMax(Sample sample, Value value) {
        if (!getLabels().contains(value)) {
            throw new IllegalArgumentException(String.format("Max not calculate for value %s", value));
        }
        List<Map<Value, Double>> valueForValue = this.valueForValue.get(sample);
        return valueForValue.stream()
                .mapToDouble(x -> x.getOrDefault(value, 0.0))
                .summaryStatistics()
                .getMax();
    }

    @Override
    public Set<Value> getLabels(Sample sample) {
        return labels.get(sample);
    }

    /**
     * The type Producer.
     */
    protected static abstract class Producer extends AbstractMetric.Producer {

        /**
         * The Metric values.
         */
        protected final EnumMap<Sample, List<Map<Value, Double>>> sampleMetricValues = new EnumMap<>(Sample.class);

        /**
         * The Labels.
         */
        protected final EnumMap<Sample, Set<Value>> sampleLabels = new EnumMap<>(Sample.class);

        @Override
        public Metric.Producer add(Sample sample, Predictions predictions, Column targets) {
            if (targets.getType().isNumeric()) {
                throw new IllegalArgumentException("Can't calculate per-value metrics for numerical targets");
            }

            Map<Value, Double> valueMetrics = new HashMap<>();
            Set<Value> labels = targets.getType().getDomain();

            double average = 0.0;
            for (Value value : labels) {
                double metricForValue = calculateMetricForValue(value, predictions, targets);
                valueMetrics.put(value, metricForValue);
                average += metricForValue;
            }

            List<Map<Value, Double>> metricValues = sampleMetricValues.get(sample);
            if (metricValues == null) {
                metricValues = new ArrayList<>();
                sampleMetricValues.put(sample, metricValues);
            }
            metricValues.add(valueMetrics);

            Set<Value> sampleLabels = this.sampleLabels.get(sample);
            if (sampleLabels == null) {
                sampleLabels = new HashSet<>();
                this.sampleLabels.put(sample, sampleLabels);
            }
            sampleLabels.addAll(labels);

            add(sample, average / labels.size());
            return this;
        }

        /**
         * Calculate a metric for the value {@code value}.
         *
         * @param value       the value
         * @param predictions the predictions
         * @param column      the target
         * @return the double
         */
        protected abstract double calculateMetricForValue(Value value, Predictions predictions, Column column);
    }


}

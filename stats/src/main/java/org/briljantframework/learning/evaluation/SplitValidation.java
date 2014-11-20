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

package org.briljantframework.learning.evaluation;

import com.google.common.base.Preconditions;
import org.briljantframework.data.DataFrame;
import org.briljantframework.data.column.Column;
import org.briljantframework.learning.*;
import org.briljantframework.learning.evaluation.result.ConfusionMatrix;
import org.briljantframework.learning.evaluation.result.Metric;
import org.briljantframework.learning.evaluation.result.Metrics;
import org.briljantframework.learning.evaluation.result.Result;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mostly a mockup so far
 * <p>
 * Created by Isak Karlsson on 20/08/14.
 *
 * @param <D> the type parameter
 * @param <T> the type parameter
 */
public class SplitValidation<D extends DataFrame<?>, T extends Column> extends AbstractEvaluator<D, T> {

    private final double testRatio;

    /**
     * Instantiates a new Split evaluator.
     *
     * @param testRatio the test ratio
     */
    public SplitValidation(double testRatio) {
        super(Metrics.CLASSIFICATION);
        Preconditions.checkArgument(testRatio > 0 && testRatio < 1, "Test ratio must be in range )0, 1(.");
        this.testRatio = testRatio;
    }

    /**
     * Create split evaluator.
     *
     * @param <T> the type parameter
     * @return the split evaluator
     */
    public static <D extends DataFrame<?>, T extends Column> SplitValidation<D, T> create() {
        return new SplitValidation<>(0.3);
    }

    /**
     * Create split evaluator.
     *
     * @param <T>       the type parameter
     * @param testRatio the test ratio
     * @return the split evaluator
     */
    public static <D extends DataFrame<?>, T extends Column> SplitValidation<D, T> withTestRatio(double testRatio) {
        return new SplitValidation<>(testRatio);
    }

    @Override
    public Result evaluate(Classifier<?, ? super D, ? super T> classifier, SupervisedDataset<? extends D, ? extends T> supervisedDataset) {
        SupervisedDatasetSplit<D, T> partitions = SupervisedDatasetSplit.withFraction(supervisedDataset, testRatio);
        SupervisedDataset<D, T> train = partitions.getTrainingSet();
        SupervisedDataset<D, T> test = partitions.getValidationSet();

        Model<?, ? super D> model = classifier.fit(train.getDataFrame(), train.getTarget());
        Predictions outSamplePredictions = model.predict(test.getDataFrame());
        Predictions inSamplePredictions = model.predict(train.getDataFrame());

        ConfusionMatrix confusionMatrix = ConfusionMatrix.create(outSamplePredictions, test.getTarget());
        List<Metric> metrics = getMetricFactories().stream().map(Metric.Factory::newProducer).map(producer -> {
            producer.add(Metric.Sample.IN, inSamplePredictions, train.getTarget());
            producer.add(Metric.Sample.OUT, outSamplePredictions, test.getTarget());
            return producer.produce();
        }).collect(Collectors.toCollection(ArrayList::new));

        return Result.create(metrics, Arrays.asList(confusionMatrix));
    }

    @Override
    public String toString() {
        return String.format("Split validation with test ratio = %s", getTestRatio());
    }

    /**
     * Gets test ratio.
     *
     * @return the test ratio
     */
    public double getTestRatio() {
        return testRatio;
    }
}

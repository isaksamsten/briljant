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

package org.briljantframework.learning.tree;

import org.briljantframework.Utils;
import org.briljantframework.data.DataFrame;
import org.briljantframework.data.column.CategoricColumn;
import org.briljantframework.data.values.Numeric;
import org.briljantframework.data.values.Value;
import org.briljantframework.learning.example.Example;
import org.briljantframework.learning.example.Examples;

/**
 * NOTE: This cannot be reused among trees (it is stateful for performance reasons)
 * <p>
 * Created by Isak Karlsson on 09/09/14.
 */
public class RandomSplitter extends AbstractSplitter<DataFrame<?>, CategoricColumn> {

    private final int maxFeatures;

    private final Gain criterion;
    private int[] features = null;

    /**
     * Instantiates a new Random splitter.
     *
     * @param maxFeatures the max features
     * @param criterion   the setCriterion
     */
    private RandomSplitter(int maxFeatures, Gain criterion) {
        this.maxFeatures = maxFeatures;
        this.criterion = criterion;
    }

    /**
     * With maximum features.
     *
     * @param maxFeatures the max features
     * @return the builder
     */
    public static Builder withMaximumFeatures(int maxFeatures) {
        return new Builder(maxFeatures);
    }

    @Override
    public Tree.Split<ValueThreshold> find(Examples examples, DataFrame<?> dataFrame, CategoricColumn column) {
        if (features == null) {
            initialize(dataFrame);
        }

        int maxFeatures = this.maxFeatures > 0 ? this.maxFeatures : (int) Math.round(Math.sqrt(dataFrame.columns())) + 1;

        Utils.permute(features);

        Tree.Split<ValueThreshold> bestSplit = null;
        double bestImpurity = Double.POSITIVE_INFINITY;
        for (int i = 0; i < features.length && i < maxFeatures; i++) {
            int axis = features[i];

            Value threshold = search(dataFrame, examples, axis);
            if (threshold.na()) {
                continue;
            }

            Tree.Split<ValueThreshold> split = split(dataFrame, examples, axis, threshold);
            double impurity = criterion.calculate(examples, split);
            if (impurity < bestImpurity) {
                bestSplit = split;
                bestImpurity = impurity;
            }
        }

        if (bestSplit != null) {
            bestSplit.setImpurity(new double[]{bestImpurity, 0, 0});
        }
        return bestSplit;
    }

    private void initialize(DataFrame dataFrame) {
        this.features = new int[dataFrame.columns()];
        for (int i = 0; i < features.length; i++) {
            this.features[i] = i;
        }
    }

    /**
     * Search value.
     *
     * @param dataFrame the dataset
     * @param examples  the examples
     * @param axis      the axis
     * @return the value
     */
    protected Value search(DataFrame dataFrame, Examples examples, int axis) {
        switch (dataFrame.getType(axis).getDataType()) {
            case NUMERIC:
                return sampleNumericValue(dataFrame, examples, axis);
            case CATEGORIC:
            case FACTOR:
                return sampleCategoricValue(dataFrame, examples, axis);
            default:
                throw new IllegalStateException(String.format("Header: %s, not supported", dataFrame.getType(axis)));
        }
    }

    /**
     * Sample numeric value.
     *
     * @param dataFrame the dataset
     * @param examples  the examples
     * @param axis      the axis
     * @return the value
     */
    protected Value sampleNumericValue(DataFrame dataFrame, Examples examples, int axis) {
        Example a = examples.getRandomSample().getRandomExample();
        Example b = examples.getRandomSample().getRandomExample();

        Value valueA = dataFrame.getValue(a.getIndex(), axis);
        Value valueB = dataFrame.getValue(b.getIndex(), axis);

        // TODO - what if both A and B are missing?
        if (valueA.na()) {
            return valueB;
        } else if (valueB.na()) {
            return valueA;
        } else {
            Numeric numa = (Numeric) valueA;
            Numeric numb = (Numeric) valueB;
            return Numeric.valueOf((numa.asDouble() + numb.asDouble()) / 2);
        }
    }

    /**
     * Sample categoric value.
     *
     * @param dataFrame the dataset
     * @param examples  the examples
     * @param axis      the axis
     * @return the value
     */
    protected Value sampleCategoricValue(DataFrame dataFrame, Examples examples, int axis) {
        Example example = examples.getRandomSample().getRandomExample();
        return dataFrame.getValue(example.getIndex(), axis);
    }

    /**
     * The type Builder.
     */
    public static class Builder implements Splitter.Builder<RandomSplitter> {
        private int maxFeatures;
        private Gain criterion = Gain.INFO;

        private Builder(int maxFeatures) {
            this.maxFeatures = maxFeatures;
        }

        /**
         * Sets max features.
         *
         * @param maxFeatures the max features
         * @return the max features
         */
        public Builder withMaximumFeatures(int maxFeatures) {
            this.maxFeatures = maxFeatures;
            return this;
        }

        /**
         * Sets setCriterion.
         *
         * @param criterion the setCriterion
         * @return the setCriterion
         */
        public Builder setCriterion(Gain criterion) {
            this.criterion = criterion;
            return this;
        }

        /**
         * Create random splitter.
         *
         * @return the random splitter
         */
        public RandomSplitter create() {
            return new RandomSplitter(maxFeatures, criterion);
        }

    }
}

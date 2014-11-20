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

package org.briljantframework.learning.time;

import org.briljantframework.data.column.Column;
import org.briljantframework.learning.Classifier;
import org.briljantframework.learning.Prediction;
import org.briljantframework.learning.ensemble.Ensemble;
import org.briljantframework.learning.ensemble.Sampler;
import org.briljantframework.matrix.DenseMatrix;
import org.briljantframework.matrix.Matrix;
import org.briljantframework.matrix.RowVector;
import org.briljantframework.matrix.dataset.MatrixDataFrame;
import org.briljantframework.matrix.distance.Distance;

/**
 * Created by Isak Karlsson on 24/09/14.
 */
public class RandomShapeletForest implements Classifier<RowVector, MatrixDataFrame, Column> {

    private final Ensemble<RowVector, MatrixDataFrame, Column> ensemble;

    private RandomShapeletForest(Builder builder) {
        this.ensemble = builder.ensemble.create();
    }

    /**
     * Size builder.
     *
     * @param size the size
     * @return the builder
     */
    public static Builder withSize(int size) {
        return new Builder().withSize(size);
    }

    @Override
    public Model fit(MatrixDataFrame dataset, Column column) {
        Ensemble.Model<RowVector, MatrixDataFrame> model = ensemble.fit(dataset, column);

        //        double noModels = model.getModels().size();
        double[] averageLengthImportance = null;
        double[] averagePositionImportance = null;
        for (org.briljantframework.learning.Model<RowVector, MatrixDataFrame> m : model.getModels()) {
            ShapeletTree.Model stModel = (ShapeletTree.Model) m;

            Matrix lengthImportance = stModel.getLengthImportance();
            Matrix positionImportance = stModel.getPositionImportance();
            //            double totalErrorReduction = stModel.getTotalErrorReduction();
            if (averageLengthImportance == null) {
                averageLengthImportance = new double[lengthImportance.columns()];
                averagePositionImportance = new double[positionImportance.columns()];
            }
            for (int i = 0; i < averageLengthImportance.length; i++) {
                averageLengthImportance[i] = averageLengthImportance[i] + (lengthImportance.get(i) / ensemble.size());

                averagePositionImportance[i] = averagePositionImportance[i] + (positionImportance.get(i) / ensemble.size());
            }
        }


        return new Model(DenseMatrix.columnVector(averageLengthImportance), DenseMatrix.columnVector(averagePositionImportance), model);
    }

    @Override
    public String toString() {
        return "Ensemble of Randomized Shapelet Trees";
    }

    /**
     * The type Model.
     */
    public static class Model implements org.briljantframework.learning.Model<RowVector, MatrixDataFrame> {

        private final Ensemble.Model<RowVector, MatrixDataFrame> model;
        private final DenseMatrix lengthImportance;
        private final DenseMatrix positionImportance;

        /**
         * Instantiates a new Model.
         *
         * @param lengthImportance   the length importance
         * @param positionImportance the position importance
         * @param model              the model
         */
        public Model(DenseMatrix lengthImportance, DenseMatrix positionImportance, Ensemble.Model<RowVector, MatrixDataFrame> model) {
            this.lengthImportance = lengthImportance;
            this.model = model;
            this.positionImportance = positionImportance;
        }

        @Override
        public Prediction predict(RowVector row) {
            return model.predict(row);
        }

        /**
         * Gets length importance.
         *
         * @return the length importance
         */
        public Matrix getLengthImportance() {
            return lengthImportance;
        }

        /**
         * Gets position importance.
         *
         * @return the position importance
         */
        public Matrix getPositionImportance() {
            return positionImportance;
        }
    }

    /**
     * The type Builder.
     */
    public static class Builder implements Classifier.Builder<RandomShapeletForest> {

        private final RandomShapeletSplitter.Builder randomShapeletSplitter = RandomShapeletSplitter.withDistance(new EarlyAbandonSlidingDistance(Distance.EUCLIDEAN));

        private final Ensemble.Builder<RowVector, MatrixDataFrame, Column> ensemble = Ensemble.withMember(ShapeletTree.withSplitter(randomShapeletSplitter));

        /**
         * Lower builder.
         *
         * @param lower the setLowerLength
         * @return the builder
         */
        public Builder withLowerLength(int lower) {
            randomShapeletSplitter.withLowerLength(lower);
            return this;
        }

        /**
         * Sample size.
         *
         * @param sampleSize the sample size
         * @return the builder
         */
        public Builder withSampleSize(int sampleSize) {
            randomShapeletSplitter.withSampleSize(sampleSize);
            return this;
        }

        /**
         * Shapelets builder.
         *
         * @param maxShapelets the max shapelets
         * @return the builder
         */
        public Builder withInspectedShapelets(int maxShapelets) {
            randomShapeletSplitter.withInspectedShapelets(maxShapelets);
            return this;
        }

        /**
         * Distance builder.
         *
         * @param distance the distance
         * @return the builder
         */
        public Builder withDistance(Distance distance) {
            randomShapeletSplitter.withDistance(distance);
            return this;
        }

        /**
         * Distance builder.
         *
         * @param distance the distance
         * @return the builder
         */
        public Builder withDistance(Distance.Builder distance) {
            randomShapeletSplitter.withDistance(distance);
            return this;
        }

        /**
         * Upper builder.
         *
         * @param upper the setUpperLength
         * @return the builder
         */
        public Builder withUpperLength(int upper) {
            randomShapeletSplitter.withUpperLength(upper);
            return this;
        }

        /**
         * Size builder.
         *
         * @param size the size
         * @return the builder
         */
        public Builder withSize(int size) {
            ensemble.withSize(size);
            return this;
        }

        /**
         * Alpha builder.
         *
         * @param alpha the setAlpha
         * @return the builder
         */
        public Builder withAlpha(double alpha) {
            randomShapeletSplitter.withAlpha(alpha);
            return this;
        }

        /**
         * Randomizer builder.
         *
         * @param randomizer the setRandomizer
         * @return the builder
         */
        public Builder withSampler(Sampler randomizer) {
            ensemble.withSampler(randomizer);
            return this;
        }

        @Override
        public RandomShapeletForest create() {
            return new RandomShapeletForest(this);
        }
    }
}

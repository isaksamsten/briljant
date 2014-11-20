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

package org.briljantframework.learning.linear;

import com.google.common.base.Preconditions;
import org.briljantframework.data.values.Numeric;
import org.briljantframework.learning.Classifier;
import org.briljantframework.learning.Prediction;
import org.briljantframework.matrix.Matrices;
import org.briljantframework.matrix.Matrix;
import org.briljantframework.matrix.RowVector;
import org.briljantframework.matrix.dataset.MatrixDataFrame;
import org.briljantframework.matrix.dataset.Series;
import org.briljantframework.matrix.math.LinearAlgebra;

/**
 * Created by Isak Karlsson on 29/09/14.
 */
public class LinearRegression implements Classifier<RowVector, MatrixDataFrame, Series> {

    private LinearRegression() {
    }

    /**
     * Create linear regression.
     *
     * @return the linear regression
     */
    public static LinearRegression create() {
        return new LinearRegression();
    }

    @Override
    public Model fit(MatrixDataFrame frame, Series series) {
        Preconditions.checkArgument(frame.rows() == series.size());
        return new Model(LinearAlgebra.leastLinearSquares(frame.asMatrix(), series.asMatrix()));
    }

    /**
     * The type Model.
     */
    public static final class Model implements org.briljantframework.learning.Model<RowVector, MatrixDataFrame> {

        private final Matrix theta;

        /**
         * Instantiates a new Model.
         *
         * @param theta the theta
         */
        public Model(Matrix theta) {
            this.theta = theta;
        }

        /**
         * Gets theta.
         *
         * @return the theta
         */
        public Matrix getTheta() {
            return theta;
        }

        @Override
        public Prediction predict(RowVector row) {
            return Prediction.numeric(Numeric.valueOf(Matrices.dot(theta, row)));
        }
    }
}

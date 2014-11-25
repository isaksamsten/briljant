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

package org.briljantframework.matrix.transformation;

import org.briljantframework.data.transform.Transformation;
import org.briljantframework.data.transform.Transformer;
import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.exceptions.ArgumentException;
import org.briljantframework.matrix.Matrix;

/**
 * Created by Isak Karlsson on 12/08/14.
 */
public class MinMaxNormalizer implements Transformer {

    @Override
    public Transformation fit(DataFrame frame) {
        Matrix matrix = frame.asMatrix();
        double[] min = new double[matrix.columns()];
        double[] max = new double[matrix.columns()];

        for (int j = 0; j < matrix.columns(); j++) {
            double minTemp = Double.POSITIVE_INFINITY, maxTemp = Double.NEGATIVE_INFINITY;
            for (int i = 0; i < matrix.rows(); i++) {
                double value = matrix.get(i, j);
                if (value > maxTemp) {
                    maxTemp = value;
                }

                if (value < minTemp) {
                    minTemp = value;
                }
            }

            min[j] = minTemp;
            max[j] = maxTemp;
        }

        return new MinMaxNormalization(min, max);
    }

    /**
     * Created by Isak Karlsson on 12/08/14.
     *
     * @param <E> the type parameter
     */
    public static class MinMaxNormalization implements Transformation {

        private final double[] min, max;

        /**
         * Instantiates a new Min max normalization.
         *
         * @param min the min
         * @param max the max
         */
        public MinMaxNormalization(double[] min, double[] max) {
            if (min.length != max.length) {
                throw new ArgumentException("min.length != max.length");
            }
            this.min = min;
            this.max = max;
        }

        @Override
        public DataFrame transform(DataFrame frame) {
            //        E newFrame = copyTo.newEmptyDataset(frame);
            //        Matrix matrix = newFrame.asMatrix();
            //        for (int j = 0; j < matrix.columns(); j++) {
            //            double min = this.min[j];
            //            double max = this.max[j];
            //            for (int i = 0; i < matrix.rows(); i++) {
            //                matrix.put(i, j, (frame.get(i, j) - min) / (max - min));
            //            }
            //        }

            return null;
        }
    }
}

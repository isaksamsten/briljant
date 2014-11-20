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

import org.briljantframework.data.DataFrame;
import org.briljantframework.data.transform.Transformation;
import org.briljantframework.data.transform.Transformer;
import org.briljantframework.matrix.Matrix;
import org.briljantframework.matrix.MismatchException;
import org.briljantframework.matrix.dataset.MatrixDataFrame;

/**
 * Created by Isak Karlsson on 12/08/14.
 */
public class MeanImputer<E extends MatrixDataFrame> implements Transformer<E> {

    @Override
    public Transformation<E> fit(E frame) {
        Matrix matrix = frame.asMatrix();
        double[] means = new double[matrix.columns()];

        for (int j = 0; j < matrix.columns(); j++) {
            double mean = 0.0;
            int rows = 0;
            for (int i = 0; i < matrix.rows(); i++) {
                double value = matrix.get(i, j);
                if (!Double.isNaN(value)) {
                    mean += value;
                    rows += 1;
                }
            }
            means[j] = mean / rows;
        }

        return new MeanImputation<>(means);
    }

    private static class MeanImputation<E extends MatrixDataFrame> implements Transformation<E> {

        private final double[] means;

        private MeanImputation(double[] means) {
            this.means = means;
        }

        @Override
        public E transform(E frame, DataFrame.CopyTo<E> factory) {
            if (frame.columns() != means.length) {
                throw new MismatchException("transform", "can't impute missing values for " +
                        "matrix with shape %s using %d values", frame.getShape(), means.length);
            }
            E copy = factory.copyDataset(frame);
            Matrix matrix = copy.asMatrix();
            for (int j = 0; j < matrix.columns(); j++) {
                for (int i = 0; i < matrix.rows(); i++) {
                    if (Double.isNaN(frame.get(i, j))) {
                        matrix.put(i, j, means[j]);
                    }
                }
            }
            return copy;
        }
    }


}

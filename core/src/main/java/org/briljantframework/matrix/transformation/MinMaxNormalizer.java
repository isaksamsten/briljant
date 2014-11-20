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
import org.briljantframework.matrix.Matrix;
import org.briljantframework.matrix.dataset.MatrixDataFrame;

/**
 * Created by Isak Karlsson on 12/08/14.
 */
public class MinMaxNormalizer<E extends MatrixDataFrame> implements Transformer<E> {

    @Override
    public Transformation<E> fit(E frame) {
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

        return new MinMaxNormalization<>(min, max);
    }
}

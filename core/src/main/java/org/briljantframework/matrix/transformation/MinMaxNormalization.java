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
import org.briljantframework.exceptions.ArgumentException;
import org.briljantframework.matrix.Matrix;
import org.briljantframework.matrix.dataset.MatrixDataFrame;

/**
 * Created by Isak Karlsson on 12/08/14.
 *
 * @param <E> the type parameter
 */
public class MinMaxNormalization<E extends MatrixDataFrame> implements Transformation<E> {

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
    @SuppressWarnings("unchecked")
    public E transform(E frame, DataFrame.CopyTo<E> copyTo) {
        E newFrame = copyTo.newEmptyDataset(frame);
        Matrix matrix = newFrame.asMatrix();
        for (int j = 0; j < matrix.columns(); j++) {
            double min = this.min[j];
            double max = this.max[j];
            for (int i = 0; i < matrix.rows(); i++) {
                matrix.put(i, j, (frame.get(i, j) - min) / (max - min));
            }
        }

        return newFrame;
    }
}

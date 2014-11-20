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
import org.briljantframework.matrix.Axis;
import org.briljantframework.matrix.DenseMatrix;
import org.briljantframework.matrix.Matrices;
import org.briljantframework.matrix.Matrix;
import org.briljantframework.matrix.dataset.MatrixDataFrame;

/**
 * Z normalization is also known as "Normalization to Zero Mean and Unit of Energy" first mentioned by found in Goldin &
 * Kanellakis. It ensures that all elements of the input vector are transformed into the output vector whose mean is
 * approximately 0 while the standard deviation are in a range close to 1.
 * <p>
 * This normalizer, normalizes the input frame "row-wise",
 * <p>
 * Created by Isak Karlsson on 26/09/14.
 */
public class ZNormalizer<E extends MatrixDataFrame> implements Transformer<E> {

    @Override
    public Transformation<E> fit(E frame) {
        Matrix mean = Matrices.mean(DenseMatrix::new, frame.asMatrix(), Axis.COLUMN);

        Matrix x = frame.asMatrix();
        Matrix xNorm = new DenseMatrix(x.getShape());

        for (int i = 0; i < xNorm.rows(); i++) {
            for (int j = 0; j < xNorm.columns(); j++) {
                xNorm.put(i, j, (x.get(i, j) - mean.get(j)));
            }
        }

        Matrix sigma = Matrices.std(DenseMatrix::new, xNorm, Axis.COLUMN);
        return new ZNormalization<>(mean, sigma);
    }

    private static class ZNormalization<E extends MatrixDataFrame> implements Transformation<E> {

        private final Matrix sigma;
        private final Matrix mean;

        public ZNormalization(Matrix mean, Matrix sigma) {
            this.mean = mean;
            this.sigma = sigma;
        }

        @Override
        public E transform(E frame, DataFrame.CopyTo<E> copyTo) {
            E newFrame = copyTo.newEmptyDataset(frame);

            Matrix x = frame.asMatrix();
            Matrix xNorm = newFrame.asMatrix();

            for (int i = 0; i < xNorm.rows(); i++) {
                for (int j = 0; j < xNorm.columns(); j++) {
                    xNorm.put(i, j, (x.get(i, j) - mean.get(j)) / sigma.get(j));
                }
            }

            return newFrame;
        }
    }
}

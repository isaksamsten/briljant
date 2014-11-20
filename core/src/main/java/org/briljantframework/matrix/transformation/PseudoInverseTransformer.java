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
import org.briljantframework.data.types.Types;
import org.briljantframework.matrix.Matrix;
import org.briljantframework.matrix.dataset.MatrixDataFrame;
import org.briljantframework.matrix.math.LinearAlgebra;

/**
 * Transforms a frame to it's inverse
 * <p>
 * Created by Isak Karlsson on 11/08/14.
 */
public class PseudoInverseTransformer<E extends MatrixDataFrame> implements Transformer<E> {

    @Override
    public Transformation<E> fit(E container) {
        return new PinvTransformation<>();
    }

    private static class PinvTransformation<E extends MatrixDataFrame> implements Transformation<E> {
        @Override
        public E transform(E frame, DataFrame.CopyTo<E> factory) {
            Matrix matrix = LinearAlgebra.pinv(frame.asMatrix());
            E copy = factory.newBuilder(frame.getTypes()).create();
            copy.setMatrix(new Types(frame.getTypes()), matrix);
            return copy;
        }
    }
}

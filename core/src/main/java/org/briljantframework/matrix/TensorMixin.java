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

package org.briljantframework.matrix;

/**
 * Created by Isak Karlsson on 05/09/14.
 */
public interface TensorMixin<T extends MatrixLike> extends Matrix.New<T>, Matrix.Copy<T>, MatrixLike {

    default T sqrt() {
        return Matrices.sqrt(this, this);
    }

    default T pow(double power) {
        return Matrices.pow(this, this, power);
    }

    default T log() {
        return Matrices.log(this, this);
    }

    default T log2() {
        return Matrices.log2(this, this);
    }

    default T log10() {
        return Matrices.log10(this, this);
    }

    /**
     * Transpose matrix like.
     *
     * @return the matrix like
     */
    MatrixLike transpose();
}

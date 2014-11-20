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

package org.briljantframework.matrix.decomposition;

import org.briljantframework.matrix.DenseMatrix;
import org.briljantframework.matrix.Diagonal;

/**
 * Created by isak on 23/06/14.
 */
public class SingularValueDecomposition implements Decomposition {

    /**
     * \Sigma is an m×n rectangular diagonal matrix with nonnegative real numbers on the diagonal. The diagonal entries
     * \Sigma_{i,i} of \Sigma are known as the singular values of M
     */
    public final Diagonal s;

    /**
     * U is a m×m real or complex unitary matrix
     */
    public final DenseMatrix u;

    /**
     * V* (or simply the transpose of V if V is real) is an n×n real unitary matrix
     */
    public final DenseMatrix v;

    /**
     * Instantiates a new Singular value decomposition.
     *
     * @param s the s
     * @param u the u
     * @param v the v
     */
    public SingularValueDecomposition(Diagonal s, DenseMatrix u, DenseMatrix v) {
        this.s = s;
        this.u = u;
        this.v = v;
    }

    /**
     * Gets diagonal.
     *
     * @return the diagonal
     */
    public Diagonal getDiagonal() {
        return s;
    }

    /**
     * Gets left singular values.
     *
     * @return the left singular values
     */
    public DenseMatrix getLeftSingularValues() {
        return u;
    }

    /**
     * Gets right singular values.
     *
     * @return the right singular values
     */
    public DenseMatrix getRightSingularValues() {
        return v;
    }

    @Override
    public String toString() {
        return "SingularValueDecomposition";
    }
}

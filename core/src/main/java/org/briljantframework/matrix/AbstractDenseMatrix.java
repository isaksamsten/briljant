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

import java.util.function.DoubleUnaryOperator;

/**
 * Created by Isak Karlsson on 20/08/14.
 */
public abstract class AbstractDenseMatrix implements MatrixLike {

    protected final double[] values;
    protected int rows;
    protected int cols;

    /**
     * Instantiates a new Abstract tensor.
     *
     * @param rows    the rows
     * @param columns the columns
     * @param values  the values
     */
    public AbstractDenseMatrix(int rows, int columns, double[] values) {
        this.values = values;
        this.cols = columns;
        this.rows = rows;
    }

    /**
     * Apply void.
     *
     * @param operator the operator
     */
    public void apply(DoubleUnaryOperator operator) {
        for (int i = 0; i < values.length; i++) {
            values[i] = operator.applyAsDouble(values[i]);
        }
    }

    /**
     * Create a copy of this matrix. This contract stipulates that modifications
     * of the copy does not affect the original.
     *
     * @return the copy
     */
    public abstract MatrixLike copy();

    /**
     * Is square.
     *
     * @return true if rows() == columns()
     */
    public boolean isSquare() {
        return rows() == columns();
    }

    /**
     * The shape of the current matrix.
     *
     * @return the shape
     */
    public Shape getShape() {
        return Shape.of(rows(), columns());
    }

    /**
     * Returns true if {@link org.briljantframework.matrix.Shape#size()}  == {@link #size()}
     *
     * @param shape the shape
     * @return the boolean
     */
    public boolean hasCompatibleShape(Shape shape) {
        return hasCompatibleShape(shape.rows, shape.columns);
    }

    /**
     * Has compatible shape.
     *
     * @param rows the rows
     * @param cols the cols
     * @return the boolean
     * @throws ArithmeticException
     */
    public boolean hasCompatibleShape(int rows, int cols) {
        return Math.multiplyExact(rows, cols) == rows() * columns();
    }

    /**
     * Equal shape (i.e.
     *
     * @param other the other
     * @return the boolean
     */
    public boolean hasEqualShape(MatrixLike other) {
        return rows() == other.rows() && columns() == other.columns();
    }

    /**
     * Rows int.
     *
     * @return number or rows
     */
    public int rows() {
        return rows;
    }

    /**
     * Columns int.
     *
     * @return number of columns
     */
    public int columns() {
        return cols;
    }

    /**
         * Raw view of the column-major underlying array. In some instances it might be possible to mutate this (e.g., if
     * the implementation provides a direct reference. However, there are nos such guarantees).
     *
     * @return the underlying array. Touch with caution.
     */
    public double[] asDoubleArray() {
        return values;
    }

    /**
     * Set value at row i and column j to value
     *
     * @param i     row
     * @param j     column
     * @param value value
     */
    public abstract void put(int i, int j, double value);

    /**
     * Puts <code>value</code> at the linearized position <code>index</code>.
     *
     * @param index the index
     * @param value the value
     * @see #get(int)
     */
    public abstract void put(int index, double value);
}

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

    /**
     * The Values.
     */
    protected final double[] values;
    /**
     * The Rows.
     */
    protected int rows;

    /**
     * The Cols.
     */
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
     * Is square.
     *
     * @return true if rows() == columns()
     */
    public boolean isSquare() {
        return rows() == columns();
    }

    /**
     * Array double [ ].
     *
     * @return the underlying array. Touch with caution.
     */
    public double[] toArray() {
        return values;
    }


    /**
     * Shape shape.
     *
     * @return the shape
     */
    public Shape getShape() {
        return Shape.of(rows(), columns());
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
     * Has compatible shape.
     *
     * @param shape the shape
     * @return the boolean
     */
    public boolean hasCompatibleShape(Shape shape) {
        return hasCompatibleShape(shape.rows, shape.columns);
    }

    /**
     * Has equal size.
     *
     * @param other the other
     * @return the boolean
     */
    public boolean hasEqualShape(MatrixLike other) {
        return rows() == other.rows() && columns() == other.columns();
    }

    /**
     * Copy tensor like.
     *
     * @return the tensor like
     */
    public abstract MatrixLike copy();
}

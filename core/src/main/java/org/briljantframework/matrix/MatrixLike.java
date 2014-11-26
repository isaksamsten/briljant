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
 * Created by Isak Karlsson on 28/08/14.
 */
public interface MatrixLike {

    /**
     * Set value at row i and column j to value
     *
     * @param i     row
     * @param j     column
     * @param value value
     */
    void put(int i, int j, double value);

    /**
     * Get value at row i and column j
     *
     * @param i row
     * @param j column
     * @return value double
     */
    double get(int i, int j);

    /**
     * Puts <code>value</code> at the linearized position <code>index</code>.
     *
     * @param index the index
     * @param value the value
     * @see #get(int)
     */
    void put(int index, double value);

    /**
     * Create a copy of this matrix. This contract stipulates that modifications
     * of the copy does not affect the original.
     *
     * @return the copy
     */
    public MatrixLike copy();

    /**
     * Is square.
     *
     * @return true if rows() == columns()
     */
    default boolean isSquare() {
        return rows() == columns();
    }

    /**
     * The number of rows.
     *
     * @return number or rows
     */
    int rows();

    /**
     * The number of columns.
     *
     * @return number of columns
     */
    int columns();

    /**
     * Returns the linearized size of this matrix.
     * <p>
     * <pre>{@link #rows()}*{@link #columns()} == {@code #size()}</pre>
     *
     * @return the int
     */
    int size();

    /**
     * Flattens the traversal of the matrix in column-major order.
     * If {@link #rows()} == 1 or {@link #columns()} == 1, <code>get(index)</code> behaves as
     * expected. If not, the matrix is traversed in column-major order, i.e.
     * <p>
     * For example, given the following matrix
     * <pre>
     *     1 2 3
     *     4 5 6
     * </pre>
     * <p>
     * this code
     * <pre>
     *     for(int i = 0; i < x.size(); i++) {
     *        System.out.print(x.get(i));
     *     }
     * </pre>
     * <p>
     * prints
     * <p>
     * <pre>
     *    142536
     * </pre>
     *
     * @param index the index
     * @return the double
     */
    double get(int index);

    /**
     * The shape of the current matrix.
     *
     * @return the shape
     */
    default Shape getShape() {
        return Shape.of(rows(), columns());
    }

    /**
     * Returns true if {@link Shape#size()}  == {@link #size()}
     *
     * @param shape the shape
     * @return the boolean
     */
    default boolean hasCompatibleShape(Shape shape) {
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
    default boolean hasCompatibleShape(int rows, int cols) {
        return Math.multiplyExact(rows, cols) == rows() * columns();
    }

    /**
     * Equal shape (i.e.
     *
     * @param other the other
     * @return the boolean
     */
    default boolean hasEqualShape(MatrixLike other) {
        return rows() == other.rows() && columns() == other.columns();
    }

    /**
     * Raw view of the column-major underlying array. In some instances it might be possible to mutate this (e.g., if
     * the implementation provides a direct reference. However, there are nos such guarantees).
     *
     * @return the underlying array. Touch with caution.
     */
    double[] asDoubleArray();
}

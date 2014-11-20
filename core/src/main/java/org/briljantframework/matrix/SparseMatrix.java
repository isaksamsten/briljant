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

import com.carrotsearch.hppc.IntDoubleMap;
import com.carrotsearch.hppc.IntDoubleOpenHashMap;

/**
 * Created by Isak Karlsson on 02/09/14.
 */
public class SparseMatrix implements MatrixLike {

    private final int rows, cols;
    private final double defaultValue;
    private IntDoubleMap map = new IntDoubleOpenHashMap();

    /**
     * Instantiates a new Sparse matrix.
     *
     * @param rows         the rows
     * @param cols         the cols
     * @param defaultValue the default value
     */
    public SparseMatrix(int rows, int cols, double defaultValue) {
        this.rows = rows;
        this.cols = cols;
        this.defaultValue = defaultValue;
    }

    /**
     * Instantiates a new Sparse matrix.
     *
     * @param shape the shape
     * @param array the array
     */
    public SparseMatrix(Shape shape, double[] array) {
        this(shape.rows, shape.rows, Double.NaN);
        for (int i = 0; i < shape.rows; i++) {
            for (int j = 0; j < shape.columns; j++) {
                put(i, j, array[index(i, j)]);
            }
        }
    }

    /**
     * Instantiates a new Sparse matrix.
     *
     * @param shape the shape
     */
    public SparseMatrix(Shape shape) {
        this(shape.rows, shape.columns, 0.0);
    }

    private int index(int row, int col) {
        if (col >= this.columns() || col < 0) {
            throw new IllegalArgumentException(String.format("index out of bounds; value %d out of bound %d", col,
                    this.cols - 1));
        } else if (row >= this.rows() || row < 0) {
            throw new IllegalArgumentException(String.format("index out of bounds; value %d out of bound %d", row,
                    this.rows - 1));
        } else {
            return col * this.rows + row;
        }
    }

    @Override
    public void put(int i, int j, double value) {
        map.put(index(i, j), value);
    }

    @Override
    public double get(int i, int j) {
        return map.getOrDefault(index(i, j), defaultValue);
    }

    @Override
    public double get(int index) {
        return 0;
    }

    @Override
    public void put(int index, double value) {

    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public int rows() {
        return rows;
    }

    @Override
    public int columns() {
        return cols;
    }

    @Override
    public double[] toArray() {
        throw new UnsupportedOperationException();
    }

    @Override
    public SparseMatrix copy() {
        SparseMatrix s = new SparseMatrix(rows, cols, defaultValue);
        s.map = new IntDoubleOpenHashMap(this.map);
        return s;
    }

    /**
     * Transpose matrix like.
     *
     * @return the matrix like
     */
    public SparseMatrix transpose() {
        throw new IllegalArgumentException();
    }

    /**
     * Gets column.
     *
     * @param index the index
     * @return the column
     */
    public Matrix getColumn(int index) {
        throw new UnsupportedOperationException();
    }

    /**
     * Gets row.
     *
     * @param index the index
     * @return the row
     */
    public Matrix getRow(int index) {
        throw new UnsupportedOperationException();
    }
}

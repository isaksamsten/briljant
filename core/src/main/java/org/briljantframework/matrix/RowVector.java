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

import com.google.common.base.Preconditions;
import org.briljantframework.data.RowView;
import org.briljantframework.data.values.Value;
import org.briljantframework.matrix.dataset.MatrixDataFrame;

/**
 * Created by Isak on 7/3/2014.
 */
public class RowVector extends RowView implements MatrixLike {

    private final int size;

    /**
     * Instantiates a new Vector view.
     *
     * @param parent the parent
     * @param row    the row
     */
    public RowVector(MatrixDataFrame parent, int row) {
        super(parent, row);
        this.size = parent.columns();
    }

    @Override
    public void put(int i, int j, double value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public double get(int i, int j) {
        Preconditions.checkArgument(i < 1 && i >= 0 && j < size && j >= 0);
        return get(j);
    }

    @Override
    public void put(int index, double value) {
        throw new IllegalStateException("A VectorView is immutable");
    }

    @Override
    public Matrix copy() {
        DenseMatrix copy = new DenseMatrix(1, size);
        for (int i = 0; i < size; i++) {
            copy.put(i, get(i));
        }
        return copy;
    }

    /**
     * Transpose matrix like.
     *
     * @return the matrix like
     */
    public MatrixLike transpose() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int rows() {
        return 1;
    }

    @Override
    public int columns() {
        return size;
    }

    public double get(int col) {
        return ((MatrixDataFrame) dataFrame).get(index, col);
    }

    /**
     * Not optimized
     * <p>
     * Consider:
     * <p>
     * <pre>
     *     VectorView v = frame.getRow(1);
     *     Matrix matrix = new DenseMatrix(v);
     * </pre>
     *
     * @return
     */
    @Override
    public double[] toArray() {
        double[] array = new double[this.size()];
        for (int i = 0; i < array.length; i++) {
            array[i] = get(i);
        }
        return array;
    }

    public Matrix asRowVector() {
        return copy();
    }

    @Override
    public Value getValue(int col) {
        return dataFrame.getValue(index, col);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public String toString() {
        return String.format("VectorView(%d, %d)", index, size);
    }
}

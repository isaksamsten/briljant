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

package org.briljantframework.matrix.dataset;

import com.carrotsearch.hppc.DoubleArrayList;
import com.google.common.base.Preconditions;
import org.briljantframework.data.column.Column;
import org.briljantframework.data.column.Columns;
import org.briljantframework.data.column.NumericColumn;
import org.briljantframework.data.types.NumericType;
import org.briljantframework.data.types.Type;
import org.briljantframework.data.values.Missing;
import org.briljantframework.data.values.Numeric;
import org.briljantframework.data.values.Value;
import org.briljantframework.matrix.DenseMatrix;
import org.briljantframework.matrix.Matrix;
import org.briljantframework.matrix.MatrixLike;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.stream.Stream;

/**
 * Immutable tagged vector
 * <p>
 * Created by Isak Karlsson on 23/09/14.
 */
public class Series implements MatrixLike, NumericColumn, Column.CopyTo<Series> {

    private final Type type;
    private Matrix vector;

    /**
     * Instantiates a new Series.
     *
     * @param other the other
     * @param type  the types
     */
    public Series(MatrixLike other, Type type) {
        this(new DenseMatrix(other.getShape(), other), type);
    }

    /**
     * Instantiates a new Series.
     *
     * @param vector the vector
     * @param type   the types
     */
    protected Series(Matrix vector, Type type) {
        Preconditions.checkArgument(vector.columns() == 1);
        this.vector = vector;
        this.type = type;
    }

    /**
     * The constant factory.
     *
     * @return the factory
     */
    public static CopyTo<Series> getFactory() {
        return type -> new SeriesBuilder(new NumericType(type.getName()));
    }

    /**
     * Gets vector.
     *
     * @return the vector
     */
    public Matrix asMatrix() {
        return vector;
    }

    /**
     * Sets vector.
     *
     * @param vector the vector
     */
    public void setVector(Matrix vector) {
        if (!this.vector.hasEqualShape(vector)) {
            throw new IllegalArgumentException("vector does not have the same dimensions as previous vector");
        }
        this.vector = vector;
    }

    @Override
    public String toString() {
        return String.format("Series(%s, types=%s, shape=%s)", Arrays.toString(vector.asDoubleArray()),
                getType().getName(), getShape());
    }

    @Override
    public void put(int i, int j, double value) {

    }

    @Override
    public double get(int i, int j) {
        return 0;
    }

    @Override
    public void put(int index, double value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Series copy() {
        return new Series(vector.copy(), type);
    }

    @Override
    public int rows() {
        return vector.rows();
    }

    @Override
    public int columns() {
        return vector.columns();
    }

    @Override
    public int size() {
        return vector.size();
    }

    @Override
    public double get(int index) {
        return vector.get(index);
    }

    @Override
    public double[] asDoubleArray() {
        return vector.asDoubleArray();
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
    public Type getType() {
        return type;
    }

    @Override
    public Value getValue(int id) {
        Double value = get(id);
        return Double.isNaN(value) ? Missing.valueOf() : Numeric.valueOf(value);
    }

    @Override
    public Stream<Value> take(Collection<Integer> rows) {
        return Columns.take(this, rows);
    }

    @Override
    public Stream<Value> drop(Collection<Integer> rows) {
        return Columns.drop(this, rows);
    }

    @Override
    public Iterator<Value> iterator() {
        return new Iterator<Value>() {
            private int current = 0;

            @Override
            public boolean hasNext() {
                return current < rows();
            }

            @Override
            public Value next() {
                return getValue(current++);
            }
        };
    }

    @Override
    public Builder<Series> newBuilder(Type target) {
        return getFactory().newBuilder(target);
    }

    private static class SeriesBuilder implements Column.Builder<Series> {

        private final NumericType target;
        private DoubleArrayList list = new DoubleArrayList();

        private SeriesBuilder(NumericType target) {
            this.target = target;

        }

        @Override
        public void add(double value) {
            list.add(value);
        }

        @Override
        public void add(int value) {
            list.add(value);
        }

        @Override
        public void add(Object value) {
            if (value == null) {
                list.add(Double.NaN);
            } else if (value instanceof Number) {
                list.add(((Number) value).doubleValue());
            } else {
                list.add(Double.NaN);
            }
        }

        @Override
        public void add(Value value) {
            Object object = value.value();
            if (object instanceof Number) {
                list.add(((Number) object).doubleValue());
            } else {
                list.add(Double.NaN);
            }
        }

        @Override
        public Series create() {
            return new Series(new DenseMatrix(list.size(), 1, list.toArray()), target);
        }

        @Override
        public Iterator<Value> iterator() {
            return new Iterator<Value>() {
                private int current = 0;

                @Override
                public boolean hasNext() {
                    return current < list.size();
                }

                @Override
                public Value next() {
                    return Numeric.valueOf(list.get(current++));
                }
            };
        }
    }
}

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
import com.carrotsearch.hppc.ObjectIntMap;
import com.carrotsearch.hppc.ObjectIntOpenHashMap;
import com.google.common.base.Preconditions;
import org.briljantframework.data.*;
import org.briljantframework.data.column.Column;
import org.briljantframework.data.types.NumericType;
import org.briljantframework.data.types.Type;
import org.briljantframework.data.types.Types;
import org.briljantframework.data.values.Missing;
import org.briljantframework.data.values.Numeric;
import org.briljantframework.data.values.Value;
import org.briljantframework.matrix.DenseMatrix;
import org.briljantframework.matrix.Matrix;
import org.briljantframework.matrix.MatrixLike;
import org.briljantframework.matrix.RowVector;

import java.util.*;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A frame is a tagged matrix
 * <p>
 * Created by Isak Karlsson on 28/08/14.
 */
public class MatrixDataFrame implements MatrixLike, DataFrame<RowVector> {

    private static final DataFrame.CopyTo<MatrixDataFrame> COPY = new CopyTo();
    private final RowIterable<RowVector> iterable = new RowIterable<>(this);
    private Types types;

    private ObjectIntMap<String> typeNameToIndex = new ObjectIntOpenHashMap<>();
    private Matrix matrix;

    /**
     * Instantiates a new Frame.
     *
     * @param types  the headers
     * @param matrix the matrix
     */
    public MatrixDataFrame(Types types, Matrix matrix) {
        this.matrix = checkNotNull(matrix, "A frame must have a matrix");
        this.types = checkNotNull(types, "A frame must have headers");

        int index = 0;
        for (Type type : types) {
            String name = type.getName();
            if (typeNameToIndex.containsKey(name)) {
                name = String.format("%s_%d", name, index);
            }
            typeNameToIndex.put(name, index++);
        }
    }

    public MatrixDataFrame(Matrix matrix) {
        this(Types.range(NumericType::new, matrix.columns()), matrix);
    }

    /**
     * The constant factory.
     *
     * @return the factory
     */
    public static DataFrame.CopyTo<MatrixDataFrame> copyTo() {
        return COPY;
    }

    /**
     * Matrix matrix.
     *
     * @return the underlying matrix
     */
    public Matrix asMatrix() {
        return matrix;
    }

    /**
     * Update the underlying matrix
     *
     * @param types  the headers
     * @param matrix the matrix
     */
    public void setMatrix(Types types, Matrix matrix) {
        Preconditions.checkArgument(types.size() == matrix.columns());
        this.types = types;
        this.matrix = matrix;
    }

    /**
     * Gets row view.
     *
     * @param name the name
     * @return the row view
     */
    public RowVector getRow(String name) {
        return getRow(typeNameToIndex.get(name));
    }

    /**
     * Caution: this is rather expensive (boxing a value at each call)
     *
     * @param row value at row
     * @param col and column
     * @return value at row and column
     */
    @Override
    public Value getValue(int row, int col) {
        double value = matrix.get(row, col);
        return Double.isNaN(value) ? Missing.INSTANCE : Numeric.valueOf(value);
    }

    /**
     * Return a view of the row at <code>index</code>
     *
     * @param index the row
     * @return a cursor with the row data
     * @see #getRow(int)
     */
    @Override
    public RowVector getRow(int index) {
        return new RowVector(this, index);
    }

    @Override
    public Stream<RowVector> takeRows(Iterable<Integer> rows) {
        return Datasets.takeRows(this, rows);
    }

    @Override
    public Stream<RowVector> dropRows(Iterable<Integer> rows) {
        return Datasets.dropRows(this, rows);
    }

    /**
     * Gets column.
     *
     * @param index the index
     * @return the column
     */
    @Override
    public Series getColumn(int index) {
        return new Series(matrix.getColumn(index), getType(index));
    }

    @Override
    public Iterable<Column> getColumns() {
        return new ColumnIterable(this);
    }

    @Override
    public MatrixDataFrame dropColumn(int index) {
        return Datasets.dropColumnAs(this, index, MatrixDataFrame.copyTo());
    }

    @Override
    public MatrixDataFrame dropColumns(Iterable<Integer> columns) {
        Set<Integer> set = new HashSet<>();
        List<Type> types = new ArrayList<>(columns());
        for (Integer index : columns) {
            set.add(index);
            types.add(getType(index));
        }
        DataFrame.Builder<MatrixDataFrame> builder = copyTo().newBuilder(types);
        for (int j = 0; j < columns(); j++) {
            for (int i = 0; i < rows(); i++) {
                if (set.contains(j)) {
                    builder.add(get(i, j));
                }
            }
        }
        return builder.create();
    }

    @Override
    public MatrixDataFrame addColumn(Column column) {
        return addColumns(Collections.singleton(column));
    }

    @Override
    public MatrixDataFrame addColumns(Collection<? extends Column> columns) {
        return Datasets.addColumns(this, columns, copyTo());
    }

    @Override
    public Type getType(int col) {
        return types.get(col);
    }

    @Override
    public Types getTypes() {
        return types;
    }

    /**
     * Gets column.
     *
     * @param name the name
     * @return the column
     */
    public Series getColumn(String name) {
        return getColumn(typeNameToIndex.get(name));
    }

    @Override
    public String toString() {
        return Datasets.toString(this);
    }

    @Override
    public void put(int i, int j, double value) {
        matrix.put(i, j, value);
    }

    @Override
    public double get(int i, int j) {
        return matrix.get(i, j);
    }

    @Override
    public void put(int index, double value) {
        matrix.put(index, value);
    }

    @Override
    public MatrixDataFrame copy() {
        return new MatrixDataFrame(types, matrix.copy());
    }

    @Override
    public int rows() {
        return matrix.rows();
    }

    @Override
    public int columns() {
        return matrix.columns();
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public double get(int index) {
        return 0;
    }

    @Override
    public double[] asDoubleArray() {
        return matrix.asDoubleArray();
    }

    @Override
    public Iterator<RowVector> iterator() {
        return iterable.iterator();
    }

    private static class CopyTo implements DataFrame.CopyTo<MatrixDataFrame> {

        @Override
        public MatrixDataFrame newEmptyDataset(MatrixDataFrame frame) {
            return new MatrixDataFrame(frame.getTypes(), new DenseMatrix(frame.rows(), frame.columns()));
        }

        @Override
        public MatrixDataFrame copyDataset(MatrixDataFrame dataset) {
            return new MatrixDataFrame(dataset.types, new DenseMatrix(dataset.matrix));
        }

        @Override
        public DataFrame.Builder<MatrixDataFrame> newBuilder(Iterable<? extends Type> types) {
            return new Builder(types);
        }
    }

    /**
     * The type Builder.
     */
    public static class Builder extends AbstractBuilder<MatrixDataFrame> {

        private final DoubleArrayList rowBuffer = new DoubleArrayList();
        private int columns;
        private double[] colBuffer;

        /**
         * Instantiates a new Builder.
         *
         * @param valueTypes the value headers
         */
        public Builder(Iterable<? extends Type> valueTypes) {
            super(valueTypes);
        }

        /**
         * Columns void.
         *
         * @param size the size
         */
        public void columns(int size) {
            this.columns = size;
        }

        @Override
        public MatrixDataFrame create() {
            this.row = 0;
            this.column = 0;
            if (colBuffer == null) {
                rowBuffer.trimToSize();
                this.columns = types.size();
                return new MatrixDataFrame(types, DenseMatrix.fromRowOrder(rowBuffer.size() / columns, columns, rowBuffer.buffer));
            } else {
                return new MatrixDataFrame(Types.range(NumericType::new, columns), new DenseMatrix(this.columns, colBuffer));
            }

        }

        /**
         * Values void.
         *
         * @param values the values
         */
        public void values(double[] values) {
            colBuffer = values;
        }

        @Override
        protected void put(int row, int column, Value value) {
            add(value.asDouble());
        }

        @Override
        public void add(Object value) {
            if (value == null) {
                add(Double.NaN);
            } else if (value instanceof Number) {
                add(((Number) value).doubleValue());
            } else {
                add(Double.NaN);
            }
        }

        @Override
        public void add(double value) {
            rowBuffer.add(value);
        }

        @Override
        public void add(int value) {
            rowBuffer.add(value);
        }

        @Override
        public void addRow(Row row) {
            for (int i = 0; i < row.size(); i++) {
                add(row.getValue(i));
            }
        }


        @Override
        public Iterator<Value> iterator() {
            if (colBuffer == null) {
                return new Iterator<Value>() {

                    private int current = 0;

                    @Override
                    public boolean hasNext() {
                        return current < rowBuffer.size();
                    }

                    @Override
                    public Value next() {
                        return Numeric.valueOf(rowBuffer.get(current++));
                    }
                };
            }
            throw new UnsupportedOperationException();
        }
    }
}

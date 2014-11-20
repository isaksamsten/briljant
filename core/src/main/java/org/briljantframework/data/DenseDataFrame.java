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

package org.briljantframework.data;

import com.google.common.base.Preconditions;
import org.briljantframework.data.column.Column;
import org.briljantframework.data.column.ColumnView;
import org.briljantframework.data.types.Type;
import org.briljantframework.data.types.Types;
import org.briljantframework.data.values.Value;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.briljantframework.data.Datasets.dropColumnAs;

/**
 * Simple heterogeneous dataset.
 * <p>
 * Created by isak on 30/06/14.
 */
public class DenseDataFrame implements DataFrame<RowView>, Serializable {

    private static final DataFrame.CopyTo<DenseDataFrame> COPY_TO = new CopyTo();
    private final RowIterable<RowView> iterable = new RowIterable<>(this);
    private int rows = -1, cols = -1;
    private ArrayList<Value> values = null;
    private Types types = null;

    /**
     * Instantiates a new Dense dataset.
     *
     * @param values the values
     * @param types  the headers
     */
    protected DenseDataFrame(ArrayList<Value> values, Types types) {
        this.values = checkNotNull(values, "Values are required.");
        this.types = checkNotNull(types, "Headers are required");
        this.cols = types.size();
        this.rows = values.size() / this.cols;
    }

    /**
     * Instantiates a new Dense dataset.
     *
     * @param types  the types
     * @param values the values
     */
    public DenseDataFrame(Types types, Collection<? extends Collection<?>> values) {
        checkNotNull(types);
        checkNotNull(values);
        checkArgument(types.size() == values.size(), "The size of values and types does not match.");

        this.cols = types.size();
        this.types = new Types(types);

        for (Collection<?> objects : values) {
            if (rows < 0) {
                rows = objects.size();
            } else {
                if (rows != objects.size()) {
                    throw new IllegalArgumentException("Columns does not have the same length.");
                }
            }
        }
        List<? extends Iterator<?>> collectionIterators = values.stream().map(t -> t.iterator()).collect(Collectors.toList());
        this.values = new ArrayList<>(rows * cols);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                this.values.add(types.get(j).createValueFrom(collectionIterators.get(j).next()));
            }
        }
    }

    /**
     * Copy constructor
     *
     * @param other to copy
     */
    private DenseDataFrame(DenseDataFrame other) {
        this.values = other.values;
        this.cols = other.cols;
        this.rows = other.rows;
        this.types = other.types;
    }

    /**
     * Gets factory.
     *
     * @return the factory
     */
    public static DataFrame.CopyTo<DenseDataFrame> copyTo() {
        return COPY_TO;
    }

    public static DataFrame.Builder<DenseDataFrame> builder(Iterable<? extends Type> types) {
        return new Builder(types);
    }

    @Override
    public String toString() {
        return Datasets.toString(this);
    }

    @Override
    public Iterator<RowView> iterator() {
        return iterable.iterator();
    }

    @Override
    public int rows() {
        return this.rows;
    }

    @Override
    public Value getValue(int row, int col) {
        checkArgument(row >= 0 && row < rows() && col >= 0 && col < columns());
        int pos = row * cols + col;
        if (pos > values.size() || pos < 0) {
            return null;
        } else {
            return values.get(pos);
        }
    }

    @Override
    public RowView getRow(int index) {
        if (index * cols + cols > values.size()) {
            throw new ArrayIndexOutOfBoundsException("row > rows");
        } else {
            return new RowView(this, index);
        }
    }

    @Override
    public Stream<RowView> takeRows(Iterable<Integer> rows) {
        return Datasets.takeRows(this, rows);
    }

    @Override
    public Stream<RowView> dropRows(Iterable<Integer> rows) {
        return Datasets.dropRows(this, rows);
    }

    @Override
    public Column getColumn(int index) {
        Preconditions.checkElementIndex(index, columns());
        return new ColumnView(this, index);
    }

    @Override
    public Iterable<Column> getColumns() {
        return new ColumnIterable(this);
    }

    @Override
    public DenseDataFrame dropColumn(int index) {
        Preconditions.checkElementIndex(index, columns());
        return dropColumnAs(this, index, copyTo());
    }

    @Override
    public DenseDataFrame dropColumns(Iterable<Integer> columns) {
        return Datasets.dropColumnsAs(this, columns, copyTo());
    }

    @Override
    public DenseDataFrame addColumn(Column column) {
        return addColumns(Collections.singleton(column));
    }

    @Override
    public DenseDataFrame addColumns(Collection<? extends Column> columns) {
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

    @Override
    public int columns() {
        return cols;
    }

    /**
     * The type Factory.
     */
    private static class CopyTo implements DataFrame.CopyTo<DenseDataFrame> {

        @Override
        public DenseDataFrame newEmptyDataset(DenseDataFrame dataset) {
            return new DenseDataFrame(new ArrayList<>(), new Types(dataset.getTypes()));
        }

        @Override
        public DenseDataFrame copyDataset(DenseDataFrame dataset) {
            return new DenseDataFrame(dataset);
        }

        @Override
        public DataFrame.Builder<DenseDataFrame> newBuilder(Iterable<? extends Type> types) {
            return new Builder(types);
        }

    }

    /**
     * The type Dataset builder.
     */
    public static class Builder extends AbstractBuilder<DenseDataFrame> {

        private final ArrayList<Value> values = new ArrayList<>();

        /**
         * Instantiates a new Builder.
         *
         * @param types the types
         */
        public Builder(Iterable<? extends Type> types) {
            super(types);

        }

        @Override
        protected void put(int row, int column, Value value) {
            values.add(value);
        }

        @Override
        public DenseDataFrame create() {
            return new DenseDataFrame(values, types);
        }

        @Override
        public Iterator<Value> iterator() {
            return values.iterator();
        }
    }


}


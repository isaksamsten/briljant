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

import org.briljantframework.data.column.Column;
import org.briljantframework.data.types.Type;
import org.briljantframework.data.types.Types;
import org.briljantframework.data.values.Value;

import java.util.Collection;
import java.util.Iterator;
import java.util.stream.Stream;

/**
 * General abstraction for datasets with a known number of rows. The expectation is that {@code valueAt(int, int)} and
 * run in amortized constant time. A dataset is expected to be immutable.
 * <p>
 * Created by isak on 30/06/14.
 *
 * @param <R> the type parameter
 */
public interface DataFrame<R extends Row> extends Traversable<R> {

    /**
     * Rows int.
     *
     * @return the number of rows
     */
    int rows();

    /**
     * Gets value.
     *
     * @param row value at row
     * @param col and column
     * @return a Value
     */
    Value getValue(int row, int col);

    /**
     * Get a specified row
     *
     * @param index at index
     * @return an entry at index
     */
    R getRow(int index);

    /**
     * Gets rows.
     *
     * @param rows the rows
     * @return the rows
     */
    Stream<R> takeRows(Iterable<Integer> rows);

    /**
     * Drop rows.
     *
     * @param rows the rows
     * @return the dataset
     */
    Stream<R> dropRows(Iterable<Integer> rows);

    /**
     * Gets column.
     *
     * @param index the index
     * @return the column
     */
    Column getColumn(int index);

    /**
     * Gets column views.
     *
     * @return all columns
     */
    Iterable<Column> getColumns();

    /**
     * Instead a more specialized implementation can be provided.
     *
     * @param index the index
     * @return the dataset
     */
    DataFrame dropColumn(int index);

    /**
     * Drop columns.
     *
     * @param columns the columns
     * @return the dataset
     */
    DataFrame dropColumns(Iterable<Integer> columns);

    /**
     * Add column.
     *
     * @param column the column
     * @return the dataset
     */
    DataFrame addColumn(Column column);

    /**
     * Add columns.
     *
     * @param columns the columns
     * @return the dataset
     */
    DataFrame addColumns(Collection<? extends Column> columns);


    /**
     * Construct a dataset sequentially by adding items row-wise. For example,
     * <p>
     * <pre>
     *     Dataset.Builder<T> b = new DenseDataset.Builder(types);
     *     b.add(1); b.add(2); b.add(3);
     *     b.add(4); b.add(5); b.add(6);
     *     T dataset = b.create();
     * </pre>
     * <p>
     * should result in a dataset with 2 rows and 3 columns.
     * <p>
     * Implementers should make sure that the types are copied
     * <p>
     * Created by Isak Karlsson on 12/06/14.
     *
     * @param <D> the type parameter
     */
    interface Builder<D extends DataFrame<?>> extends Iterable<Value> {

        /**
         * Add void.
         *
         * @param value the value
         */
        void add(double value);

        /**
         * Add void.
         *
         * @param value the value
         */
        void add(int value);

        /**
         * Add void.
         *
         * @param value the value
         */
        void add(Object value);

        /**
         * Add row.
         *
         * @param row the entry
         */
        default void addRow(Row row) {
            for (Value v : row) {
                add(v);
            }
        }

        /**
         * Combine void.
         *
         * @param right the right
         */
        default void addAll(Iterable<? extends Value> right) {
            right.forEach(this::add);
        }

        /**
         * Add already created value. Usually sub-classes are expected to call the {@link
         * Type#convertValueFrom(org.briljantframework.data.values.Value)}****** to allow for conversions, but this is an
         * implementation choice.
         *
         * @param value the value
         */
        void add(Value value);

        /**
         * Create d.
         *
         * @return the d
         */
        D create();
    }

    /**
     * Created by Isak Karlsson on 19/08/14.
     *
     * @param <T> the type parameter
     */
    interface CopyTo<T extends DataFrame<?>> {

        /**
         * New empty dataset.
         *
         * @param dataset the dataset
         * @return the t
         */
        default T newEmptyDataset(T dataset) {
            throw new UnsupportedOperationException("Can't create new %s " + dataset.getClass().getName());
        }

        /**
         * Create a copy of dataset
         *
         * @param dataset the dataset
         * @return the t
         */
        default T copyDataset(T dataset) {
            throw new UnsupportedOperationException("Can't copy %s " + dataset.getClass().getName());

        }

        /**
         * Create synchronized builder.
         *
         * @param types the types
         * @return the builder
         */
        default Builder<T> newSynchronizedBuilder(Iterable<? extends Type> types) {
            return new SynchronizedBuilder<>(newBuilder(types));
        }

        /**
         * Create builder.
         *
         * @param types the headers
         * @return the dataset builder
         */
        Builder<T> newBuilder(Iterable<? extends Type> types);

    }

    /**
     * The type Synchronized builder.
     *
     * @param <D> the type parameter
     */
    class SynchronizedBuilder<D extends DataFrame<?>> implements Builder<D> {
        private final Builder<D> builder;

        /**
         * Instantiates a new Synchronized builder.
         *
         * @param builder the builder
         */
        protected SynchronizedBuilder(Builder<D> builder) {
            this.builder = builder;
        }

        @Override
        public synchronized void add(double value) {
            builder.add(value);
        }

        @Override
        public synchronized Iterator<Value> iterator() {
            return builder.iterator();
        }

        @Override
        public synchronized void add(Object value) {
            builder.add(value);
        }

        @Override
        public synchronized void add(int value) {
            builder.add(value);
        }


        @Override
        public synchronized void addRow(Row row) {
            builder.addRow(row);
        }

        @Override
        public synchronized void add(Value value) {
            builder.add(value);
        }

        @Override
        public synchronized D create() {
            return builder.create();
        }


    }

    /**
     * The type Abstract builder.
     *
     * @param <D> the type parameter
     */
    abstract class AbstractBuilder<D extends DataFrame<?>> implements Builder<D> {
        /**
         * The Headers.
         */
        protected final Types types;
        private final int columns;

        /**
         * The Column.
         */
        protected int column = 0;
        /**
         * The Row.
         */
        protected int row = 0;

        /**
         * Instantiates a new Abstract builder.
         *
         * @param types the headers
         */
        protected AbstractBuilder(Iterable<? extends Type> types) {
            this.types = new Types(types);
            this.columns = this.types.size();
        }

        /**
         * Put void.
         *
         * @param row    the row
         * @param column the column
         * @param value  the value
         */
        protected abstract void put(int row, int column, Value value);

        private void increment() {
            column += 1;
            if (column == columns) {
                column = 0;
                row += 1;
            }
        }

        /**
         * Gets types.
         *
         * @return the types
         */
        public Types getTypes() {
            return types;
        }

        @Override
        public void add(Object value) {
            put(row, column, types.get(column).createValueFrom(value));
            increment();
        }

        @Override
        public void add(double value) {
            put(row, column, types.get(column).createValueFrom(value));
            increment();
        }


        @Override
        public void add(int value) {
            put(row, column, types.get(column).createValueFrom(value));
            increment();
        }

        @Override
        public void addRow(Row row) {
            for (Value value : row) {
                add(value);
            }
        }

        @Override
        public void add(Value value) {
            put(row, column, types.get(column).convertValueFrom(value));
            increment();
        }


    }
}

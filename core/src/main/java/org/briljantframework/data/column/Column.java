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

package org.briljantframework.data.column;

import org.briljantframework.data.types.Type;
import org.briljantframework.data.values.Value;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Created by Isak Karlsson on 13/08/14.
 */
public interface Column extends Iterable<Value> {

    /**
     * Gets types.
     *
     * @return the target types
     */
    Type getType();

    /**
     * Gets domain.
     *
     * @return the domain
     */
    default Set<Value> getDomain() {
        return getType().getDomain();
    }

    /**
     * Gets value.
     *
     * @param id the instance id
     * @return the value for instance with <code>id</code>
     */
    Value getValue(int id);

    /**
     * Take column.
     *
     * @param rows the rows
     * @return the column
     */
    Stream<Value> take(Collection<Integer> rows);

    /**
     * Drop column.
     *
     * @param rows the rows
     * @return the column
     */
    Stream<Value> drop(Collection<Integer> rows);

    /**
     * Stream stream.
     *
     * @return the stream
     */
    default Stream<Value> stream() {
        return StreamSupport.stream(spliterator(), false);
    }

    /**
     * Parallel stream.
     *
     * @return the stream
     */
    default Stream<Value> parallelStream() {
        return StreamSupport.stream(spliterator(), true);
    }

    /**
     * Size int.
     *
     * @return the total number of targets
     */
    int size();

    /**
     * Contruct a collection of Targets For example,
     * <p>
     * <pre>
     *     TargetsBuilder<T> b = ..;
     *     b.create(1); b.create(2);
     *     T targets = b.build();
     * </pre>
     * <p>
     * Creates a collection of Targets with values 1 and 2
     * <p>
     * Created by isak on 17/08/14.
     *
     * @param <T> the type parameter
     */
    interface Builder<T extends Column> extends Iterable<Value> {

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
         * Add void.
         *
         * @param value the value
         */
        void add(Value value);

        /**
         * Combine void.
         *
         * @param values the values
         */
        default void addAll(Iterable<Value> values) {
            values.forEach(this::add);
        }

        /**
         * Create t.
         *
         * @return the t
         */
        T create();
    }

    /**
     * Created by Isak Karlsson on 19/08/14.
     *
     * @param <T> the type parameter
     */
    interface CopyTo<T extends Column> {
        /**
         * Create builder.
         *
         * @param target the target
         * @return the target builder
         */
        Builder<T> newBuilder(Type target);
    }
}

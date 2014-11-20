package org.briljantframework.data.column;

import com.google.common.collect.Sets;
import org.briljantframework.data.types.Type;
import org.briljantframework.data.values.Value;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.Spliterators;
import java.util.stream.Collector;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Created by Isak Karlsson on 16/11/14.
 */
public class Columns {
    private Columns() {

    }

    /**
     * Collector collector.
     *
     * @param <C>     the type parameter
     * @param type    the type
     * @param copyTo the factory
     * @return the collector
     */
    public static <C extends Column> Collector<? super Value, ?, C> collect(Type type, Column.CopyTo<C> copyTo) {
        return Collector.of(() -> copyTo.newBuilder(type), Column.Builder::add,
                (left, right) -> {
                    left.addAll(right);
                    return left;
                }, Column.Builder::create);
    }

    /**
     * Drop c.
     *
     * @param column the column
     * @param rows   the rows
     * @return the c
     */
    public static Stream<Value> drop(Column column, Collection<Integer> rows) {
        return StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(new ExcludeIndexIterator(column, rows), 0),
                false
        );
    }

    /**
     * Take c.
     *
     * @param column the column
     * @param rows   the rows
     * @return the c
     */
    public static Stream<Value> take(Column column, Collection<Integer> rows) {
        return StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(new IncludeIndexIterator(column, rows), 0),
                false
        );
    }

    /**
     * As c.
     *
     * @param <C>     the type parameter
     * @param column  the column
     * @param copyTo the factory
     * @return the c
     */
    public static <C extends Column> C as(Column column, Column.CopyTo<C> copyTo) {
        Column.Builder<C> builder = copyTo.newBuilder(column.getType());
        column.forEach(builder::add);
        return builder.create();
    }

    /**
     * The type Exclude index iterator.
     */
    static class ExcludeIndexIterator implements Iterator<Value> {

        private final Set<Integer> exclude;
        private final Column column;
        private int current = 0;

        /**
         * Instantiates a new Exclude index iterator.
         *
         * @param column  the column
         * @param exclude the exclude
         */
        ExcludeIndexIterator(Column column, Iterable<Integer> exclude) {
            this.exclude = Sets.newHashSet(exclude);
            this.column = column;
        }

        @Override
        public boolean hasNext() {
            return current < column.size();
        }

        @Override
        public Value next() {
            Value value = null;
            while (current < column.size() && value == null) {
                if (!exclude.contains(current)) {
                    value = column.getValue(current);
                }
                current += 1;
            }
            return value;
        }
    }

    /**
     * The type Include index iterator.
     */
    static class IncludeIndexIterator implements Iterator<Value> {

        private final Column column;
        private final Iterator<Integer> include;

        /**
         * Instantiates a new Include index iterator.
         *
         * @param column  the column
         * @param include the include
         */
        IncludeIndexIterator(Column column, Iterable<Integer> include) {
            this.column = column;
            this.include = include.iterator();
        }

        @Override
        public boolean hasNext() {
            return include.hasNext();
        }

        @Override
        public Value next() {
            return column.getValue(include.next());
        }
    }
}

package org.briljantframework.data;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.briljantframework.Utils;
import org.briljantframework.data.column.Column;
import org.briljantframework.data.transform.RemoveIncompleteColumns;
import org.briljantframework.data.types.Type;
import org.briljantframework.data.types.Types;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Created by Isak Karlsson on 27/10/14.
 */
public class Datasets {

    /**
     * Row collector.
     *
     * @param <D> the type parameter
     * @return collector
     */
    public static <D extends DataFrame<?>> Collector<? super Row, ?, D> collect(Supplier<DataFrame.Builder<D>> supplier) {
        return Collector.of(
                supplier,
                DataFrame.Builder::addRow,
                (left, right) -> {
                    left.addAll(right);
                    return left;
                },
                DataFrame.Builder::create);
    }

    /**
     * As d.
     *
     * @param <D>       the type parameter
     * @param dataFrame the dataset
     * @param copyTo    the factory
     * @return the d
     */
    public static <D extends DataFrame<?>> D as(DataFrame<?> dataFrame, DataFrame.CopyTo<D> copyTo) {
        DataFrame.Builder<D> builder = copyTo.newBuilder(dataFrame.getTypes());
        dataFrame.forEach(builder::addRow);
        return builder.create();
    }

    /**
     * Add columns.
     *
     * @param <D>       the type parameter
     * @param dataFrame the dataset
     * @param columns   the columns
     * @param copyTo    the factory   @return the d
     * @return the d
     */
    public static <D extends DataFrame<?>> D addColumns(DataFrame<?> dataFrame, Collection<? extends Column> columns, DataFrame.CopyTo<D> copyTo) {
        List<Type> types = Lists.newArrayList(dataFrame.getTypes());
        for (Column column : columns) {
            Preconditions.checkArgument(column.size() == dataFrame.rows());
            types.add(column.getType());
        }

        DataFrame.Builder<D> builder = copyTo.newBuilder(types);
        for (int i = 0; i < dataFrame.rows(); i++) {
            builder.addRow(dataFrame.getRow(i));
            for (Column column : columns) {
                builder.add(column.getValue(i));
            }
        }

        return builder.create();
    }

    /**
     * Take rows.
     *
     * @param dataFrame the dataset
     * @param rows      the rows
     * @return the stream
     */
    public static <R extends Row> Stream<R> takeRows(DataFrame<? extends R> dataFrame, Iterable<Integer> rows) {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(new IncludeIndexIterator<>(dataFrame, rows.iterator()), 0), false);
    }

    /**
     * Drop rows.
     *
     * @param dataFrame the dataset
     * @param rows      the rows
     * @return the d
     */
    public static <R extends Row> Stream<R> dropRows(DataFrame<? extends R> dataFrame, Iterable<Integer> rows) {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(new ExcludeIndexIterator<>(dataFrame, rows.iterator()), 0), false);
    }

    /**
     * Gets column.
     *
     * @param dataFrame the dataset
     * @param index     the index
     * @param copyTo    the factory
     * @return the column
     */
    public static <C extends Column> C getColumnAs(DataFrame<?> dataFrame, int index, Column.CopyTo<C> copyTo) {
        Preconditions.checkArgument(index >= 0 && index < dataFrame.columns());
        Column.Builder<C> builder = copyTo.newBuilder(dataFrame.getType(index));
        for (int i = 0; i < dataFrame.rows(); i++) {
            builder.add(dataFrame.getValue(i, index));
        }
        return builder.create();
    }

    /**
     * Drop column with {@code index} and return a new dataset of type {@code D} created using {@code factory}.
     *
     * @param dataFrame the dataset
     * @param index     the index
     * @return the d
     */
    public static <D extends DataFrame<?>> D dropColumnAs(DataFrame<?> dataFrame, int index, DataFrame.CopyTo<D> copyTo) {
        Preconditions.checkArgument(index > 0 && index < dataFrame.columns());
        List<Type> types = Lists.newArrayList(dataFrame.getTypes());
        types.remove(index);

        DataFrame.Builder<D> builder = copyTo.newBuilder(types);
        for (int i = 0; i < dataFrame.rows(); i++) {
            for (int j = 0; j < dataFrame.columns(); j++) {
                if (j != index) {
                    builder.add(dataFrame.getValue(i, j));
                }
            }
        }
        return builder.create();
    }

    /**
     * Drop columns.
     *
     * @param <D>       the type parameter
     * @param dataFrame the dataset
     * @param columns   the columns
     * @param copyTo    the factory
     * @return the d
     */
    public static <D extends DataFrame<?>> D dropColumnsAs(DataFrame dataFrame, Iterable<Integer> columns, DataFrame.CopyTo<D> copyTo) {
        Set<Integer> set = new HashSet<>();
        List<Type> types = new ArrayList<>(dataFrame.columns());
        for (Integer index : columns) {
            set.add(index);
            types.add(dataFrame.getType(index));
        }
        DataFrame.Builder<D> builder = copyTo.newBuilder(types);
        for (int i = 0; i < dataFrame.rows(); i++) {
            for (int j = 0; j < dataFrame.columns(); j++) {
                if (set.contains(j)) {
                    builder.add(dataFrame.getValue(i, j));
                }
            }
        }
        return builder.create();
    }

    /**
     * Randomize d.
     *
     * @param <D>     the type parameter
     * @param dataset the dataset
     * @param copyTo  the factory
     * @return the d
     */
    public static <D extends DataFrame<?>> D randomize(D dataset, DataFrame.CopyTo<D> copyTo) {
        Preconditions.checkArgument(dataset.rows() > 0);
        int[] order = new int[dataset.rows()];
        for (int i = 0; i < order.length; i++) {
            order[i] = i;
        }
        Utils.permute(order);

        DataFrame.Builder<D> builder = copyTo.newBuilder(dataset.getTypes());
        for (int o : order) {
            builder.addRow(dataset.getRow(o));
        }

        return builder.create();
    }

    /**
     * Stack d.
     *
     * @param <D>      the type parameter
     * @param datasets the datasets
     * @param copyTo   the factory
     * @return the d
     */
    public static <D extends DataFrame<?>> D stack(Collection<? extends DataFrame<?>> datasets, DataFrame.CopyTo<D> copyTo) {
        //        List<Type> gold = null;
        //        for (Dataset<?> dataset : datasets) {
        //            if (gold == null) {
        //                gold = dataset.getTypes();
        //                continue;
        //            }
        //
        //            List<Type> types = dataset.getTypes();
        //            if (gold.size() != types.size()) {
        //                throw new IllegalArgumentException("Header sizes does not match");
        //            }
        //
        //            for (int i = 0; i < gold.size(); i++) {
        //                if (gold.get(i).getDataType() != types.get(i).getDataType()) {
        //                    throw new IllegalArgumentException("Can't stack datasets with incompatible types");
        //                }
        //            }
        //        }
        //
        //        List<Type> types = Types.clone(gold, new DefaultTypeFactory());
        //        Dataset.Builder<D> builder = copyTo.newBuilder(types);
        //
        //        for (Dataset<?> dataset : datasets) {
        //            for (Row row : dataset) {
        //                builder.addRow(row);
        //            }
        //        }

        return null;// builder.create();
    }

    /**
     * To string.
     *
     * @param dataFrame the dataset
     * @return the string
     */
    public static String toString(DataFrame dataFrame) {
        return toString(dataFrame, 10);
    }

    /**
     * To string.
     *
     * @param dataFrame the dataset
     * @param max       the max
     * @return the string
     */
    public static String toString(DataFrame<?> dataFrame, int max) {
        ImmutableTable.Builder<Object, Object, Object> b = ImmutableTable.builder();
        Types types = dataFrame.getTypes();
        b.put(0, 0, "   ");
        for (int i = 0; i < types.size(); i++) {
            b.put(0, i + 1, types.get(i).getName());
        }
        for (int i = 0; i < dataFrame.rows() && i < max; i++) {
            Row row = dataFrame.getRow(i);
            b.put(i + 1, 0, String.format("[%d,]   ", i));
            for (int j = 0; j < row.size(); j++) {
                b.put(i + 1, j + 1, row.getValue(j).toString());
            }
        }

        StringBuilder builder = new StringBuilder(dataFrame.getClass().getSimpleName()).append(" (").append(dataFrame.rows()).append("x").append(dataFrame.columns()).append(")\n");
        Utils.prettyPrintTable(builder, b.build(), 1, 2, false, false);
        return builder.toString();
    }

    private static class IncludeIndexIterator<R extends Row> implements Iterator<R> {
        private final DataFrame<? extends R> dataFrame;
        private final Iterator<Integer> rows;

        private IncludeIndexIterator(DataFrame<? extends R> dataFrame, Iterator<Integer> rows) {
            this.dataFrame = dataFrame;
            this.rows = rows;
        }

        @Override
        public boolean hasNext() {
            return rows.hasNext();
        }

        @Override
        public R next() {
            return dataFrame.getRow(rows.next());
        }
    }

    private static class ExcludeIndexIterator<R extends Row> implements Iterator<R> {
        private final DataFrame<? extends R> dataFrame;
        private final Set<Integer> exclude;
        private int current = 0;

        /**
         * Instantiates a new Exclude subset iterator.
         *
         * @param dataFrame the dataset
         * @param iterator  the iterator
         */
        public ExcludeIndexIterator(DataFrame<? extends R> dataFrame, Iterator<Integer> iterator) {
            this.dataFrame = dataFrame;
            this.exclude = Sets.newHashSet(iterator);
        }

        @Override
        public boolean hasNext() {
            return current < dataFrame.rows();
        }

        @Override
        public R next() {
            R row = null;
            while (current < dataFrame.rows() && row == null) {
                if (!exclude.contains(current)) {
                    row = dataFrame.getRow(current);
                }
                current += 1;
            }
            return row;
        }
    }
}

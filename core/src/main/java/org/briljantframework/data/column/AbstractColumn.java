package org.briljantframework.data.column;

import com.google.common.base.Preconditions;
import org.briljantframework.data.types.Type;
import org.briljantframework.data.values.Value;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

/**
 * Created by Isak Karlsson on 10/11/14.
 */
public abstract class AbstractColumn implements Column {

    private final List<Value> values;
    private final int size;
    private final Type type;

    /**
     * Instantiates a new Basic target.
     *
     * @param type   the types
     * @param values the values
     */
    public AbstractColumn(Type type, List<Value> values) {
        this.type = Preconditions.checkNotNull(type);
        this.values = Preconditions.checkNotNull(values);
        this.size = values.size();
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public Value getValue(int id) {
        return this.values.get(id);
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public Iterator<Value> iterator() {
        return values.iterator();
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
    public String toString() {
        return String.format("name: %s, size: %d, domain: %s, data-type: %s",
                getType().getName(),
                size(),
                getType().getDomain(),
                getType().getDataType());
    }

    /**
     * Created by isak on 17/08/14.
     *
     * @param <T> the type parameter
     */
    protected abstract static class AbstractColumnBuilder<T extends Column> implements Column.Builder<T> {

        /**
         * The Values.
         */
        protected final ArrayList<Value> values = new ArrayList<>();
        /**
         * The Type.
         */
        protected final Type type;

        /**
         * Instantiates a new Builder.
         *
         * @param type the types
         */
        public AbstractColumnBuilder(Type type) {
            this.type = type;
        }

        @Override
        public void add(double value) {
            values.add(type.createValueFrom(value));
        }

        @Override
        public void add(int value) {
            values.add(type.createValueFrom(value));
        }

        @Override
        public void add(Object value) {
            values.add(type.createValueFrom(value));
        }

        @Override
        public void add(Value value) {
            values.add(type.convertValueFrom(value));
        }

        @Override
        public Iterator<Value> iterator() {
            return values.iterator();
        }


    }
}

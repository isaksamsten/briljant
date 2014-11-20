package org.briljantframework.data.column;

import org.briljantframework.data.DataFrame;
import org.briljantframework.data.types.Type;
import org.briljantframework.data.values.Value;

import java.util.Collection;
import java.util.Iterator;
import java.util.stream.Stream;

/**
 * Created by Isak Karlsson on 13/11/14.
 */
public class ColumnView implements Column, Iterable<Value> {

    public final int index;
    protected final DataFrame dataFrame;

    public ColumnView(DataFrame dataFrame, int index) {
        this.dataFrame = dataFrame;
        this.index = index;
    }

    @Override
    public Type getType() {
        return dataFrame.getType(index);
    }

    @Override
    public Value getValue(int row) {
        return dataFrame.getValue(row, index);
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
    public int size() {
        return dataFrame.rows();
    }

    @Override
    public Iterator<Value> iterator() {
        return new Iterator<Value>() {
            private int current = 0;

            @Override
            public boolean hasNext() {
                return current < size();
            }

            @Override
            public Value next() {
                return getValue(current++);
            }
        };
    }

    @Override
    public String toString() {
        return String.format("ColumnView type: %s  index: %d  dataset: %s", getType(), index, dataFrame.getClass()
                .getSimpleName());
    }
}

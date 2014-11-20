package org.briljantframework.data;

import org.briljantframework.data.column.Column;
import org.briljantframework.data.column.ColumnView;

import java.util.Iterator;

/**
 * Created by Isak Karlsson on 13/11/14.
 */
public class ColumnIterable implements Iterable<Column> {
    private final DataFrame dataFrame;

    /**
     * Instantiates a new Entry iterable.
     *
     * @param dataFrame the dataset
     */
    public ColumnIterable(DataFrame dataFrame) {
        this.dataFrame = dataFrame;
    }

    @Override
    public Iterator<Column> iterator() {
        return new Iterator<Column>() {

            private int column = 0;

            @Override
            public boolean hasNext() {
                return column < dataFrame.columns();
            }

            @Override
            public ColumnView next() {
                return new ColumnView(dataFrame, column++);
            }
        };
    }
}

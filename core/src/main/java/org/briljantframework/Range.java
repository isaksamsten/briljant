package org.briljantframework;

import com.google.common.base.Preconditions;

import java.util.AbstractCollection;
import java.util.Iterator;

/**
 * Created by Isak Karlsson on 09/11/14.
 */
public class Range extends AbstractCollection<Integer> {

    private final int start, end, step;

    private Range(int start, int end, int step) {
        Preconditions.checkArgument(start < end);
        this.start = start;
        this.end = end;
        this.step = step;
    }

    /**
     * Closed range.
     *
     * @param start the start
     * @param end   the end
     * @param step  the step
     * @return the range
     */
    public static Range closed(int start, int end, int step) {
        return new Range(start, end, step);
    }

    /**
     * Closed range.
     *
     * @param start the start
     * @param end   the end
     * @return the range
     */
    public static Range closed(int start, int end) {
        return new Range(start, end, 1);
    }

    @Override
    public Iterator<Integer> iterator() {
        return new Iterator<Integer>() {
            private int current = start;

            @Override
            public boolean hasNext() {
                return current < end;
            }

            @Override
            public Integer next() {
                int tmp = current;
                current += step;
                return tmp;
            }
        };
    }

    @Override
    public int size() {
        return (end - start) / step;
    }
}

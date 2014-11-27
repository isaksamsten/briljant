package org.briljantframework.vector;

import com.google.common.collect.Iterators;

import java.util.Iterator;

/**
 * Created by Isak Karlsson on 27/11/14.
 */
public class IntValue extends AbstractIntVector implements Value {
    private final int value;

    public IntValue(int value) {
        this.value = value;
    }

    @Override
    public Iterator<Integer> iterator() {
        return Iterators.singletonIterator(value);
    }

    @Override
    public int compareTo(Value o) {
        return getAsInt() - o.getAsInt();
    }

    @Override
    public int getAsInt(int index) {
        return value;
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public Builder newCopyBuilder() {
        return null;
    }

    @Override
    public Builder newBuilder() {
        return null;
    }

    @Override
    public Builder newBuilder(int size) {
        return null;
    }
}

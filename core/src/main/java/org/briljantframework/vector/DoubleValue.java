package org.briljantframework.vector;

import com.google.common.collect.Iterators;

import java.util.Iterator;

/**
 * Created by Isak Karlsson on 27/11/14.
 */
public class DoubleValue extends AbstractDoubleVector implements Value {

    private final double value;

    public DoubleValue(double value) {
        this.value = value;
    }

    @Override
    public int compareTo(Value o) {
        return isNA() && o.isNA() ? 0 : Double.compare(getAsDouble(), o.getAsDouble());
    }

    @Override
    public double getAsDouble(int index) {
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

    @Override
    public Iterator<Double> iterator() {
        return Iterators.singletonIterator(getAsDouble());
    }

}

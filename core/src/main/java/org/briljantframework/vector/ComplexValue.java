package org.briljantframework.vector;

import com.google.common.collect.Iterators;

import java.util.Iterator;

/**
 * Created by Isak Karlsson on 27/11/14.
 */
public class ComplexValue extends AbstractComplexVector implements Value {
    private final Complex complex;

    public ComplexValue(Complex complex) {
        this.complex = complex;
    }

    @Override
    public int compareTo(Value o) {
        return isNA() && o.isNA() ? 0 : Double.compare(getAsDouble(), o.getAsDouble());
    }

    @Override
    public double getAsDouble(int index) {
        return complex.getReal();
    }

    @Override
    public Complex getAsComplex(int index) {
        return complex;
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
    public Iterator<Complex> iterator() {
        return Iterators.singletonIterator(complex);
    }
}

package org.briljantframework.vector;

import java.util.Iterator;

/**
 * Created by Isak Karlsson on 20/11/14.
 */
public class DoubleVector implements Vector, Iterable<Double> {


    public static final double NA = Double.NaN;

    @Override
    public Iterator<Double> iterator() {
        return null;
    }

    @Override
    public double getAsDouble(int index) {
        return 0;
    }

    @Override
    public int getAsInteger(int index) {
        return 0;
    }

    @Override
    public Binary getAsBinary(int index) {
        return null;
    }

    @Override
    public String getAsString(int index) {
        return null;
    }

    @Override
    public boolean isNA(int index) {
        return false;
    }

    @Override
    public int compare(int a, int b) {
        return 0;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public Type getType() {
        return null;
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

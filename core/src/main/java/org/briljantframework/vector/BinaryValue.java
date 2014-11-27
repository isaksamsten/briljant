package org.briljantframework.vector;

import com.google.common.collect.Iterators;

import java.util.Iterator;

/**
 * Created by Isak Karlsson on 27/11/14.
 */
public class BinaryValue extends AbstractBinaryVector implements Value {
    private final int binary;

    public BinaryValue(int binary) {
        this.binary = binary;
    }

    public BinaryValue(Binary binary) {
        this.binary = binary.asInt();
    }

    @Override
    public Iterator<Binary> iterator() {
        return Iterators.singletonIterator(Binary.valueOf(binary));
    }

    @Override
    public int compareTo(Value o) {
        return getAsInt() - o.getAsInt();
    }

    @Override
    public int getAsInt(int index) {
        return binary;
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

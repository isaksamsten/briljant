package org.briljantframework.vector;

import com.carrotsearch.hppc.IntArrayList;
import com.google.common.collect.UnmodifiableIterator;

import java.util.Iterator;

/**
 * Created by Isak Karlsson on 20/11/14.
 */
public class IntVector implements Vector, Iterable<Integer>  {

    private final int size;
    private final int[] values;

    protected IntVector(int[] values) {
        this.size = values.length;
        this.values = values;
    }

    /**
     * The constant NA.
     */
    public static final int NA = Integer.MIN_VALUE;

    public static final Type TYPE = new Type() {
        @Override
        public IntVector.Builder newBuilder() {
            return new IntVector.Builder();
        }

        @Override
        public IntVector.Builder newBuilder(int size) {
            return new IntVector.Builder(size);
        }

        @Override
        public Class<?> getDataClass() {
            return Integer.TYPE;
        }

        @Override
        public int compare(int a, Vector va, int b, Vector ba) {
            return !va.isNA(a) && !ba.isNA(b) ? va.getAsInteger(a) - ba.getAsInteger(b) : 0;
        }
    };


    @Override
    public double getAsDouble(int index) {
        return getAsInteger(index);
    }

    @Override
    public Binary getAsBinary(int index) {
        return Binary.valueOf(getAsInteger(index));
    }

    @Override
    public String getAsString(int index) {
        return String.valueOf(getAsInteger(index));
    }

    @Override
    public boolean isTrue(int index) {
        return getAsInteger(index) == 1;
    }

    @Override
    public boolean isNA(int index) {
        return getAsInteger(index) == NA;
    }

    public int[] asIntArray() {
        return values;
    }

    public int[] toIntArray() {
        return values.clone();
    }

    @Override
    public Iterator<Integer> iterator() {
        return new UnmodifiableIterator<Integer>() {
            private int current = 0;

            @Override
            public boolean hasNext() {
                return current < size();
            }

            @Override
            public Integer next() {
                return getAsInteger(current++);
            }
        };
    }

    @Override
    public int getAsInteger(int index) {
        return values[index];
    }

    @Override
    public int compare(int a, int b) {
        return getAsInteger(a) - getAsInteger(b);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public Type getType() {
        return TYPE;
    }

    @Override
    public Builder newCopyBuilder() {
        return new Builder(toIntArray());
    }

    @Override
    public Builder newBuilder() {
        return new Builder();
    }

    @Override
    public Builder newBuilder(int size) {
        return new Builder(size);
    }

    public static final class Builder implements Vector.Builder {

        private IntArrayList buffer;

        public Builder() {
            this(INITIAL_CAPACITY);
        }

        public Builder(int capacity) {
            buffer = new IntArrayList(capacity);
        }

        Builder(int[] values) {
            buffer = IntArrayList.from(values);
        }

        @Override
        public Builder addNA(int index) {
            if (index == size()) {
                buffer.add(IntVector.NA);
            } else {
                buffer.set(index, IntVector.NA);
            }
            return this;
        }

        @Override
        public Builder addNA() {
            return addNA(size());
        }

        @Override
        public Builder add(Vector from, int fromIndex) {
            return add(size(), from, fromIndex);
        }

        @Override
        public Builder add(int atIndex, Vector from, int fromIndex) {
            if (atIndex == buffer.size()) {
                buffer.add(from.getAsInteger(fromIndex));
            } else {
                buffer.set(atIndex, from.getAsInteger(fromIndex));
            }
            return this;
        }

        @Override
        public Builder add(int index, Object value) {
            if (value instanceof Number) {
                int intValue = ((Number) value).intValue();
                if (index == size()) {
                    buffer.add(intValue);
                } else {
                    buffer.set(index, intValue);
                }
            } else {
                addNA();
            }
            return this;
        }

        @Override
        public Builder add(Object value) {
            return null;
        }

        public Builder add(int value) {
            return add(size(), value);
        }

        public Builder add(int index, int value) {
            if (index == size()) {
                buffer.add(value);
            } else {
                buffer.set(index, value);
            }
            return this;
        }

        @Override
        public int size() {
            return buffer.size();
        }

        @Override
        public IntVector create() {
            IntVector vector = new IntVector(buffer.toArray());
            buffer = null;
            return vector;
        }
    }
}

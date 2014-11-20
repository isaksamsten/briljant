package org.briljantframework.vector;

import com.carrotsearch.hppc.IntArrayList;
import com.google.common.collect.UnmodifiableIterator;

import java.util.Iterator;

/**
 * Created by Isak Karlsson on 20/11/14.
 */
public class BinaryVector implements Vector, Iterable<Binary> {

    public static final Binary NA = Binary.NA;

    public static Type TYPE = new Type() {
        @Override
        public Builder newBuilder() {
            return null;
        }

        @Override
        public Builder newBuilder(int size) {
            return null;
        }

        @Override
        public Class<?> getDataClass() {
            return Binary.class;
        }

        @Override
        public int compare(int a, Vector va, int b, Vector ba) {
            return va.getAsInteger(a) - ba.getAsInteger(b);
        }
    };

    private int[] values;

    protected BinaryVector(IntArrayList values) {
        this.values = values.toArray();
    }

    public BinaryVector(boolean... values) {
        this.values = new int[values.length];
        for (int i = 0; i < values.length; i++) {
            this.values[i] = values[i] ? 1 : 0;
        }
    }

    @Override
    public Iterator<Binary> iterator() {
        return new UnmodifiableIterator<Binary>() {
            private int current = 0;

            @Override
            public boolean hasNext() {
                return current < size();
            }

            @Override
            public Binary next() {
                return getAsBinary(current++);
            }
        };
    }

    @Override
    public double getAsDouble(int index) {
        return getAsInteger(index);
    }

    @Override
    public int getAsInteger(int index) {
        return values[index];
    }

    @Override
    public Binary getAsBinary(int index) {
        return Binary.valueOf(getAsInteger(index));
    }

    @Override
    public String getAsString(int index) {
        return Binary.valueOf(index).name();
    }

    @Override
    public boolean isNA(int index) {
        return getAsInteger(index) == IntVector.NA;
    }

    @Override
    public int compare(int a, int b) {
        return getAsInteger(a) - getAsInteger(b);
    }

    @Override
    public int size() {
        return values.length;
    }

    @Override
    public Type getType() {
        return TYPE;
    }

    @Override
    public Builder newCopyBuilder() {
        return new Builder(toIntArray());
    }

    public int[] toIntArray() {
        return values.clone();
    }

    public int[] asIntArray() {
        return values;
    }

    @Override
    public Builder newBuilder() {
        return new Builder();
    }

    @Override
    public Builder newBuilder(int size) {
        return new Builder(size);
    }

    public static class Builder implements Vector.Builder {

        private IntArrayList buffer;

        public Builder(int size) {
            this.buffer = new IntArrayList(size);
        }

        public Builder() {
            this(INITIAL_CAPACITY);
        }

        public Builder(int[] ints) {
            this.buffer = IntArrayList.from(ints);
        }

        @Override
        public Builder addNA(int index) {
            buffer.set(index, IntVector.NA);
            return this;
        }

        @Override
        public Builder addNA() {
            buffer.add(IntVector.NA);
            return this;
        }

        @Override
        public Vector.Builder add(Vector from, int fromIndex) {
            buffer.add(from.getAsInteger(fromIndex));
            return this;
        }

        @Override
        public Builder add(int atIndex, Vector from, int fromIndex) {
            buffer.set(atIndex, from.getAsInteger(fromIndex));
            return this;
        }

        @Override
        public Builder add(int index, Object value) {
            if (value instanceof Number) {
                buffer.set(index, ((Number) value).intValue());
            } else {
                buffer.add(index, IntVector.NA);
            }
            return this;
        }

        @Override
        public Builder add(Object value) {
            if (value instanceof Number) {
                buffer.add(((Number) value).intValue());
            } else {
                buffer.add(IntVector.NA);
            }
            return this;
        }

        @Override
        public int size() {
            return buffer.size();
        }

        @Override
        public BinaryVector create() {
            BinaryVector vector = new BinaryVector(buffer);
            buffer = null;
            return vector;
        }
    }
}

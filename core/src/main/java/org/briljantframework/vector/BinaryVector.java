package org.briljantframework.vector;

import com.carrotsearch.hppc.IntArrayList;
import com.google.common.collect.UnmodifiableIterator;

import java.util.Arrays;
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
        public boolean isNA(Object value) {
            return value == null ||
                    (value instanceof Binary && value == NA) ||
                    (value instanceof Integer && (int) value == IntVector.NA);
        }

        @Override
        public int compare(int a, Vector va, int b, Vector ba) {
            return va.getAsInt(a) - ba.getAsInt(b);
        }

        @Override
        public String toString() {
            return "binary";
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

    public BinaryVector(int... values) {
        this.values = Arrays.copyOf(values, values.length);
    }

    public static Builder newBuilderWithInitialValues(Object... values) {
        Builder builder = new Builder(0, values.length);
        builder.addAll(Arrays.asList(values));
        return builder;
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
        int i = getAsInt(index);
        if (i == IntVector.NA) {
            return DoubleVector.NA;
        } else {
            return i;
        }
    }

    @Override
    public int getAsInt(int index) {
        return values[index];
    }

    @Override
    public Binary getAsBinary(int index) {
        return Binary.valueOf(getAsInt(index));
    }

    @Override
    public String getAsString(int index) {
        Binary bin = Binary.valueOf(index);
        if (bin == Binary.NA) {
            return StringVector.NA;
        } else {
            return bin.name();
        }
    }

    @Override
    public Vector getAsVector(int index) {
        Binary binary = getAsBinary(index);
        return binary == NA ? Undefined.INSTANCE : new BinaryVector(binary.asInt());
    }

    @Override
    public String toString(int index) {
        return getAsBinary(index).name();
    }

    @Override
    public boolean isNA(int index) {
        return getAsInt(index) == IntVector.NA;
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

    @Override
    public Builder newBuilder() {
        return new Builder();
    }

    @Override
    public Builder newBuilder(int size) {
        return new Builder(size);
    }

    public int[] toIntArray() {
        return values.clone();
    }

    public int[] asIntArray() {
        return values;
    }

    @Override
    public int compare(int a, int b) {
        return getAsInt(a) - getAsInt(b);
    }

    public static class Builder implements Vector.Builder {

        private IntArrayList buffer;

        public Builder() {
            this(0);
        }

        public Builder(int size) {
            this(size, Math.max(INITIAL_CAPACITY, size));
        }

        public Builder(int size, int capacity) {
            buffer = new IntArrayList(capacity);
            for (int i = 0; i < size; i++) {
                buffer.add(IntVector.NA);
            }
        }

        public Builder(int[] ints) {
            this.buffer = IntArrayList.from(ints);
        }

        @Override
        public Builder setNA(int index) {
            ensureCapacity(index);
            buffer.buffer[index] = IntVector.NA;
            return this;
        }

        @Override
        public Builder addNA() {
            return setNA(size());
        }

        @Override
        public Builder add(Vector from, int fromIndex) {
            return set(size(), from, fromIndex);
        }

        @Override
        public Builder set(int atIndex, Vector from, int fromIndex) {
            ensureCapacity(atIndex);
            buffer.buffer[atIndex] = from.getAsInt(fromIndex);
            return this;
        }

        @Override
        public Builder set(int index, Object value) {
            ensureCapacity(index);
            int intValue = IntVector.NA;
            if (value instanceof Number) {
                intValue = ((Number) value).intValue();
            } else if (value instanceof Binary) {
                intValue = ((Binary) value).asInt();
            }
            buffer.buffer[index] = intValue;
            return this;
        }

        @Override
        public Builder add(Object value) {
            return set(size(), value);
        }

        @Override
        public Builder addAll(Vector from) {
            for (int i = 0; i < from.size(); i++) {
                add(from.getAsBinary(i));
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

        public Builder add(Binary binary) {
            return add(binary.asInt());
        }

        public Builder add(int value) {
            ensureCapacity(size());
            buffer.buffer[size()] = value;
            return this;
        }

        private void ensureCapacity(int index) {
            buffer.ensureCapacity(index + 1);
            int i = buffer.size();
            while (i <= index) {
                buffer.buffer[i++] = IntVector.NA;
                buffer.elementsCount++;
            }
        }
    }
}

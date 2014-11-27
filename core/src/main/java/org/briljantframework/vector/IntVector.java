package org.briljantframework.vector;

import com.carrotsearch.hppc.IntArrayList;
import com.google.common.collect.UnmodifiableIterator;
import com.google.common.primitives.Ints;

import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by Isak Karlsson on 20/11/14.
 */
public class IntVector extends AbstractIntVector {

    private final int[] values;

    public IntVector(int... values) {
        this.values = Arrays.copyOf(values, values.length);
    }

    public IntVector(int[] values, int size) {
        this.values = Arrays.copyOf(values, size);
    }

    public static Vector.Builder newBuilderWithInitialValues(int... values) {
        Builder builder = new Builder(0, values.length);
        for (int value : values) {
            builder.add(value);
        }
        return builder;
    }

    @Override
    public int getAsInt(int index) {
        return values[index];
    }

    @Override
    public int size() {
        return values.length;
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
    public Iterator<Integer> iterator() {
        return new UnmodifiableIterator<Integer>() {
            private int current = 0;

            @Override
            public boolean hasNext() {
                return current < size();
            }

            @Override
            public Integer next() {
                return getAsInt(current++);
            }
        };
    }

    @Override
    public String toString() {
        return IntStream.of(values).mapToObj(x -> x == NA ? "NA" : String.valueOf(x)).collect(Collectors.joining(","));
    }

    public static final class Builder implements Vector.Builder {

        private IntArrayList buffer;

        public Builder() {
            this(0, INITIAL_CAPACITY);
        }

        public Builder(int size) {
            this(size, size);
        }

        public Builder(int size, int capacity) {
            buffer = new IntArrayList(Math.max(size, capacity));
            for (int i = 0; i < size; i++) {
                buffer.add(NA);
            }
        }

        Builder(int[] values) {
            buffer = IntArrayList.from(values);
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
            if (value instanceof Number) {
                ensureCapacity(index);
                int intValue = ((Number) value).intValue();
                buffer.buffer[index] = intValue;
            } else {
                setNA(index);
            }

            return this;
        }

        @Override
        public Builder add(Object value) {
            return set(size(), value);
        }

        @Override
        public Builder addAll(Vector from) {
            for (int i = 0; i < from.size(); i++) {
                add(from.getAsInt(i));
            }

            return this;
        }

        @Override
        public void parseAndAdd(String value) {
            Integer integer = Ints.tryParse(value);
            if (integer == null) {
                addNA();
            } else {
                add(integer);
            }
        }

        @Override
        public int size() {
            return buffer.size();
        }

        @Override
        public IntVector create() {
            IntVector vector = new IntVector(buffer.buffer, buffer.size());
            buffer = null;
            return vector;
        }

        public Builder add(int value) {
            return set(size(), value);
        }

        public Builder set(int index, int value) {
            ensureCapacity(index);
            buffer.buffer[index] = value;
            return this;
        }

        private void ensureCapacity(int index) {
            buffer.ensureCapacity(index + 1);
            int i = buffer.size();
            while (i <= index) {
                buffer.buffer[i++] = NA;
                buffer.elementsCount++;
            }
        }
    }


}

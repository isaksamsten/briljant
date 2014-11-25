package org.briljantframework.vector;

import com.google.common.collect.UnmodifiableIterator;
import com.google.common.primitives.Doubles;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * A StringVector contains string values or NA.
 * <p>
 * TODO(isak): Perhaps string vectors should just be ObjectVector and store raw objects
 * TODO(isak): It might be wasteful to store equal objects multiple times. Consider having a subclass CompressedObjectVector or similar.
 * Created by Isak Karlsson on 20/11/14.
 */
public class StringVector implements Vector, Iterable<String> {

    public static final String NA = null;
    public static final Type TYPE = new Type() {
        @Override
        public Builder newBuilder() {
            return new Builder();
        }

        @Override
        public Builder newBuilder(int size) {
            return new Builder(size);
        }

        @Override
        public Class<?> getDataClass() {
            return String.class;
        }

        @Override
        public int compare(int a, Vector va, int b, Vector ba) {
            return !va.isNA(a) && !ba.isNA(b) ? va.getAsString(a).compareTo(ba.getAsString(b)) : 0;
        }

        @Override
        public String toString() {
            return "string";
        }
    };

    private final List<String> values;

    protected StringVector(List<String> values, boolean copy) {
        if (copy) {
            this.values = new ArrayList<>(values);
        } else {
            this.values = values;
        }
    }

    public StringVector(List<String> values) {
        this(values, true);
    }

    @Override
    public Iterator<String> iterator() {
        return new UnmodifiableIterator<String>() {
            private int current = 0;

            @Override
            public boolean hasNext() {
                return current < size();
            }

            @Override
            public String next() {
                return getAsString(current++);
            }
        };
    }

    @Override
    public double getAsDouble(int index) {
        return tryParseDouble(getAsString(index));
    }

    @Override
    public int getAsInt(int index) {
        return tryParseInteger(getAsString(index));
    }

    @Override
    public Binary getAsBinary(int index) {
        String str = getAsString(index);
        if (str == null) {
            return Binary.NA;
        } else if (str.equalsIgnoreCase("true")) {
            return Binary.TRUE;
        } else if (str.equalsIgnoreCase("false")) {
            return Binary.FALSE;
        } else {
            return Binary.NA;
        }
    }

    @Override
    public String getAsString(int index) {
        return values.get(index);
    }

    @Override
    public boolean isNA(int index) {
        return getAsString(index) == NA;
    }

    @Override
    public int compare(int a, int b) {
        return !isNA(a) && !isNA(b) ? getAsString(a).compareTo(getAsString(b)) : 0;
    }

    @Override
    public int size() {
        return values.size();
    }

    @Override
    public Type getType() {
        return TYPE;
    }

    @Override
    public Builder newCopyBuilder() {
        return new Builder(new ArrayList<>(values));
    }

    @Override
    public Builder newBuilder() {
        return new Builder();
    }

    @Override
    public Builder newBuilder(int size) {
        return new Builder(size);
    }

    @Override
    public String toString(int index) {
        String value = getAsString(index);
        return value == StringVector.NA ? "NA" : value;
    }

    protected double tryParseDouble(String str) {
        if (str == StringVector.NA) {
            return DoubleVector.NA;
        }
        Double d = Doubles.tryParse(str);
        if (d != null) {
            return d;
        } else {
            return DoubleVector.NA;
        }
    }

    protected int tryParseInteger(String str) {
        if (str == StringVector.NA) {
            return IntVector.NA;
        }
        Double i = Doubles.tryParse(str);
        if (i != null) {
            return i.intValue();
        } else {
            return IntVector.NA;
        }
    }

    public static Builder newBuilderWithInitialValues(Object... values) {
        Builder builder = new Builder(0, values.length);
        builder.addAll(Arrays.asList(values));
        return builder;
    }

    public static class Builder implements Vector.Builder {

        private ArrayList<String> buffer;

        public Builder() {
            this(0, INITIAL_CAPACITY);
        }

        public Builder(int size) {
            this(size, size);
        }

        public Builder(int size, int capacity) {
            buffer = new ArrayList<>(Math.max(size, capacity));
            for (int i = 0; i < size; i++) {
                buffer.add(StringVector.NA);
            }
        }

        public Builder(ArrayList<String> buffer) {
            this.buffer = buffer;
        }

        @Override
        public Builder setNA(int index) {
            ensureCapacity(index);
            buffer.set(index, StringVector.NA);
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
            buffer.set(atIndex, from.getAsString(fromIndex));
            return this;
        }

        @Override
        public Builder set(int index, Object value) {
            ensureCapacity(index);
            if (value == StringVector.NA) {
                buffer.set(index, StringVector.NA);
            } else {
                buffer.set(index, value.toString());
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
                add(from.getAsString(i));
            }
            return this;
        }

        private void ensureCapacity(int index) {
            while (buffer.size() <= index) {
                buffer.add(StringVector.NA);
            }
        }

        @Override
        public int size() {
            return buffer.size();
        }

        @Override
        public StringVector create() {
            return new StringVector(buffer, false);
        }
    }
}

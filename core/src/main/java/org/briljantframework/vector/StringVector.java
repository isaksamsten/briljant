package org.briljantframework.vector;

import com.google.common.collect.UnmodifiableIterator;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Ints;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Isak Karlsson on 20/11/14.
 */
public class StringVector implements Vector, Iterable<String> {

    public static final String NA = null;
    private static final Type TYPE = new Type() {
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
            return String.class;
        }

        @Override
        public int compare(int a, Vector va, int b, Vector ba) {
            return !va.isNA(a) && !ba.isNA(b) ? va.getAsString(a).compareTo(ba.getAsString(b)) : 0;
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
    public int getAsInteger(int index) {
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

    protected double tryParseDouble(String str) {
        Double d = Doubles.tryParse(str);
        if (d != null) {
            return d;
        } else {
            return DoubleVector.NA;
        }
    }

    protected int tryParseInteger(String str) {
        Integer i = Ints.tryParse(str);
        if (i != null) {
            return i;
        } else {
            return IntVector.NA;
        }
    }
}

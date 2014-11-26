package org.briljantframework.vector;

/**
 * Undefined is an immutable 0 size vector returning NA
 * <p>
 * Created by Isak Karlsson on 26/11/14.
 */
public class Undefined implements Vector {

    public static final String ILLEGAL = "Can't index undefined.";

    public static final Undefined INSTANCE = new Undefined();

    public static final Type TYPE = new Type() {
        @Override
        public Builder newBuilder() {
            return Builder.INSTANCE;
        }

        @Override
        public Builder newBuilder(int size) {
            return Builder.INSTANCE;
        }

        @Override
        public Class<?> getDataClass() {
            return Object.class;
        }

        @Override
        public boolean isNA(Object value) {
            return true;
        }

        @Override
        public int compare(int a, Vector va, int b, Vector ba) {
            return 0;
        }
    };

    @Override
    public double getAsDouble(int index) {
        return DoubleVector.NA;
    }

    @Override
    public int getAsInt(int index) {
        return IntVector.NA;
    }

    @Override
    public Binary getAsBinary(int index) {
        return BinaryVector.NA;
    }

    @Override
    public String getAsString(int index) {
        return StringVector.NA;
    }

    @Override
    public Vector getAsVector(int index) {
        return this;
    }

    @Override
    public String toString(int index) {
        return "NA";
    }

    @Override
    public boolean isNA(int index) {
        return true;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public Type getType() {
        return TYPE;
    }

    @Override
    public Builder newCopyBuilder() {
        return Builder.INSTANCE;
    }

    @Override
    public Builder newBuilder() {
        return Builder.INSTANCE;
    }

    @Override
    public Builder newBuilder(int size) {
        return Builder.INSTANCE;
    }

    @Override
    public int compare(int a, int b) {
        return 0;
    }

    public static class Builder implements Vector.Builder {

        public static final Builder INSTANCE = new Builder();


        private Builder() {

        }

        @Override
        public Builder setNA(int index) {
            return this;
        }

        @Override
        public Vector.Builder addNA() {
            return this;
        }

        @Override
        public Vector.Builder add(Vector from, int fromIndex) {
            return this;
        }

        @Override
        public Vector.Builder set(int atIndex, Vector from, int fromIndex) {
            return this;
        }

        @Override
        public Vector.Builder set(int index, Object value) {
            return this;
        }

        @Override
        public Vector.Builder add(Object value) {
            return this;
        }

        @Override
        public Vector.Builder addAll(Vector from) {
            return this;
        }

        @Override
        public int size() {
            return 0;
        }

        @Override
        public Vector create() {
            return Undefined.INSTANCE;
        }
    }
}

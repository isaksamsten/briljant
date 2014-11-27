package org.briljantframework.vector;

/**
 * Created by Isak Karlsson on 27/11/14.
 */
public abstract class AbstractComplexVector implements Vector, Iterable<Complex> {
    public static final Type TYPE = new Type() {
        @Override
        public ComplexVector.Builder newBuilder() {
            return new ComplexVector.Builder();
        }

        @Override
        public ComplexVector.Builder newBuilder(int size) {
            return new ComplexVector.Builder(size);
        }

        @Override
        public Class<?> getDataClass() {
            return Complex.class;
        }

        @Override
        public boolean isNA(Object value) {
            return value == null || (value instanceof Complex && ((Complex) value).isNaN());
        }

        @Override
        public int compare(int a, Vector va, int b, Vector ba) {
            throw new UnsupportedOperationException("Can't compare complex numbers");
        }

        @Override
        public Scale getScale() {
            return Scale.NUMERICAL;
        }

        @Override
        public String toString() {
            return "complex";
        }
    };
    public static final Complex NA = Complex.NaN;

    /**
     * {@inheritDoc}
     */
    @Override
    public int getAsInt(int index) {
        double value = getAsDouble(index);
        if (Double.isNaN(value)) {
            return IntVector.NA;
        } else {
            return (int) value;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Binary getAsBinary(int index) {
        return Binary.valueOf(getAsInt(index));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAsString(int index) {
        Complex complex = getAsComplex(index);
        if (complex.isNaN()) {
            return StringVector.NA;
        } else {
            return complex.toString();
        }
    }

    @Override
    public Value getAsValue(int index) {
        Complex complex = getAsComplex(index);
        return complex.isNaN() ? Undefined.INSTANCE : new ComplexValue(complex);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString(int index) {
        Complex complex = getAsComplex(index);
        return complex.isNaN() ? "NA" : complex.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isNA(int index) {
        return Double.isNaN(getAsDouble(index));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Type getType() {
        return TYPE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compare(int a, int b) {
        throw new UnsupportedOperationException("Can't compare complex numbers.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compare(int a, int b, Vector other) {
        throw new UnsupportedOperationException("Can't compare complex number.");
    }
}

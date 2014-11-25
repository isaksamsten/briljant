package org.briljantframework.vector;

import java.io.Serializable;

/**
 * A vector is a homogeneous list of values which supports missing values (i.e. NA).
 * <p>
 * Created by Isak Karlsson on 20/11/14.
 */
public interface Vector extends Serializable {

    /**
     * Returns value as {@code double} if applicable. Otherwise returns {@link org.briljantframework.vector.DoubleVector#NA}.
     *
     * @param index the index
     * @return a double
     */
    double getAsDouble(int index);

    /**
     * Returns value as {@code int} if applicable. Otherwise returns {@link org.briljantframework.vector.IntVector#NA}
     *
     * @param index the index
     * @return an int
     */
    int getAsInt(int index);

    /**
     * Returns value as {@link org.briljantframework.vector.Binary}.
     *
     * @param index the index
     * @return a {@link org.briljantframework.vector.Binary}
     */
    Binary getAsBinary(int index);

    /**
     * Returns value as {@link java.lang.String}, {@code null} is used to denot missing values.
     *
     * @param index the index
     * @return a {@code String} or {@code null}
     */
    String getAsString(int index);

    /**
     * Return the string representation of the value at {@code index}
     *
     * @param index the index
     * @return the string representation. Returns "NA" if value is missing.
     */
    String toString(int index);

    /**
     * Returns value as {@link org.briljantframework.vector.Complex} or
     * {@link org.briljantframework.vector.ComplexVector#NA} if missing.
     *
     * @param index the index
     * @return a {@link org.briljantframework.vector.Complex}
     */
    default Complex getAsComplex(int index) {
        double value = getAsDouble(index);
        if (Double.isNaN(value)) {
            return ComplexVector.NA;
        }
        return new Complex(value, 0);
    }

    /**
     * Returns {@code true} if value at {@code index} is considered to be true.
     * <p>
     * The following conventions apply:
     * <ul>
     * <li><pre>1.0 == TRUE</pre></li>
     * <li><pre>1 == TRUE</pre></li>
     * <li><pre>"true" == TRUE</pre></li>
     * <li><pre>Binary.TRUE == TRUE</pre></li>
     * </ul>
     * <p>
     * All other values are considered to be FALSE
     *
     * @param index the index
     * @return true or false
     */
    default boolean isTrue(int index) {
        return getAsBinary(index) == Binary.TRUE;
    }

    /**
     * Returns true if value at {@code index} is NA
     *
     * @param index the index
     * @return true or false
     */
    boolean isNA(int index);

    /**
     * Returns true there ara any NA values
     *
     * @return true or false
     */
    default boolean hasNA() {
        for (int i = 0; i < size(); i++) {
            if (isNA(i)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Follows the conventions from {@link java.lang.Comparable#compareTo(Object)}.
     * <p>
     * Returns value {@code <} 0 if value at index {@code a} is less than {@code b}.
     * value {@code > 0} if value at index {@code b} is larger than {@code a} and 0
     * if they are equal.
     *
     * @param a the index a
     * @param b the index b
     * @return comparing int
     * @see java.lang.Comparable#compareTo(Object)
     */
    int compare(int a, int b);

    /**
     * Returns the size of the vector
     *
     * @return size
     */
    int size();

    /**
     * Get the type of the vector.
     *
     * @return the type
     */
    Type getType();

    /**
     * Creates a new builder able to build new vectors of this type, initialized with
     * the values in this builder.
     * <p>
     * <code>
     * <pre>
     *         Vector.Builder builder = vector.newCopyBuilder()
     *         builder.add("Hello world")
     *
     *         assert builder.create().size() == vector.size() + 1
     *         assert builder.create().getAsString(0) == vector.getAsString(0)
     *     </pre>
     * </code>
     *
     * @return a new builder
     */
    Builder newCopyBuilder();


    /**
     * Creates a new builder able to build vectors of this type
     *
     * @return a new builder
     */
    Builder newBuilder();

    /**
     * Creates a new builder able to build vectors of this type
     *
     * @param size the initial size
     * @return a new builder
     */
    Builder newBuilder(int size);

    /**
     * Returns a copy of this vector as a int array.
     *
     * @return an array copy of this vector.
     */
    default int[] toIntArray() {
        int[] values = new int[size()];
        for (int i = 0; i < size(); i++) {
            values[i] = getAsInt(i);
        }
        return values;
    }

    /**
     * Returns a, possibly underlying, int array representation of this vector. Mutations
     * of this array is possibly unsafe.
     * <p>
     * The default implementation is however safe.
     *
     * @return a possible unsafe underlying array
     */
    default int[] asIntArray() {
        return toIntArray();
    }

    /**
     * Returns a copy of this vector as a double array
     *
     * @return an array copy of this vector
     */
    default double[] toDoubleArray() {
        double[] values = new double[size()];
        for (int i = 0; i < size(); i++) {
            values[i] = getAsDouble(i);
        }
        return values;
    }

    /**
     * Returns a, possibly underlying, double representation of this vector. Mutations
     * of this array is possibly unsafe.
     * <p>
     * The default implementation is however safe.
     *
     * @return a possibly unsafe underlying array
     */
    default double[] asDoubleArray() {
        return toDoubleArray();
    }

    /**
     * Builds new vectors
     */
    public static interface Builder {

        /**
         * Recommended initial capacity
         */
        int INITIAL_CAPACITY = 50;

        /**
         * Add NA at {@code index}. If {@code index > size()} the resulting vector
         * should be padded with NA:s between {@code size} and {@code index} and {@code index} set to NA.
         *
         * @param index the index
         * @return a modified builder
         */
        Builder setNA(int index);

        /**
         * Same as {@code addNA(size())}
         *
         * @return a modified builder
         */
        Builder addNA();

        /**
         * Same as {@code add(size(), from, fromIndex)}
         *
         * @param from      the vector to take the value from
         * @param fromIndex the index
         * @return a modified builder
         */
        Builder add(Vector from, int fromIndex);

        /**
         * Add value at {@code fromIndex} in {@code from} to {@code atIndex}. Padding with NA:s
         * between {@code atIndex} and {@code size()} if {@code atIndex > size()}.
         *
         * @param atIndex   the index
         * @param from      the vector to take the value from
         * @param fromIndex the index
         * @return a modified build
         */
        Builder set(int atIndex, Vector from, int fromIndex);

        /**
         * Add {@code value} at {@code index}. Padding with NA:s
         * between {@code atIndex} and {@code size()} if {@code atIndex > size()}.
         * <p>
         * If value {@code value} cannot be added to this vector type, a NA value is added instead.
         *
         * @param index the index
         * @param value the value
         * @return a modified builder
         */
        Builder set(int index, Object value);

        /**
         * Same as {@code add(size(), value)}
         *
         * @param value the value
         * @return a modified builder
         */
        Builder add(Object value);

        /**
         * Add all values in {@code from} to this builder.
         *
         * @param from the vector
         * @return a modified builder
         */
        Builder addAll(Vector from);

        /**
         * Add all values in iterable
         *
         * @param iterable the collection of values
         */
        default void addAll(Iterable<?> iterable) {
            iterable.forEach(this::add);
        }

        /**
         * Returns the size of the resulting vector
         *
         * @return the size
         */
        int size();

        /**
         * Create a new vector of suitable type. This interface does not provide any guarantees to
         * whether or not it is possible to construct several vectors using the same builder.
         * Most commonly, once {@code create()} has been called subsequent calls on the builder will fail.
         *
         * @return a new vector
         */
        Vector create();
    }


    /**
     * Provides information of a particular vectors type. The common choice is that referential equality is used
     * when comparing types.
     */
    public static interface Type {

        /**
         * Creates a new builder able to build vectors of this type
         *
         * @return a new builder
         */
        Builder newBuilder();

        /**
         * Creates a new builder able to build vectors of this type
         *
         * @param size initial size (the vector is padded with NA)
         * @return a new builder
         */
        Builder newBuilder(int size);

        /**
         * Copy (and perhaps convert) {@code vector} to this type
         *
         * @param vector the vector to copy
         * @return a new vector
         */
        default Vector copyFrom(Vector vector) {
            return newBuilder(vector.size()).addAll(vector).create();
        }

        /**
         * Get the underlying class used to represent values of this vector type
         *
         * @return the class
         */
        Class<?> getDataClass();

        /**
         * Compare value at position {@code a} from {@code va} to value at
         * position {@code b} from {@code va}.
         *
         * @param a  the index in va
         * @param va the vector
         * @param b  the index in ba
         * @param ba the vector
         * @return the comparison
         */
        int compare(int a, Vector va, int b, Vector ba);

        /**
         * Check if value at position {@code a} from {@code va} and value at
         * position {@code b} from {@code va} are equal.
         *
         * @param a  the index in va
         * @param va the vector
         * @param b  the index in ba
         * @param ba the vector
         * @return true if equal false otherwise
         */
        default boolean equals(int a, Vector va, int b, Vector ba) {
            return compare(a, va, b, ba) == 0;
        }
    }
}

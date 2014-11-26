package org.briljantframework.vector;

/**
 * Provides information of a particular vectors type. The common choice is that referential equality is used
 * when comparing types.
 */
public interface Type {

    /**
     * Creates a new builder able to build vectors of this type
     *
     * @return a new builder
     */
    Vector.Builder newBuilder();

    /**
     * Creates a new builder able to build vectors of this type
     *
     * @param size initial size (the vector is padded with NA)
     * @return a new builder
     */
    Vector.Builder newBuilder(int size);

    /**
     * Copy (and perhaps convert) {@code vector} to this type
     *
     * @param vector the vector to copy
     * @return a new vector
     */
    default Vector copy(Vector vector) {
        return newBuilder(vector.size()).addAll(vector).create();
    }

    /**
     * Get the underlying class used to represent values of this vector type
     *
     * @return the class
     */
    Class<?> getDataClass();

    /**
     * Returns true if this object is NA for this value type
     *
     * @param value the value
     * @return true if value is NA
     */
    boolean isNA(Object value);

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

package org.briljantframework.vector;

import java.io.IOException;
import java.io.Serializable;

import org.briljantframework.io.DataEntry;

/**
 * A vector is an homogeneous (i.e. with values of only one type) and immutable (i.e. the contents
 * cannot change) array of values supporting missing entries (i.e. NA). Since NA values are
 * implemented differently depending value type, checking for NA-values are done via the
 * {@link #isNA(int)} method. For the default types, the {@link Is#NA} is available.
 * <p>
 * The vector interface extends the {@link VectorLike} interface.
 *
 * @author Isak Karlsson
 */
public interface Vector extends VectorLike, Serializable {

  /**
   * Returns value as {@link org.briljantframework.vector.Value}.
   * {@link org.briljantframework.vector.Undefined} denotes missing values.
   *
   * While wrapping the return type in a {@code Value} require additional space and impose some
   * overhead it brings the benefit of knowing the type of a particular value, which in some cases
   * might be useful.
   *
   * @param index the index
   * @return a {@code Vector}
   */
  Value getAsValue(int index);

  /**
   * Return the string representation of the value at {@code index}
   *
   * @param index the index
   * @return the string representation. Returns "NA" if value is missing.
   */
  String toString(int index);

  /**
   * Returns {@code true} if value at {@code index} is considered to be true.
   * <p>
   * The following conventions apply:
   * <ul>
   * <li>{@code 1.0 == TRUE}</li>
   * <li>{@code 1 == TRUE}</li>
   * <li>{@code &quot;true&quot; == TRUE}</li>
   * <li>{@code Binary.TRUE == TRUE}</li>
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
   * {@inheritDoc}
   */
  @Override
  default double get(int index) {
    return getAsDouble(index);
  }

  /**
   * Returns value as {@code double} if applicable. Otherwise returns {@link DoubleVector#NA}.
   *
   * @param index the index
   * @return a double
   */
  double getAsDouble(int index);

  /**
   * Returns value as {@code int} if applicable. Otherwise returns {@link IntVector#NA}
   *
   * @param index the index
   * @return an int
   */
  int getAsInt(int index);

  /**
   * Returns value as {@link Binary}.
   *
   * @param index the index
   * @return a {@link Binary}
   */
  Binary getAsBinary(int index);

  /**
   * Returns value as {@link Complex} or {@link ComplexVector#NA} if missing.
   *
   * @param index the index
   * @return a {@link Complex}
   */
  default Complex getAsComplex(int index) {
    double value = getAsDouble(index);
    if (Double.isNaN(value)) {
      return ComplexVector.NA;
    }
    return new Complex(value, 0);
  }

  /**
   * Returns value as {@link String}, {@code null} is used to denote missing values.
   *
   * @param index the index
   * @return a {@code String} or {@code null}
   */
  String getAsString(int index);

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
   * Creates a new builder able to build new vectors of this type, initialized with the values in
   * this builder.
   * <p>
   * <code>
   * <pre>
   *         Vector vec = vector.newCopyBuilder().add("Hello world")
   * 
   *         assert vec.size() == vector.size() + 1
   *         assert vec.getAsString(0) == vector.getAsString(0)
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
   * Creates a new builder able to build vectors of this type. The constructed builder produces a
   * vector of length {@code size}, filled with NA.
   * 
   * TODO(isak): add {@code newBuilderWithInitialCapacity(int capacity)}
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
   * Returns a, possibly underlying, int array representation of this vector. Mutations of this
   * array is possibly unsafe.
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
   * Returns a, possibly underlying, double representation of this vector. Mutations of this array
   * is possibly unsafe.
   * <p>
   * The default implementation is however safe.
   *
   * @return a possibly unsafe underlying array
   */
  default double[] asDoubleArray() {
    return toDoubleArray();
  }

  /**
   * Follows the conventions from {@link Comparable#compareTo(Object)}.
   * <p>
   * Returns value {@code <} 0 if value at index {@code a} is less than {@code b}. value {@code > 0}
   * if value at index {@code b} is larger than {@code a} and 0 if they are equal.
   *
   * @param a the index a
   * @param b the index b
   * @return comparing int
   * @see Comparable#compareTo(Object)
   */
  int compare(int a, int b);

  /**
   * Compare value at {@code a} in {@code this} to value at {@code b} in {@code ba}. Equivalent to
   * {@code this.getAsValue(a).compareTo(other.getAsValue(b))}, but in most circumstances with
   * greater performance.
   *
   * @param a the index in {@code this}
   * @param b the index in {@code other}
   * @param other the other vector
   * @return the comparison
   * @see java.lang.Comparable#compareTo(Object)
   */
  int compare(int a, int b, Vector other);

  /**
   * Compare value at position {@code a} in {@code this} to {@code other}. Equivalent to
   * {@code this.getAsValue(a).compareTo(other)} but in most circumstances with greater performance.
   *
   * @param a the index in {@code this}
   * @param other the value
   * @return the comparison
   */
  default int compare(int a, Value other) {
    return compare(a, 0, other);
  }

  /**
   * Builds new vectors TODO(isak): maybe builder.getAs... and set(int at, Vector.Builder b, int
   * from)?
   */
  public static interface Builder {

    /**
     * Recommended initial capacity
     */
    int INITIAL_CAPACITY = 50;

    /**
     * Add NA at {@code index}. If {@code index > size()} the resulting vector should be padded with
     * NA:s between {@code size} and {@code index} and {@code index} set to NA.
     *
     * @param index the index
     * @return a modified builder
     */
    Builder setNA(int index);

    /**
     * Same as {@code addNA(size())}
     *
     * @return a modified builder
     * @see #setNA(int)
     */
    Builder addNA();

    /**
     * Same as {@code add(size(), from, fromIndex)}
     *
     * @param from the vector to take the value from
     * @param fromIndex the index
     * @return a modified builder
     */
    Builder add(Vector from, int fromIndex);

    /**
     * Same as {@code add(value, 0)} (i.e. a more convinient way of adding 1-length vectors)
     * 
     * @param value the value
     * @return a modified builder
     */
    default Builder add(Value value) {
      return add(value, 0);
    }

    /**
     * Add value at {@code fromIndex} in {@code from} to {@code atIndex}. Padding with NA:s between
     * {@code atIndex} and {@code size()} if {@code atIndex > size()}.
     *
     * @param atIndex the index
     * @param from the vector to take the value from
     * @param fromIndex the index
     * @return a modified build
     */
    Builder set(int atIndex, Vector from, int fromIndex);

    /**
     * Add {@code value} at {@code index}. Padding with NA:s between {@code atIndex} and
     * {@code size()} if {@code atIndex > size()}.
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
    default Builder addAll(Iterable<?> iterable) {
      iterable.forEach(this::add);
      return this;
    }

    Builder remove(int index);

    /**
     * Swaps value at {@code a} with value at {@code b}
     * 
     * @param a the first index
     * @param b the seconds index
     */
    Builder swap(int a, int b);

    /**
     * Reads a value from the input stream and appends it to the builder (after the last value).
     * 
     * @param entry the input stream
     * @return receiver modified
     * @throws IOException if {@code inputStream} fail
     */
    Builder read(DataEntry entry) throws IOException;

    /**
     * Reads a value from the input stream and set {@code index} to the next value in the stream.
     * 
     * @param index the index
     * @param entry the input stream
     * @return receiver modified
     * @throws IOException if {@code inputStream} fail
     */
    Builder read(int index, DataEntry entry) throws IOException;

    /**
     * Returns the size of the resulting vector
     *
     * @return the size
     */
    int size();

    /**
     * Create a new vector of suitable type. This interface does not provide any guarantees to
     * whether or not it is possible to construct several vectors using the same builder. Most
     * commonly, once {@code create()} has been called subsequent calls on the builder will fail.
     *
     * @return a new vector
     */
    Vector build();
  }


}

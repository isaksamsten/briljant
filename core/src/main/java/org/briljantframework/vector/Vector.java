package org.briljantframework.vector;

import org.briljantframework.Swappable;
import org.briljantframework.complex.Complex;
import org.briljantframework.exceptions.TypeConversionException;
import org.briljantframework.io.DataEntry;
import org.briljantframework.matrix.Matrix;

import java.io.IOException;
import java.io.Serializable;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * <p> A vector is an homogeneous (i.e. with values of only one type) and immutable (i.e. the
 * contents cannot change) array of values supporting missing entries (i.e. NA). Since NA values are
 * implemented differently depending value type, checking for NA-values are done via the {@link
 * #isNA(int)} method. For the default types, the {@link Is#NA} is available. </p>
 *
 * <p> Implementers must ensure that <ul> <li>{@link #hashCode()} and {@link #equals(Object)}</li>
 * work as expected. <li>The vector cannot be changed, i.e. a vector cannot expose it's underlying
 * implementation and be mutated. This simplifies parallel algorithms.</li> </ul> </p>
 *
 * @author Isak Karlsson
 */
public interface Vector extends Serializable {

  /**
   * Returns value as {@link org.briljantframework.vector.Value}. {@link
   * org.briljantframework.vector.Undefined} denotes missing values.
   *
   * While wrapping the return type in a {@code Value} require additional space and impose some
   * overhead it brings the benefit of knowing the type of a particular value, which in some cases
   * is very useful.
   *
   * @param index the index
   * @return a {@code Vector}
   */
  Value get(int index);

  /**
   * Returns the value at {@code index} as an instance of {@code Class<T>}. If value at {@code
   * index} is not an instance of {@code cls}, returns an appropriate {@code NA} value. For
   * references types (apart from {@code Complex}) this means {@code null} and for {@code primitive}
   * types their respective {@code NA} value. Hence, checking for {@code null} does not always work,
   * instead {@link Is#NA(Object)} (and comrades) should be used.
   *
   * <pre>
   *   Vector v = new GenericVector(Date.class, Arrays.asList(new Date(), new Date());
   *   Date date = v.getAs(Date.class, 0);
   *   if(Is.NA(date)) { // or date == null
   *     // got a NA value
   *   }
   *
   *   Vector v = ...; // for example an IntVector
   *   int value = v.getAs(Integer.class, 32);
   *   if(Is.NA(value)) { // or value == IntVector.NA (but not value == null)
   *     // got a NA value
   *   }
   * </pre>
   *
   * @param cls   the class
   * @param index the index
   * @param <T>   the type
   * @return a value of type; returns {@code NA} if value is not an instance of {@code cls}
   */
  <T> T get(Class<T> cls, int index);

  /**
   * Return the string representation of the value at {@code index}
   *
   * @param index the index
   * @return the string representation. Returns "NA" if value is missing.
   */
  String toString(int index);

  /**
   * Returns {@code true} if value at {@code index} is considered to be true. <p> The following
   * conventions apply: <ul> <li>{@code 1.0+-0i == TRUE}</li> <li>{@code 1.0 == TRUE}</li>
   * <li>{@code 1 == TRUE}</li> <li>{@code &quot;true&quot; == TRUE}</li> <li>{@code Binary.TRUE ==
   * TRUE}</li> </ul> <p> All other values are considered to be FALSE
   *
   * @param index the index
   * @return true or false
   */
  /**/
  default boolean isTrue(int index) {
    return getAsBit(index) == Bit.TRUE;
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
   * Returns value as {@link Bit}.
   *
   * @param index the index
   * @return a {@link Bit}
   */
  Bit getAsBit(int index);

  /**
   * Returns value as {@link org.briljantframework.complex.Complex} or {@link ComplexVector#NA} if
   * missing.
   *
   * @param index the index
   * @return a {@link org.briljantframework.complex.Complex}
   */
  default Complex getAsComplex(int index) {
    double value = getAsDouble(index);
    if (Is.NA(value)) {
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
  VectorType getType();

  /**
   * Get type of value at {@code index}
   *
   * @param index the index
   * @return the type of value
   */
  VectorType getType(int index);

  /**
   * For values implementing {@link java.lang.Comparable}, {@link org.briljantframework.vector.Scale#NUMERICAL}
   * should be returned; otherwise {@link org.briljantframework.vector.Scale#NOMINAL}.
   */
  default Scale getScale() {
    return getType().getScale();
  }

  /**
   * Creates a new builder able to build new vectors of this type, initialized with the values in
   * this builder. <p>
   *
   * <pre>
   * Vector vec = vector.newCopyBuilder().add("Hello world")
   * assert vec.size() == vector.size() + 1
   * assert vec.getAsString(0) == vector.getAsString(0)
   * </pre>
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
   * array is possibly unsafe. <p> The default implementation is however safe.
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
   * is possibly unsafe. <p> The default implementation is however safe.
   *
   * @return a possibly unsafe underlying array
   */
  default double[] asDoubleArray() {
    return toDoubleArray();
  }

  /**
   * Returns a sequential {@code Stream} of values with this vector as its source.
   *
   * @return a sequential {@code Stream} over the elements of this {@code Vector}.
   */
  default Stream<Value> stream() {
    return StreamSupport.stream(asValueList().spliterator(), false);
  }

  /**
   * Returns a parallel {@code Stream} of values with this {@code Vector} as its source.
   *
   * @return a parallel {@code Stream} over the elements of this {@code Vector}
   */
  default Stream<Value> parallelStream() {
    return StreamSupport.stream(asValueList().spliterator(), true);
  }

  /**
   * Returns this Vector as an {@link java.util.List} of {@link org.briljantframework.vector.Value}.
   * The returned list is unmodifiable
   *
   * @return an unmodifiable list
   */
  default List<Value> asValueList() {
    return new AbstractList<Value>() {
      @Override
      public Value get(int i) {
        return Vector.this.get(i);
      }

      @Override
      public int size() {
        return Vector.this.size();
      }
    };
  }

  /**
   * Returns this vector as an immutable {@code Matrix}. Should return an appropriate specialization
   * of the {@link org.briljantframework.matrix.Matrix} interface. For example, a {@link
   * org.briljantframework.vector.DoubleVector} should return a {@link
   * org.briljantframework.matrix.DoubleMatrix} implementation.
   *
   * Since {@code Vector}s are immutable, mutations of the returned matrix throws {@link
   * org.briljantframework.exceptions.ImmutableModificationException}.
   *
   * <pre>
   * Vector a = new DoubleVector(1, 2, 3, 4, 5);
   * DoubleMatrix mat = a.asMatrix().asDoubleMatrix();
   * double sum = mat.reduce(0, Double::sum);
   * </pre>
   *
   * @return this vector as a matrix
   * @throws org.briljantframework.exceptions.TypeConversionException if unable to convert vector to
   *                                                                  matrix
   */
  Matrix asMatrix() throws TypeConversionException;

  /**
   * Follows the conventions from {@link Comparable#compareTo(Object)}. <p> Returns value {@code <}
   * 0 if value at index {@code a} is less than {@code b}. value {@code > 0} if value at index
   * {@code b} is larger than {@code a} and 0 if they are equal.
   *
   * @param a the index a
   * @param b the index b
   * @return comparing int
   * @see Comparable#compareTo(Object)
   */
  int compare(int a, int b);

  /**
   * Compare value at {@code a} in {@code this} to value at {@code b} in {@code ba}. Equivalent to
   * {@code this.get(a).compareTo(other.get(b))}, but in most circumstances with greater
   * performance.
   *
   * @param a     the index in {@code this}
   * @param other the other vector
   * @param b     the index in {@code other}
   * @return the comparison
   * @see java.lang.Comparable#compareTo(Object)
   */
  int compare(int a, Vector other, int b);

  /**
   * Returns true if element at {@code a} in {@code this} equals element at {@code b} in {@code
   * other}.
   *
   * @param a     the index in this
   * @param other the other vector
   * @param b     the index in other
   * @return true if values are equal
   */
  default boolean equals(int a, Vector other, int b) {
    return compare(a, other, b) == 0;
  }

  /**
   * Compare value at position {@code a} in {@code this} to {@code other}. Equivalent to {@code
   * this.get(a).compareTo(other)} but in most circumstances with greater performance.
   *
   * @param a     the index in {@code this}
   * @param other the value
   * @return the comparison
   */
  default int compare(int a, Value other) {
    return compare(a, other, 0);
  }

  /**
   * <p> Builds a new vector. A builder can incrementally grow, but not allow gaps. For example, if
   * a builder is initialized with size {@code 8}, {@link #add(Object)} (et. al.) adds a value at
   * index {@code 8} and indexes {@code 0-7} have the value {@code NA}. If the value at index {@code
   * 11} is set, values {@code 9, 10} are set to {@code NA}. </p>
   *
   * <p> When transferring values between vectors, prefer {@link #set(int, Vector, int)} to {@link
   * #set(int, Object)}. For example, {@code Vector.Builder a; Vector b; a.set(0, b, 10)} sets the
   * value of {@code a} at index {@code 0} to the value at index {@code 10} in {@code b}. This
   * avoids unboxing values from one vector to another. For the numerical vectors, values are
   * coerced, e.g. {@code 1} from an int-vector becomes {@code 1.0} in a double vector or {@code
   * Bit.TRUE} in a bit-vector. </p>
   */
  public static interface Builder extends Swappable {

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
     * @param from      the vector to take the value from
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
     * @param atIndex   the index
     * @param from      the vector to take the value from
     * @param fromIndex the index
     * @return a modified build
     */
    Builder set(int atIndex, Vector from, int fromIndex);

    default Builder set(int atIndex, Value from) {
      return set(atIndex, from, 0);
    }

    /**
     * Add {@code value} at {@code index}. Padding with NA:s between {@code atIndex} and {@code
     * size()} if {@code atIndex > size()}. <p> If value {@code value} cannot be added to this
     * vector type, a NA value is added instead.
     *
     * <p>How values are resolved depend on the implementation but {@code null} always result in
     * {@code NA} and an instance of {@link Value} always results in the value carried by the value.
     * Finally, if {@link org.briljantframework.io.reslover.Resolvers#find(Class)} return a non-null
     * value {@link org.briljantframework.io.reslover.Resolver#resolve(Class, Object)} is used
     * (where the former {@code Class} is the value in the vector and the latter {@code Class} is
     * {@code value.getClass()}).
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

    default Builder addAll(Collection<Object> collection) {
      collection.forEach(this::add);
      return this;
    }

    default Builder addAll(Object... objects) {
      return addAll(Arrays.asList(objects));
    }

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

    /**
     * Removes value at {@code index} and shifts element to the left.
     *
     * @param index the index
     * @return a modified builder with element at {@code index} removed
     */
    Builder remove(int index);

    /**
     * Compares value at {@code a} and {@code b}.
     *
     * @param a first index
     * @param b second index
     * @return cmp < 0 if value at {@code a} is less than {@code b}, cmp > 0 if value at a is
     * greater than b and 0 otherwise
     */
    int compare(int a, int b);

    /**
     * Swaps value at {@code a} with value at {@code b}
     *
     * @param a the first index
     * @param b the seconds index
     */
    void swap(int a, int b);

    default Builder readAll(DataEntry entry) throws IOException {
      while (entry.hasNext()) {
        read(entry);
      }
      return this;
    }

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
     * <p> Returns a temporary vector. Modifications to the builder (such as, e.g., {@link
     * #swap(int, int)}) is propagated to the temporary vector, allowing changes to be tracked
     * withing the builder. </p>
     *
     * @return the temporary vector.
     */
    Vector getTemporaryVector();

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

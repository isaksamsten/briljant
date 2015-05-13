package org.briljantframework.vector;

import org.briljantframework.complex.Complex;
import org.briljantframework.dataframe.Aggregator;
import org.briljantframework.exceptions.TypeConversionException;
import org.briljantframework.io.DataEntry;
import org.briljantframework.matrix.BitMatrix;
import org.briljantframework.matrix.ComplexMatrix;
import org.briljantframework.matrix.DoubleMatrix;
import org.briljantframework.matrix.IntMatrix;
import org.briljantframework.matrix.LongMatrix;
import org.briljantframework.matrix.Matrix;
import org.briljantframework.sort.Swappable;

import java.io.IOException;
import java.io.Serializable;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * <p> A vector is an homogeneous (i.e. with values of only one type) and immutable (i.e. the
 * contents cannot change) array of values supporting missing entries (i.e. NA). Since NA values
 * are
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

  <T, O> Vector transform(Class<T> in, Class<O> out, Function<T, O> operator);

  <T> Vector transform(Class<T> cls, UnaryOperator<T> operator);

  <T> Vector filter(Class<T> cls, Predicate<T> predicate);

  <T, R, C> R aggregate(Class<? extends T> in, Aggregator<? super T, ? extends R, C> aggregator);

  <T> Vector combine(Class<? extends T> cls, Vector other,
                     BinaryOperator<T> combiner);

  /**
   * Returns the value at {@code index} as an instance of {@code T}. If value at {@code index} is
   * not an instance of {@code cls}, returns an appropriate {@code NA} value. For references types
   * (apart from {@code Complex} and {@code Bit}) this means {@code null} and for {@code primitive}
   * types their respective {@code NA} value. Hence, checking for {@code null} does not always
   * work,
   * instead {@link Is#NA(Object)} (and comrades) should be used.
   *
   * <pre>
   *   Vector v = new GenericVector(Date.class, Arrays.asList(new Date(), new Date());
   *   Date date = v.get(Date.class, 0);
   *   if(Is.NA(date)) { // or date == null
   *     // got a NA value
   *   }
   *
   *   Vector v = ...; // for example an IntVector
   *   int value = v.get(Integer.class, 32);
   *   if(Is.NA(value)) { // or value == IntVector.NA (but not value == null)
   *     // got a NA value
   *   }
   * </pre>
   *
   * <p> {@link java.lang.ClassCastException} should not be thrown, instead {@code NA} should be
   * returned
   *
   * @param cls   the class
   * @param index the index
   * @param <T>   the type
   * @return a value of type; returns {@code NA} if value is not an instance of {@code cls}
   * @throws java.lang.IndexOutOfBoundsException if {@code index < 0 || index > size()}
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
   * conventions apply:
   *
   * <ul>
   * <li>{@code 1.0+-0i == TRUE}</li>
   * <li>{@code 1.0 == TRUE}</li>
   * <li>{@code 1 == TRUE}</li>
   * <li>{@code &quot;true&quot; == TRUE}</li>
   * <li>{@code Binary.TRUE == TRUE}</li>
   * </ul>
   *
   * <p> All other values are considered to be FALSE
   *
   * @param index the index
   * @return true or false
   * @throws java.lang.IndexOutOfBoundsException if {@code index < 0 || index > size()}
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
   * @throws java.lang.IndexOutOfBoundsException if {@code index < 0 || index > size()}
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
   * @throws java.lang.IndexOutOfBoundsException if {@code index < 0 || index > size()}
   */
  double getAsDouble(int index);

  /**
   * Returns value as {@code int} if applicable. Otherwise returns {@link IntVector#NA}
   *
   * @param index the index
   * @return an int
   * @throws java.lang.IndexOutOfBoundsException if {@code index < 0 || index > size()}
   */
  int getAsInt(int index);

  /**
   * Returns value as {@link Bit}.
   *
   * @param index the index
   * @return a {@link Bit}
   * @throws java.lang.IndexOutOfBoundsException if {@code index < 0 || index > size()}
   */
  Bit getAsBit(int index);

  /**
   * Returns value as {@link org.briljantframework.complex.Complex} or {@link ComplexVector#NA} if
   * missing.
   *
   * @param index the index
   * @return a {@link org.briljantframework.complex.Complex}
   * @throws java.lang.IndexOutOfBoundsException if {@code index < 0 || index > size()}
   */
  Complex getAsComplex(int index);

  /**
   * Returns value as {@link String}, {@code null} is used to denote missing values.
   *
   * @param index the index
   * @return a {@code String} or {@code null}
   * @throws java.lang.IndexOutOfBoundsException if {@code index < 0 || index > size()}
   */
  String getAsString(int index);

  /**
   * Returns a new vector of length {@code indexes.size()} of the elements in index
   *
   * @param indexes a collection of indexes
   * @return a new vector
   * @throws java.lang.IndexOutOfBoundsException if {@code index < 0 || index > size()}
   */
  default Vector slice(Iterable<Integer> indexes) {
    Builder builder = newBuilder();
    for (int index : indexes) {
      builder.add(this, index);
    }
    return builder.build();
  }

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
   * @throws java.lang.IndexOutOfBoundsException if {@code index < 0 || index > size()}
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

  @SuppressWarnings("unchecked")
  default <T> T[] toArray(T[] values) {
    Class<?> cls = values.getClass().getComponentType();
    int size = size();
    values = values.length >= size ? values :
             (T[]) java.lang.reflect.Array.newInstance(cls, size);
    for (int i = 0; i < size; i++) {
      values[i] = (T) get(cls, i);
    }
    return values;
  }

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

  default <T> List<T> asList(Class<T> cls) {
    return new AbstractList<T>() {
      @Override
      public T get(int index) {
        return Vector.this.get(cls, index);
      }

      @Override
      public int size() {
        return Vector.this.size();
      }
    };
  }

  default <T> Stream<T> stream(Class<T> cls) {
    return asList(cls).stream();
  }

  default <T> Stream<T> parallelStream(Class<T> cls) {
    return asList(cls).parallelStream();
  }

  default IntStream intStream() {
    return stream(Number.class).mapToInt(Number::intValue);
  }

  default DoubleStream doubleStream() {
    return stream(Number.class).mapToDouble(Number::doubleValue);
  }

  /**
   * <p>Returns this vector as an immutable {@code Matrix}. An appropriate
   * specialization of the {@link org.briljantframework.matrix.Matrix} interface should be
   * preferred. For example, a {@link org.briljantframework.vector.DoubleVector} should return a
   * {@link org.briljantframework.matrix.DoubleMatrix} implementation.
   *
   * <p>Since {@code Vector}s are immutable, mutations of the returned matrix throws {@link
   * org.briljantframework.exceptions.ImmutableModificationException}. Use {@link
   * org.briljantframework.matrix.Matrix#copy()} to get a modifiable matrix.
   *
   * <pre>
   * Vector a = new DoubleVector(1, 2, 3, 4, 5);
   * DoubleMatrix mat = a.toMatrix().asDoubleMatrix();
   * double sum = mat.reduce(0, Double::sum);
   *
   * mat.set(0, 100.0); // throws ImmutableModificationException
   * mat = mat.copy();
   * mat.set(0, 100.0); // Works fine
   * </pre>
   *
   * <p> The general implementation should return a matrix in constant time, i.e., without copying.
   * But this is not a requirement.
   *
   * @return this vector as a matrix
   * @throws org.briljantframework.exceptions.TypeConversionException if unable to convert vector
   *                                                                  to matrix
   */
  Matrix toMatrix() throws TypeConversionException;

  default DoubleMatrix asDoubleMatrix() throws TypeConversionException {
    return toMatrix().asDoubleMatrix();
  }

  default ComplexMatrix asComplexMatrix() throws TypeConversionException {
    return toMatrix().asComplexMatrix();
  }

  default LongMatrix asLongMatrix() throws TypeConversionException {
    return toMatrix().asLongMatrix();
  }

  default BitMatrix asBitMatrix() throws TypeConversionException {
    return toMatrix().asBitMatrix();
  }

  default IntMatrix asIntMatrix() throws TypeConversionException {
    return toMatrix().asIntMatrix();
  }

  /**
   * Follows the conventions from {@link Comparable#compareTo(Object)}. <p> Returns value {@code <}
   * 0 if value at index {@code a} is less than {@code b}. value {@code > 0} if value at index
   * {@code b} is larger than {@code a} and 0 if they are equal.
   *
   * @param a the index a
   * @param b the index b
   * @return comparing int
   * @throws java.lang.IndexOutOfBoundsException if {@code index < 0 || index > size()}
   * @see Comparable#compareTo(Object)
   */
  int compare(int a, int b);

  @SuppressWarnings("unchecked")
  default int compare(int a, Comparable<?> other) {
    return get(Comparable.class, a).compareTo(other);
  }

//  int compare(int a, Comparable<?> value);

  /**
   * Compare value at {@code a} in {@code this} to value at {@code b} in {@code ba}. Equivalent to
   * {@code this.get(a).compareTo(other.get(b))}, but in most circumstances with greater
   * performance.
   *
   * @param a     the index in {@code this}
   * @param other the other vector
   * @param b     the index in {@code other}
   * @return the comparison
   * @throws java.lang.IndexOutOfBoundsException if {@code index < 0 || index > size()}
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
   * @throws java.lang.IndexOutOfBoundsException if {@code index < 0 || index > size()}
   */
  default boolean equals(int a, Vector other, int b) {
    return compare(a, other, b) == 0;
  }

  default boolean equals(int a, Object other) {
    return get(Object.class, a).equals(other);
  }

  /**
   * <p> Builds a new vector. A builder can incrementally grow, but not allow gaps. For example, if
   * a builder is initialized with size {@code 8}, {@link #add(Object)} (et. al.) adds a value at
   * index {@code 8} and indexes {@code 0-7} have the value {@code NA}. If the value at index
   * {@code
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
     * Add NA at {@code index}. If {@code index > size()} the resulting vector should be padded
     * with NA:s between {@code size} and {@code index} and {@code index} set to NA.
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

//    /**
//     * Same as {@code add(value, 0)} (i.e. a more convenient way of adding 1-length vectors)
//     *
//     * @param value the value
//     * @return a modified builder
//     */
//    default Builder add(Value value) {
//      return add(value, 0);
//    }

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

//    default Builder set(int atIndex, Value from) {
//      return set(atIndex, from, 0);
//    }

    /**
     * Add {@code value} at {@code index}. Padding with NA:s between {@code atIndex} and {@code
     * size()} if {@code atIndex > size()}. <p> If value {@code value} cannot be added to this
     * vector type, a NA value is added instead.
     *
     * <p>How values are resolved depend on the implementation.
     *
     * <p>This must hold:
     *
     * <ul>
     * <li>{@code null} always result in {@code NA}</li>
     * <li>If {@link org.briljantframework.io.reslover.Resolvers#find(Class)} return a
     * non-null value the returned {@link org.briljantframework.io.reslover.Resolver#resolve(Class,
     * Object)} shall be used to produce the converted value. </li>
     * </ul>
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

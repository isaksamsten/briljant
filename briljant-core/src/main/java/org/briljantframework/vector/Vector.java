package org.briljantframework.vector;

import org.briljantframework.Check;
import org.briljantframework.array.Array;
import org.briljantframework.array.BitArray;
import org.briljantframework.array.ComplexArray;
import org.briljantframework.array.DoubleArray;
import org.briljantframework.array.IntArray;
import org.briljantframework.array.LongArray;
import org.briljantframework.complex.Complex;
import org.briljantframework.dataframe.Index;
import org.briljantframework.dataframe.SortOrder;
import org.briljantframework.exceptions.IllegalTypeException;
import org.briljantframework.function.Aggregates;
import org.briljantframework.function.Aggregator;
import org.briljantframework.io.DataEntry;
import org.briljantframework.sort.QuickSort;
import org.briljantframework.sort.Swappable;

import java.io.IOException;
import java.io.Serializable;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Collector;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * <p> A vector is an homogeneous (i.e. with values of only one (sub)type) and immutable (i.e. the
 * contents cannot change) list (with O(1) access) of values supporting missing entries (i.e. NA).
 *
 * <p> Since NA value are implemented differently depending value type, checking for NA-values are
 * done via the {@link #isNA(int)} method. For the default types, the {@link Is#NA} is available.
 *
 * <p> Implementers must ensure that:
 *
 * <ul>
 * <li>{@link #hashCode()} and {@link #equals(Object)} work as expected.</li>
 * <li>The vector cannot be changed, i.e. a vector cannot expose it's underlying
 * implementation and be mutated. This simplifies parallel algorithms.</li>
 * </ul>
 *
 * @author Isak Karlsson
 */
public interface Vector extends Serializable {

  /**
   * Construct a vector of values. The type of vector is inferred from the values.
   *
   * @param array the values
   * @param <T>   the type
   * @return a new vector, inferred from {@code T}
   */
  @SafeVarargs
  static <T> Vector of(T... array) {
//    Class<?> cls = array.getClass().getComponentType();
//    Builder builder = Vec.typeOf(cls).newBuilder();
//    for (T t : array) {
//      builder.add(t);
//    }
//    return builder.build();
    return of(Arrays.asList(array));
  }

  static <T> Vector of(Iterable<T> values) {
    Iterator<T> it = values.iterator();
    if (!it.hasNext()) {
      return singleton(null);
    }
    T t = it.next();
    Builder builder = Vec.inferTypeOf(t).newBuilder().add(t);
    while (it.hasNext()) {
      builder.add(it.next());
    }
    return builder.build();
  }

  static <T> Vector of(Supplier<T> supplier, int size) {
    if (size < 1) {
      throw new UnsupportedOperationException();
    }
    T value = supplier.get();
    Vector.Builder builder = Vec.inferTypeOf(value).newBuilder().add(value);
    for (int i = 1; i < size; i++) {
      builder.add(supplier.get());
    }
    return builder.build();
  }

  static Vector singleton(Object value, int size) {
    return new SingletonVector(value, size);
  }

  static Vector singleton(Object value) {
    return singleton(value, 1);
  }


  default <T> Vector satisfies(Class<T> cls, Vector other, BiPredicate<T, T> predicate) {
    return combine(cls, Boolean.class, other, predicate::test);
  }

  default <T> Vector satisfies(Class<? extends T> cls, Predicate<? super T> predicate) {
    return transform(cls, Boolean.class, predicate::test);
  }

  /**
   * <p> Transform each value (as a value of T) in the vector using {@code operator}, producing a
   * new
   * vector with values of type {@code O}.
   *
   * <p> Example:
   *
   * <pre>{@code
   *  Random rand = new Random(123);
   *
   *  // A vector or random numbers
   *  > Vector a = Vector.of(rand::nextGaussian, 10);
   *  [-1.438, 0.634, 0.226, 0.277, 0.184, -0.365, 1.352, 0.359, -0.205, 1.017] type: double
   *
   *  > Vector b = a.transform(Double.class, Long.class, Math::round);
   *  [-1, 1, 0, 0, 0, 0, 1, 0, 0, 1] type: long
   * }</pre>
   *
   * <p> Please note that transformations can be implemented in terms of aggregation operations.
   * For
   * example, this method can be implemented as:
   *
   * <pre>{@code
   *  a.aggregate(in, Aggregates.transform(() -> Vec.typeOf(out).newBuilder, operator));
   * }</pre>
   *
   * @param in       the input type (if the vector cannot coerce values to {@code T}, NA is used)
   * @param out      the output type
   * @param operator the operator to apply
   * @param <T>      the input type (i.e. the type of values stored in {@code this})
   * @param <O>      the output type (i.e. the type of values in the resulting vector)
   * @return a new vector of type {@code O}
   */
  default <T, O> Vector transform(Class<T> in, Class<O> out,
                                  Function<? super T, ? extends O> operator) {
    Aggregator<T, Vector, ?> transform = Aggregates.transform(
        () -> Vec.typeOf(out).newBuilder(), operator
    );
    return aggregate(in, transform);
  }

  /**
   * Transform each value (as a value of {@code T}) in the vector using {@code operator}, producing
   * a new vector with the type inferred from the first value returned by {@code operator}.
   *
   * <p> Example:
   * <pre>{@code
   * > Random rand = new Random();
   * > Vector a = Vector.of(rand::nextGaussian, 10);
   * [-1.438, 0.634, 0.226, 0.277, 0.184, -0.365, 1.352, 0.359, -0.205, 1.017] type: double
   *
   * > Vector b = a.transform(Double.class, Math::round);
   * [-1.000, 1.000, 0.000, 0.000, 0.000, 0.000, 1.000, 0.000, 0.000, 1.000] type: double
   * }</pre>
   *
   * @param cls      the input type
   * @param operator the operator
   * @param <T>      the input type
   * @return a new vector of type inferred by {@code operator}
   */
  default <T> Vector transform(Class<T> cls, UnaryOperator<T> operator) {
    return aggregate(cls, Aggregates.transform(this::newBuilder, operator));
  }

  /**
   * Filter values in this vector, treating each value as {@code cls} (or NA), using the supplied
   * predicate.
   *
   * @param cls       the class
   * @param predicate the predicate
   * @param <T>       the type
   * @return a new vector with only values for which {@code predicate} returns true
   */
  default <T> Vector filter(Class<T> cls, Predicate<T> predicate) {
    return aggregate(cls, Aggregates.filter(this::newBuilder, predicate));
  }

  /**
   * Performs a mutable aggregation of the values in this vector, similar to {@linkplain
   * Stream#collect(java.util.stream.Collector)}. A mutable aggregation performs its aggregation
   * by mutating and adding values to an aggregation container such as {@linkplain
   * org.briljantframework.stat.RunningStatistics}.
   *
   * <p> The result produced is equivalent to:
   *
   * <pre>{@code
   *  T container = aggregator.supplier();
   *  for(int i = 0; i < size(); i++) {
   *    aggreagator.accumulator().accept(container, get(in, i));
   *  }
   *  return aggragator.finisher().apply(container);
   * }</pre>
   *
   * <p> Example:
   *
   * <pre>{@code
   *  Vector randomNumber = Vector.of(rand::nextGaussian, 1000);
   *  double mean = randomNumbers.aggregate(Double.class, Aggregates.of(
   *    RunningStatistics::new, RunningStatistics::add, RunningStatistics::getMean
   *  ));
   *  mean = randomNumber.aggregate(Double.class, Aggregates.mean());
   * }</pre>
   *
   * @param in         the input type
   * @param aggregator the aggregator
   * @param <T>        the type of the input value to the mutable aggregation
   * @param <R>        the type of the mutable aggregator
   * @param <C>        the type of the return type of the aggregation
   * @return a value of type {@code R} (i.e. the result of the aggregation)
   */
  <T, R, C> R aggregate(Class<? extends T> in, Aggregator<? super T, ? extends R, C> aggregator);

  /**
   * Example:
   *
   * <pre>{@code
   *  ArrayList<Double> list = Vector.of(1,2,3).aggregate(Double.class,
   *    ArrayList::new, ArrayList::add);
   * }</pre>
   *
   * @param in       the input class
   * @param supplier the mutable container
   * @param consumer the update function
   * @param <T>      the input type
   * @param <R>      the result type
   * @return a value of type {@code R}
   */
  default <T, R> R aggregate(Class<? extends T> in, Supplier<R> supplier,
                             BiConsumer<R, ? super T> consumer) {
    return aggregate(in, Aggregator.of(supplier, consumer, Function.identity()));
  }

  default <R> R aggregate(Aggregator<? super Object, R, ?> aggregator) {
    return aggregate(getType().getDataClass(), aggregator);
  }

  default <T, R, C> R collect(Class<? extends T> cls,
                              Collector<? super T, C, ? extends R> collector) {
    Aggregator<? super T, ? extends R, C> aggregator = Aggregator.of(
        collector.supplier(),
        collector.accumulator(),
        collector.finisher());
    return aggregate(cls, aggregator);
  }

  <T, R> Vector combine(Class<? extends T> in, Class<? extends R> out, Vector other,
                        BiFunction<? super T, ? super T, ? extends R> combiner);

  <T> Vector combine(Class<T> cls, Vector other, BiFunction<T, T, ? extends T> combiner);

  default Vector add(Vector other) {
    return combine(Number.class, other, Combine.add());
  }

  default Vector add(Number other) {
    return combine(Number.class, singleton(other, size()), Combine.add());
  }

  default Vector mul(Vector other) {
    return combine(Number.class, other, Combine.mul());
  }

  default Vector mul(Number other) {
    return combine(Number.class, singleton(other, size()), Combine.mul());
  }

  default Vector div(Vector other) {
    return combine(Number.class, other, Combine.div());
  }

  default Vector div(Number other) {
    return combine(Number.class, singleton(other, size()), Combine.div());
  }

  default Vector rdiv(Number other) {
    return singleton(other, size()).combine(Number.class, this, Combine.div());
  }

  default Vector sub(Vector other) {
    return combine(Number.class, other, Combine.sub());
  }

  default Vector sub(Number other) {
    return combine(Number.class, singleton(other, size()), Combine.sub());
  }

  default Vector rsub(Number other) {
    return singleton(other, size()).combine(Number.class, this, Combine.sub());
  }

  default Vector sort(SortOrder order) {
    int o = order == SortOrder.DESC ? -1 : 1;
    Vector.Builder builder = newCopyBuilder();
    Vector tmp = builder.getTemporaryVector();
    QuickSort.quickSort(0, tmp.size(), (a, b) -> o * tmp.compare(a, b), builder);
    return builder.build();
  }

  default <T> Vector sort(Class<T> cls, Comparator<T> cmp) {
    Vector.Builder builder = newCopyBuilder();
    Vector tmp = builder.getTemporaryVector();
    QuickSort.quickSort(0, tmp.size(), (a, b) -> cmp.compare(tmp.get(cls, a), tmp.get(cls, b)),
                        builder);
    return builder.build();
  }


  Vector head(int n);

  Vector tail(int n);

  Index getIndex();

  void setIndex(Index index);

  default <T> T get(Class<T> cls, Object key) {
    return get(cls, getIndex().index(key));
  }

  default int getAsInt(Object key) {
    return getAsInt(getIndex().index(key));
  }

  default double getAsDouble(Object key) {
    return getAsDouble(getIndex().index(key));
  }

  default Complex getAsComplex(Object key) {
    return getAsComplex(getIndex().index(key));
  }

  default Bit getAsBit(Object key) {
    return getAsBit(getIndex().index(key));
  }

  default String toString(Object key) {
    return toString(getIndex().index(key));
  }

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

  default <T> T get(Class<T> cls, int index, Supplier<T> defaultValue) {
    T v = get(cls, index);
    return Is.NA(v) ? defaultValue.get() : v;
  }

//  default <T> T get(Class<T> cls, int index, T defaultValue) {
//    T v = get(cls, index);
//    return Is.NA(v) ? defaultValue : v;
//  }

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
   * Returns value as {@code int} if applicable. Otherwise returns {@link
   * org.briljantframework.vector.IntVector#NA}
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
   * Returns value as {@link org.briljantframework.complex.Complex} or {@link
   * org.briljantframework.vector.ComplexVector#NA} if
   * missing.
   *
   * @param index the index
   * @return a {@link org.briljantframework.complex.Complex}
   * @throws java.lang.IndexOutOfBoundsException if {@code index < 0 || index > size()}
   */
  Complex getAsComplex(int index);

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

  default Vector slice(Vector vector) {
    return slice(vector.asList(Bit.class));
  }

  default Vector slice(Collection<Bit> bits) {
    Check.size(this.size(), bits.size());
    Builder builder = newBuilder();
    Iterator<Bit> it = bits.iterator();
    for (int i = 0; i < size(); i++) {
      Bit b = it.next();
      if (b == Bit.TRUE) {
        builder.add(this, i);
      }
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
   * <p>Copies this vector to a {@code Matrix}. An appropriate
   * specialization of the {@link org.briljantframework.array.BaseArray} interface should be
   * preferred. For example, a {@link org.briljantframework.vector.DoubleVector} should return a
   * {@link org.briljantframework.array.DoubleArray} implementation.
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
   * @throws org.briljantframework.exceptions.IllegalTypeException if unable to convert vector
   *                                                               to matrix
   */
  <U> Array<U> asArray(Class<U> cls) throws IllegalTypeException;

  default DoubleArray asDoubleArray() throws IllegalTypeException {
    return asArray(Double.class).asDouble();
  }

  default ComplexArray asComplexArray() throws IllegalTypeException {
    return asArray(Complex.class).asComplex();
  }

  default LongArray asLongArray() throws IllegalTypeException {
    return asArray(Long.class).asLong();
  }

  default BitArray asBitArray() throws IllegalTypeException {
    return asArray(Boolean.class).asBit();
  }

  default IntArray asIntArray() throws IllegalTypeException {
    return asArray(Integer.class).asInt();
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
     * <li>If {@link org.briljantframework.io.resolver.Resolvers#find(Class)} return a
     * non-null value the returned {@link org.briljantframework.io.resolver.Resolver#resolve(Class,
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

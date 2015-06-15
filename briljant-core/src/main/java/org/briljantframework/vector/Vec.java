package org.briljantframework.vector;

import com.google.common.base.Function;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multiset;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import com.google.common.collect.UnmodifiableIterator;
import com.google.common.primitives.Ints;

import org.briljantframework.Check;
import org.briljantframework.complex.Complex;
import org.briljantframework.distribution.Distribution;
import org.briljantframework.io.DataEntry;
import org.briljantframework.sort.IndexComparator;
import org.briljantframework.sort.QuickSort;
import org.briljantframework.stat.DescriptiveStatistics;
import org.briljantframework.stat.RunningStatistics;

import java.io.IOException;
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collector;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Isak Karlsson
 */
public final class Vec {

  public static final VectorType STRING = new GenericVectorType(String.class);
  public static final VectorType BIT = BitVector.TYPE;
  public static final VectorType INT = IntVector.TYPE;
  public static final VectorType COMPLEX = ComplexVector.TYPE;
  public static final VectorType DOUBLE = DoubleVector.TYPE;
  public static final VectorType VARIABLE = new GenericVectorType(Object.class);
  public static final Map<Class<?>, VectorType> CLASS_TO_VECTOR_TYPE;
  public static final Set<VectorType> NUMERIC = Sets.newHashSet();
  public static final Set<VectorType> CATEGORIC = Sets.newHashSet();

  static {
    NUMERIC.add(DOUBLE);
    NUMERIC.add(INT);
    NUMERIC.add(COMPLEX);

    CATEGORIC.add(STRING);
    CATEGORIC.add(BIT);

    CLASS_TO_VECTOR_TYPE = ImmutableMap.<Class<?>, VectorType>builder()
        .put(Integer.class, INT)
        .put(Integer.TYPE, INT)
        .put(Double.class, DOUBLE)
        .put(Double.TYPE, DOUBLE)
        .put(String.class, STRING)
        .put(Boolean.class, BIT)
        .put(Bit.class, BIT)
        .put(Complex.class, COMPLEX)
        .put(Object.class, VARIABLE)
        .build();
  }

  private Vec() {
  }


  public static <T, V extends Vector.Builder> Collector<T, ?, Vector> collector(
      Supplier<V> supplier) {
    return Collector.of(
        supplier,
        Vector.Builder::add,
        (left, right) -> {
          left.addAll(right.getTemporaryVector());
          return left;
        }, Vector.Builder::build);
  }

  public static VectorType typeOf(Class<?> cls) {
    VectorType type = CLASS_TO_VECTOR_TYPE.get(cls);
    if (type == null) {
      return new GenericVectorType(cls);
    }
    return type;
  }

  public static VectorType inferTypeOf(Object object) {
    if (object != null) {
      return typeOf(object.getClass());
    } else {
      return VARIABLE;
    }
  }

  /**
   * Creates a new {@code Vector.Builder} which is able to infer the correct {@code Vector} to
   * return based on the first value added value.
   *
   * <p> For example, {@code Vec.inferringBuilder().add(1.0).build()} returns a {@code double}
   * vector. If unable to infer the type, e.g., when the first added value is {@code NA}, an {@code
   * object} vector is returned.
   */
  public static Vector.Builder inferringBuilder() {
    return new InferringBuilder();
  }

  public static DoubleVector rand(int size, Distribution source) {
    DoubleVector.Builder v = new DoubleVector.Builder(0, size);
    for (int i = 0; i < size; i++) {
      v.set(i, source.sample());
    }
    return v.build();
  }

  /**
   * Finds the index, in {@code vector}, of the value at {@code index} in {@code values}.  Hence,
   * given {@code Vector a}, {@code Vector b} and the index {@code i}, {@code find(a, b, i)} should
   * be preferred over {@code find(a, b.get(i))}.
   *
   * @param haystack     the vector to search
   * @param needleSource the source of the needle
   * @param needle       the needle in the source
   * @return the (first) index of {@code needleSource.get(needle)} in {@code haystack} or {@code -1}
   */
  public static int find(Vector haystack, Vector needleSource, int needle) {
    for (int i = 0; i < haystack.size(); i++) {
      if (haystack.compare(i, needleSource, needle) == 0) {
        return i;
      }
    }
    return -1;
  }

  /**
   * Finds the index of {@code needle} in {@code haystack} or return {@code -1} if value cannot be
   * found.
   *
   * @param haystack the haystack
   * @param needle   the needle
   * @param <T>      the type of object to be searched for
   * @return the index of {@code needle} or {@code -1}
   */
  public static <T> int find(Vector haystack, T needle) {
    Class<?> cls = needle.getClass();
    for (int i = 0; i < haystack.size(); i++) {
      if (haystack.get(cls, i).equals(needle)) {
        return i;
      }
    }
    return -1;
  }

  /**
   * Finds the index of the first value for which {@code predicate} returns true.
   *
   * @param vector    the vector
   * @param predicate the predicate
   * @return the index or {@code -1} if no value matched the predicate {@code true}
   */
  public static <T> int find(Class<T> cls, Vector vector, Predicate<T> predicate) {
    for (int i = 0; i < vector.size(); i++) {
      if (predicate.test(vector.get(cls, i))) {
        return i;
      }
    }
    return -1;
  }

  /**
   * @param in the vector
   * @return a new vector sorted in ascending order
   */
  public static Vector sortAsc(Vector in) {
    Vector.Builder builder = in.newCopyBuilder();
    QuickSort.quickSort(0, in.size(), builder::compare, builder);
    return builder.build();
  }

  /**
   * @param in the vector
   * @return a new vector sorted in ascending order
   */
  public static Vector sortDesc(Vector in) {
    Vector.Builder builder = in.newCopyBuilder();
    QuickSort.quickSort(0, in.size(), (a, b) -> builder.compare(b, a), builder);
    return builder.build();
  }

  public static Vector sort(Vector in, IndexComparator<? super Vector> cmp) {
    Vector.Builder builder = in.newCopyBuilder();
    Vector tmp = builder.getTemporaryVector();
    QuickSort.quickSort(0, in.size(), (a, b) -> cmp.compare(tmp, a, b), builder);
    return builder.build();
  }

  /**
   * Sorts the vector according to {@code comparator} treating the values as {@code cls}
   *
   * @param cls        the value to sort
   * @param in         the vector
   * @param comparator the comparator
   * @param <T>        the typ
   * @return a new vector; sorted according to comparator
   */
  public static <T> Vector sort(Class<T> cls, Vector in, Comparator<T> comparator) {
    Vector.Builder builder = in.newCopyBuilder();
    Vector tmp = builder.getTemporaryVector();
    QuickSort.quickSort(0, in.size(),
                        (a, b) -> comparator.compare(tmp.get(cls, a), tmp.get(cls, b)),
                        builder);
    return builder.build();
  }

  /**
   * <p> Create a vector of length {@code num} with evenly spaced values between {@code start} and
   * {@code end}. </p>
   *
   * <p> Returns a vector of {@link DoubleVector#TYPE} </p>
   *
   * @param start the start value
   * @param stop  the end value
   * @param num   the number of steps (i.e. intermediate values)
   * @return a vector
   */
  public static Vector linspace(double start, double stop, int num) {
    DoubleVector.Builder builder = new DoubleVector.Builder(0, num);
    double step = (stop - start) / (num - 1);
    double value = start;
    for (int index = 0; index < num; index++) {
      builder.set(index, value);
      value += step;
    }

    return builder.build();
  }

  /**
   * Returns a vector of length {@code 50}. With evenly spaced values in the range {@code start} to
   * {@code end}.
   *
   * @param start the start value
   * @param stop  the end value
   * @return a vector
   */
  public static Vector linspace(double start, double stop) {
    return linspace(start, stop, 50);
  }

  /**
   * <p> Split {@code vector} into {@code chunks}. Handles the case when {@code vector.size()} is
   * not evenly dividable by chunks by making some chunks larger. </p>
   *
   * <p> This implementation is lazy, i.e. chunking is done 'on-the-fly'. To get a list, {@code new
   * ArrayList<>(Vectors.split(vec, 10))} </p>
   *
   * <p> Ensures that {@code vector.getType()} is preserved. </p>
   *
   * @param vector the vector
   * @param chunks the number of chunks
   * @return a collection of {@code chunk} chunks
   */
  public static Collection<Vector> split(Vector vector, int chunks) {
    checkArgument(vector.size() >= chunks, "size must be shorter than chunks");
    if (vector.size() == chunks) {
      return Collections.singleton(vector);
    }
    int bin = vector.size() / chunks;
    int remainder = vector.size() % chunks;

    return new AbstractCollection<Vector>() {
      @Override
      public Iterator<Vector> iterator() {
        return new UnmodifiableIterator<Vector>() {
          private int current = 0;
          private int remainders = 0;

          @Override
          public boolean hasNext() {
            return current < vector.size();
          }

          @Override
          public Vector next() {
            int binSize = bin;
            if (remainders < remainder) {
              remainders++;
              binSize += 1;
            }
            Vector.Builder builder = vector.newBuilder();
            for (int i = 0; i < binSize; i++) {
              builder.add(vector, current++);
            }
            return builder.build();
          }
        };
      }

      @Override
      public int size() {
        return chunks;
      }
    };
  }

  /**
   * Computes the {@code Double} descriptive statistics of {@code vector}
   *
   * @param vector a vector (with {@code type = double})
   * @return the descriptive statistics
   */
  public static DescriptiveStatistics statistics(Vector vector) {
    RunningStatistics r = new RunningStatistics();
    for (int i = 0; i < vector.size(); i++) {
      if (!vector.isNA(i)) {
        r.add(vector.getAsDouble(i));
      }
    }
    return r;
  }

  /**
   * <p>Computes the population standard deviation of {@code vector}.
   *
   * <p>A vector of all {@code NA} returns {@code NA}
   *
   * @param vector the vector
   * @return the standard deviation
   */
  public static double std(Vector vector) {
    return statistics(vector).getStandardDeviation();
  }

  /**
   * <p>Computes the population standard deviation of {@code vector} using an already computed
   * {@code mean}.
   *
   * <p>A vector of all {@code NA} returns {@code NA}
   *
   * @param vector the vector
   * @param mean   the mean
   * @return the standard deviation
   */
  public static double std(Vector vector, double mean) {
    double var = var(vector, mean);
    return Is.NA(var) ? DoubleVector.NA : Math.sqrt(var);
  }

  /**
   * <p>Computes the sample mean of {@code vector}.
   *
   * <p>A vector of all {@code NA} returns {@code NA}
   *
   * @param vector the vector
   * @return the mean; or NA
   */
  public static double mean(Vector vector) {
    double mean = 0;
    int nonNA = 0;
    for (int i = 0; i < vector.size(); i++) {
      if (!vector.isNA(i)) {
        mean += vector.getAsDouble(i);
        nonNA += 1;
      }
    }

    return nonNA == 0 ? Na.of(Double.class) : mean / (double) nonNA;
  }

  /**
   * <p>Computes the population variance of {@code vector} using an already computed
   * {@code mean}.
   *
   * <p>A vector of all {@code NA} returns {@code NA}
   *
   * @param vector the vector
   * @param mean   the mean
   * @return the variance; or NA
   */
  public static double var(Vector vector, double mean) {
    double var = 0;
    int nonNA = 0;
    for (int i = 0; i < vector.size(); i++) {
      if (!vector.isNA(i)) {
        double residual = vector.getAsDouble(i) - mean;
        var += residual * residual;
        nonNA += 1;
      }
    }
    return nonNA == 0 ? Na.of(Double.class) : var / (double) nonNA;
  }

  /**
   * <p>Computes the population variance of {@code vector}.
   *
   * <p>A vector of all {@code NA} returns {@code NA}
   *
   * @param vector the vector
   * @return the variance
   */
  public static double var(Vector vector) {
    return var(vector, mean(vector));
  }

  /**
   * Computes the sum of values in {@code vector}. Ignores {@code NA} values.
   *
   * @param vector the vector
   * @return the sum
   */
  public static double sum(Vector vector) {
    double sum = 0;
    int nonNas = 0;
    for (int i = 0; i < vector.size(); i++) {
      double d = vector.getAsDouble(i);
      boolean nonNa = !Is.NA(d);
      if (nonNa) {
        sum += d;
        nonNas++;
      }
    }
    return nonNas > 0 ? sum : Na.of(Double.class);
  }

  public static <T extends Number> double sum(Class<T> cls, Vector vector) {
    return vector.asList(cls).stream()
        .filter(x -> !Is.NA(x))
        .mapToDouble(Number::doubleValue)
        .sum();
  }

  public static <T extends Comparable<T>> T min(Class<T> cls, Vector vector) {
    return vector.asList(cls).stream().min(Comparable::compareTo).get();
  }

  public static <T extends Comparable<T>> T max(Class<T> cls, Vector vector) {
    return vector.asList(cls).stream().max(Comparable::compareTo).get();
  }

  /**
   * Finds the minimum value in {@code v}. Ignores {@code NA} values.
   *
   * @param v the vector
   * @return the minimum value or {@code NA} if all values are {@code NA}
   */
  public static double min(Vector v) {
    return v.doubleStream().filter(Is::NA).min().orElse(DoubleVector.NA);
  }

  /**
   * Finds the maximum value in {@code v}. Ignores {@code NA} values.
   *
   * @param v the vector
   * @return the maximum value or {@code NA} if all values are {@code NA}
   */
  public static double max(Vector v) {
    return v.doubleStream().filter(Is::NA).max().orElse(DoubleVector.NA);
  }

  /**
   * Return the most frequently occurring item in {@code v}
   *
   * @param v the vector
   * @return the most frequent item; or
   */
  public static Object mode(Vector v) {
    Multiset<Object> values = HashMultiset.create();
    v.stream(Object.class).forEach(values::add);
    return Ordering.natural().onResultOf(new Function<Multiset.Entry<Object>, Integer>() {
      @Override
      public Integer apply(Multiset.Entry<Object> input) {
        return input.getCount();
      }
    }).max(values.entrySet()).getElement();
  }

  /**
   * <p>Returns a vector consisting of the unique values in {@code vectors}
   *
   * <p>For example, given {@code a, b} and {@code c}
   * <pre>{@code
   * Vector a = new IntVector(1,2,3,4);
   * Vector b = new IntVector(2,3,4,5);
   * Vector c = new IntVector(3,4,5,6);
   *
   * Vector d = Vectors.unique(a, b, c);
   * // d == [1,2,3,4,5,6];
   * }</pre>
   */
  public static Vector unique(Vector... vectors) {
    vectors = checkNotNull(vectors);
    checkArgument(vectors.length > 0);
    Vector.Builder builder = vectors[0].newBuilder();
    Set<Object> taken = new HashSet<>();
    for (Vector vector : vectors) {
      for (int i = 0; i < vector.size(); i++) {
        Object value = vector.get(Object.class, i);
        if (!taken.contains(value)) {
          taken.add(value);
          builder.add(vector, i);
        }
      }
    }
    return builder.build();
  }

  /**
   * <p> Counts the number of occurrences for each value (of type {@code T}) in {@code vector}
   *
   * <p> Since {@link Vector#get(Class, int)} returns {@code NA} if value is not an instance of
   * {@code T}, the resulting {@code Map} might contain a {@code null} key
   *
   * @param cls    the class
   * @param vector the vector
   * @param <T>    the type
   * @return a map of values to counts
   */
  public static <T> Map<T, Integer> count(Class<T> cls, Vector vector) {
    Map<T, Integer> count = new HashMap<>();
    for (T value : vector.asList(cls)) {
      count.compute(value, (x, v) -> v == null ? 1 : v + 1);
    }
    return Collections.unmodifiableMap(count);
  }


  /**
   * <p> Counts the number of occurrences for each value (wrapping the in a {@link Object}) in
   * {@code
   * vector}
   *
   * @param vector the vector
   * @return a map of values to counts
   */
  public static Map<Object, Integer> count(Vector vector) {
    Map<Object, Integer> freq = new HashMap<>();
    for (Object value : vector.asList(Object.class)) {
      freq.compute(value, (x, i) -> i == null ? 1 : i + 1);
    }
    return Collections.unmodifiableMap(freq);
  }

  /**
   * @param vector the vector
   * @return the indexes of {@code vector} sorted in increasing order by value
   */
  public static int[] indexSort(Vector vector) {
    return indexSort(vector, (o1, o2) -> Double.compare(vector.getAsDouble(o1),
                                                        vector.getAsDouble(o2)));
  }

  /**
   * @param vector     the vector
   * @param comparator the comparator
   * @return the indexes of {@code vector} sorted according to {@code comparator} by value
   */
  public static int[] indexSort(Vector vector, Comparator<Integer> comparator) {
    int[] indicies = new int[vector.size()];
    for (int i = 0; i < indicies.length; i++) {
      indicies[i] = i;
    }
    List<Integer> tempList = Ints.asList(indicies);
    Collections.sort(tempList, comparator);
    return indicies;
  }

  /**
   * Inner product, i.e. the dot product x * y. Handles {@code NA} values.
   *
   * @param x a vector
   * @param y a vector
   * @return the dot product
   */
  public static double dot(Vector x, Vector y) {
    return dot(x, 1, y, 1);
  }

  /**
   * Take the inner product of two vectors (m x 1) and (1 x m) scaling them by alpha and beta
   * respectively
   *
   * @param x     a row vector
   * @param alpha scaling factor for a
   * @param y     a column vector
   * @param beta  scaling factor for y
   * @return the inner product
   */
  public static double dot(Vector x, double alpha, Vector y, double beta) {
    Check.size(x, y);
    int size = y.size();
    double dot = 0;
    for (int i = 0; i < size; i++) {
      if (!x.isNA(i) && !y.isNA(i)) {
        dot += (alpha * x.getAsDouble(i)) * (beta * y.getAsDouble(i));
      }
    }
    return dot;
  }

  /**
   * Compute the sigmoid between a and b, i.e. 1/(1+e^(a'(-b)))
   *
   * @param a a vector
   * @param b a vector
   * @return the sigmoid
   */
  public static double sigmoid(Vector a, Vector b) {
    return 1.0 / (1 + Math.exp(dot(a, 1, b, -1)));
  }

  /**
   * Builder that infers the type of vector to build based on the first added value.
   */
  private static class InferringBuilder implements Vector.Builder {

    private Vector.Builder builder;

    @Override
    public Vector.Builder setNA(int index) {
      if (builder == null) {
        builder = getObjectBuilder();
      }
      builder.setNA(index);
      return this;
    }

    protected GenericVector.Builder getObjectBuilder() {
      return new GenericVector.Builder(Object.class);
    }

    @Override
    public Vector.Builder addNA() {
      if (builder == null) {
        builder = getObjectBuilder();
      }
      builder.addNA();
      return this;
    }

    @Override
    public Vector.Builder add(Vector from, int fromIndex) {
      return add(from.get(Object.class, fromIndex));
    }

    @Override
    public Vector.Builder set(int atIndex, Vector from, int fromIndex) {
      return set(atIndex, from.get(Object.class, fromIndex));
    }

    @Override
    public Vector.Builder set(int index, Object value) {
      if (builder == null) {
        builder = inferTypeOf(value).newBuilder();
      }
      builder.set(index, value);
      return this;
    }

    @Override
    public Vector.Builder add(Object value) {
      return set(size(), value);
    }

    @Override
    public Vector.Builder addAll(Vector from) {
      if (from.size() > 1) {
        Object value = from.get(Object.class, 0);
        if (builder == null) {
          builder = inferTypeOf(value).newBuilder();
        }
        builder.addAll(from);
      }
      return this;
    }

    @Override
    public Vector.Builder remove(int index) {
      throw indexOutOfBounds(index);
    }

    protected IndexOutOfBoundsException indexOutOfBounds(int index) {
      return new IndexOutOfBoundsException(String.format("%d out of bounds [size = 0]", index));
    }

    @Override
    public int compare(int a, int b) {
      throw indexOutOfBounds(a);
    }

    @Override
    public void swap(int a, int b) {
      throw indexOutOfBounds(a);
    }

    @Override
    public Vector.Builder read(DataEntry entry) throws IOException {
      return getObjectBuilder().read(entry);
    }

    @Override
    public Vector.Builder read(int index, DataEntry entry) throws IOException {
      if (builder == null) {
        builder = getObjectBuilder();
      }
      builder.read(index, entry);
      return this;
    }

    @Override
    public int size() {
      return builder != null ? builder.size() : 0;
    }

    @Override
    public Vector getTemporaryVector() {
      return builder != null ? builder.getTemporaryVector() : Vector.singleton(null);
    }

    @Override
    public Vector build() {
      return builder != null ? builder.build() : Vector.singleton(null);
    }
  }
}

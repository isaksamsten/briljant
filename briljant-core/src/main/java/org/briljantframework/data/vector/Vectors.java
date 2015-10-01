/*
 * The MIT License (MIT)
 * 
 * Copyright (c) 2015 Isak Karlsson
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.briljantframework.data.vector;

import java.io.IOException;
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collector;

import org.apache.commons.math3.distribution.RealDistribution;
import org.apache.commons.math3.stat.descriptive.StatisticalSummary;
import org.briljantframework.Check;
import org.briljantframework.data.Is;
import org.briljantframework.data.Na;
import org.briljantframework.data.Transferable;
import org.briljantframework.data.index.VectorLocationSetter;
import org.briljantframework.data.reader.DataEntry;
import org.briljantframework.sort.QuickSort;
import org.briljantframework.statistics.FastStatistics;

/**
 * @author Isak Karlsson
 */
public final class Vectors {

  private Vectors() {}

  public static <T, V extends Vector.Builder> Collector<T, ?, Vector> collector(Supplier<V> supplier) {
    return Collector.of(supplier, Vector.Builder::add, (left, right) -> {
      left.addAll(right.getTemporaryVector());
      return left;
    }, Vector.Builder::build);
  }

  public static DoubleVector rand(int size, RealDistribution source) {
    DoubleVector.Builder v = new DoubleVector.Builder(0, size);
    for (int i = 0; i < size; i++) {
      v.setAt(i, source.sample());
    }
    return v.build();
  }

  /**
   * Finds the index, in {@code vector}, of the value at {@code index} in {@code values}. Hence,
   * given {@code Vector a}, {@code Vector b} and the index {@code i}, {@code find(a, b, i)} should
   * be preferred over {@code find(a, b.get(i))}.
   *
   * @param haystack the vector to search
   * @param needleSource the source of the needle
   * @param needle the needle in the source
   * @return the (first) index of {@code needleSource.get(needle)} in {@code haystack} or {@code -1}
   */
  public static int find(Vector haystack, Vector needleSource, int needle) {
    for (int i = 0; i < haystack.size(); i++) {
      if (haystack.loc().compare(i, needleSource, needle) == 0) {
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
   * @param needle the needle
   * @param <T> the type of object to be searched for
   * @return the index of {@code needle} or {@code -1}
   */
  public static <T> int find(Vector haystack, T needle) {
    Class<?> cls = needle.getClass();
    for (int i = 0; i < haystack.size(); i++) {
      Object v = haystack.loc().get(cls, i);
      if (!Is.NA(v) && v.equals(needle)) {
        return i;
      }
    }
    return -1;
  }

  /**
   * Finds the index of the first value for which {@code predicate} returns true.
   *
   * @param vector the vector
   * @param predicate the predicate
   * @return the index or {@code -1} if no value matched the predicate {@code true}
   */
  public static <T> int find(Class<T> cls, Vector vector, Predicate<T> predicate) {
    for (int i = 0; i < vector.size(); i++) {
      if (predicate.test(vector.loc().get(cls, i))) {
        return i;
      }
    }
    return -1;
  }

  /**
   * <p>
   * Create a vector of length {@code num} with evenly spaced values between {@code start} and
   * {@code end}.
   * </p>
   *
   * <p>
   * Returns a vector of {@link VectorType#DOUBLE}
   * </p>
   *
   * @param start the start value
   * @param stop the end value
   * @param num the number of steps (i.e. intermediate values)
   * @return a vector
   */
  public static Vector linspace(double start, double stop, int num) {
    DoubleVector.Builder builder = new DoubleVector.Builder(0, num);
    double step = (stop - start) / (num - 1);
    double value = start;
    for (int index = 0; index < num; index++) {
      builder.setAt(index, value);
      value += step;
    }

    return builder.build();
  }

  /**
   * Returns a vector of length {@code 50}. With evenly spaced values in the range {@code start} to
   * {@code end}.
   *
   * @param start the start value
   * @param stop the end value
   * @return a vector
   */
  public static Vector linspace(double start, double stop) {
    return linspace(start, stop, 50);
  }

  /**
   * <p>
   * Split {@code vector} into {@code chunks}. Handles the case when {@code vector.size()} is not
   * evenly dividable by chunks by making some chunks larger.
   * </p>
   *
   * <p>
   * This implementation is lazy, i.e. chunking is done 'on-the-fly'. To get a list, {@code new
   * ArrayList<>(Vectors.split(vec, 10))}
   * </p>
   *
   * <p>
   * Ensures that {@code vector.getType()} is preserved.
   * </p>
   *
   * @param vector the vector
   * @param chunks the number of chunks
   * @return a collection of {@code chunk} chunks
   */
  public static Collection<Vector> split(Vector vector, int chunks) {
    Check.argument(vector.size() >= chunks, "size must be shorter than chunks");
    if (vector.size() == chunks) {
      return Collections.singleton(vector);
    }
    int bin = vector.size() / chunks;
    int remainder = vector.size() % chunks;

    return new AbstractCollection<Vector>() {
      @Override
      public Iterator<Vector> iterator() {
        return new Iterator<Vector>() {
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
   * Computes descriptive statistics of {@code vector}
   *
   * @param vector a vector
   * @return the descriptive statistics
   */
  public static StatisticalSummary statisticalSummary(Vector vector) {
    FastStatistics r = new FastStatistics();
    for (int i = 0; i < vector.size(); i++) {
      double v = vector.loc().getAsDouble(i);
      if (!Is.NA(v)) {
        r.addValue(v);
      }
    }
    return r.getSummary();
  }

  /**
   * <p>
   * Computes the population standard deviation of {@code vector}.
   *
   * <p>
   * A vector of all {@code NA} returns {@code NA}
   *
   * @param vector the vector
   * @return the standard deviation
   */
  public static double std(Vector vector) {
    return statisticalSummary(vector).getStandardDeviation();
  }

  /**
   * <p>
   * Computes the population standard deviation of {@code vector} using an already computed
   * {@code mean}.
   *
   * <p>
   * A vector of all {@code NA} returns {@code NA}
   *
   * @param vector the vector
   * @param mean the mean
   * @return the standard deviation
   */
  public static double std(Vector vector, double mean) {
    double var = var(vector, mean);
    return Is.NA(var) ? Na.DOUBLE : Math.sqrt(var);
  }

  /**
   * <p>
   * Computes the sample mean of {@code vector}.
   *
   * <p>
   * A vector of all {@code NA} returns {@code NA}
   *
   * @param vector the vector
   * @return the mean; or NA
   */
  public static double mean(Vector vector) {
    double mean = 0;
    int nonNA = 0;
    for (int i = 0; i < vector.size(); i++) {
      if (!vector.loc().isNA(i)) {
        mean += vector.loc().getAsDouble(i);
        nonNA += 1;
      }
    }

    return nonNA == 0 ? Na.of(Double.class) : mean / (double) nonNA;
  }

  /**
   * <p>
   * Computes the population variance of {@code vector} using an already computed {@code mean}.
   *
   * <p>
   * A vector of all {@code NA} returns {@code NA}
   *
   * @param vector the vector
   * @param mean the mean
   * @return the variance; or NA
   */
  public static double var(Vector vector, double mean) {
    double var = 0;
    int nonNA = 0;
    for (int i = 0; i < vector.size(); i++) {
      if (!vector.loc().isNA(i)) {
        double residual = vector.loc().getAsDouble(i) - mean;
        var += residual * residual;
        nonNA += 1;
      }
    }
    return nonNA == 0 ? Na.of(Double.class) : var / (double) nonNA;
  }

  /**
   * <p>
   * Computes the population variance of {@code vector}.
   *
   * <p>
   * A vector of all {@code NA} returns {@code NA}
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
      double d = vector.loc().getAsDouble(i);
      boolean nonNa = !Is.NA(d);
      if (nonNa) {
        sum += d;
        nonNas++;
      }
    }
    return nonNas > 0 ? sum : Na.of(Double.class);
  }

  public static <T extends Number> double sum(Class<T> cls, Vector vector) {
    return vector.asList(cls).stream().filter(x -> !Is.NA(x)).mapToDouble(Number::doubleValue)
        .sum();
  }

  public static <T extends Comparable<T>> Optional<T> min(Class<T> cls, Vector vector) {
    return vector.asList(cls).stream().min(Comparable::compareTo);
  }

  public static <T extends Comparable<T>> Optional<T> max(Class<T> cls, Vector vector) {
    return vector.asList(cls).stream().max(Comparable::compareTo);
  }

  /**
   * Finds the minimum value in {@code v}. Ignores {@code NA} values.
   *
   * @param v the vector
   * @return the minimum value or {@code NA} if all values are {@code NA}
   */
  public static double min(Vector v) {
    return v.doubleStream().filter(Is::NA).min().orElse(Na.DOUBLE);
  }

  /**
   * Finds the maximum value in {@code v}. Ignores {@code NA} values.
   *
   * @param v the vector
   * @return the maximum value or {@code NA} if all values are {@code NA}
   */
  public static double max(Vector v) {
    return v.doubleStream().filter(Is::NA).max().orElse(Na.DOUBLE);
  }


  /**
   * <p>
   * Returns a vector consisting of the unique values in {@code vectors}
   *
   * <p>
   * For example, given {@code a, b} and {@code c}
   * 
   * <pre>
   * {
   *   &#064;code
   *   Vector a = new IntVector(1, 2, 3, 4);
   *   Vector b = new IntVector(2, 3, 4, 5);
   *   Vector c = new IntVector(3, 4, 5, 6);
   * 
   *   Vector d = Vectors.unique(a, b, c);
   *   // d == [1,2,3,4,5,6];
   * }
   * </pre>
   */
  public static Vector unique(Vector... vectors) {
    vectors = Objects.requireNonNull(vectors);
    Check.argument(vectors.length > 0);
    Vector.Builder builder = vectors[0].newBuilder();
    Set<Object> taken = new HashSet<>();
    for (Vector vector : vectors) {
      for (int i = 0; i < vector.size(); i++) {
        Object value = vector.loc().get(Object.class, i);
        if (!taken.contains(value)) {
          taken.add(value);
          builder.add(vector, i);
        }
      }
    }
    return builder.build();
  }

  /**
   * <p>
   * Counts the number of occurrences for each value (of type {@code T}) in {@code vector}
   *
   * <p>
   * Since {@link Vector#get(Class, Object)} returns {@code NA} if value is not an instance of
   * {@code T}, the resulting {@code Map} might contain a {@code null} key
   *
   * @param cls the class
   * @param vector the vector
   * @param <T> the type
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
   * <p>
   * Counts the number of occurrences for each value (wrapping the in a {@link Object}) in
   * {@code vector}
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
    return indexSort(vector,
        (o1, o2) -> Double.compare(vector.loc().getAsDouble(o1), vector.loc().getAsDouble(o2)));
  }

  public interface IntCmp {

    int compare(int a, int b);
  }

  /**
   * @param vector the vector
   * @param comparator the comparator
   * @return the indexes of {@code vector} sorted according to {@code comparator} by value
   */
  public static int[] indexSort(Vector vector, IntCmp comparator) {
    int[] indicies = new int[vector.size()];
    for (int i = 0; i < indicies.length; i++) {
      indicies[i] = i;
    }
    QuickSort.quickSort(0, indicies.length, (a, b) -> comparator.compare(indicies[a], indicies[b]),
        (a, b) -> {
          int tmp = indicies[a];
          indicies[a] = indicies[b];
          indicies[b] = tmp;
        });
    return indicies;
  }

  /**
   * Inner product, i.e. the dot product x * y. Handles {@code NA} values by ignoring them.
   *
   * @param x a vector
   * @param y a vector
   * @return the dot product
   */
  public static double dot(Vector x, Vector y) {
    Check.size(x.size(), y.size());
    final int size = y.size();
    double dot = 0;
    for (int i = 0; i < size; i++) {
      double yv = y.loc().getAsDouble(i);
      double xv = x.loc().getAsDouble(i);
      if (!Is.NA(yv) && !Is.NA(xv)) {
        dot += xv * yv;
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
    return 1.0 / (1 + Math.exp(dot(a, b)));
  }

  public static Vector range(int size) {
    Vector.Builder builder = Vector.Builder.of(Integer.class);
    for (int i = 0; i < size; i++) {
      builder.add(i);
    }
    return builder.build();
  }

  /**
   * Returns an unmodifiable, identity, vector-builder which returns the argument when building a
   * vector. All mutators of the returned builder throws
   * {@link java.lang.UnsupportedOperationException}.
   *
   * <p>
   * This can be useful when copying a vector from one
   * {@linkplain org.briljantframework.data.dataframe.DataFrame.Builder DataFrame-builder} to
   * another without adding new values.
   *
   * <p>
   * Vectors marked with the {@link org.briljantframework.data.Transferable}-interface will be
   * <em>transfered</em> without copying when built.
   *
   * @param vector the vector to be built
   * @return a transferable vector-builder
   */
  public static Vector.Builder transferableBuilder(Vector vector) {
    return new TransferableVectorBuilder(vector);
  }

  private static class TransferableVectorBuilder implements Vector.Builder {

    private final Vector vector;

    private TransferableVectorBuilder(Vector vector) {
      if (!(vector instanceof Transferable)) {
        this.vector = vector.newCopyBuilder().build();
      } else {
        this.vector = vector;
      }
    }

    @Override
    public Vector.Builder setNA(Object key) {
      throw new UnsupportedOperationException();
    }

    @Override
    public Vector.Builder addNA() {
      throw new UnsupportedOperationException();
    }

    @Override
    public Vector.Builder add(Vector from, int fromIndex) {
      throw new UnsupportedOperationException();
    }

    @Override
    public Vector.Builder add(Vector from, Object key) {
      throw new UnsupportedOperationException();
    }

    @Override
    public Vector.Builder set(Object atKey, Vector from, int fromIndex) {
      throw new UnsupportedOperationException();
    }

    @Override
    public Vector.Builder set(Object atKey, Vector from, Object fromIndex) {
      throw new UnsupportedOperationException();
    }

    @Override
    public Vector.Builder set(Object key, Object value) {
      throw new UnsupportedOperationException();
    }

    @Override
    public Vector.Builder add(Object value) {
      throw new UnsupportedOperationException();
    }

    @Override
    public Vector.Builder add(double value) {
      throw new UnsupportedOperationException();
    }

    @Override
    public Vector.Builder add(int value) {
      throw new UnsupportedOperationException();
    }

    @Override
    public Vector.Builder addAll(Vector from) {
      throw new UnsupportedOperationException();
    }

    @Override
    public Vector.Builder remove(Object key) {
      throw new UnsupportedOperationException();
    }

    @Override
    public Vector.Builder readAll(DataEntry entry) throws IOException {
      throw new UnsupportedOperationException();
    }

    @Override
    public VectorLocationSetter loc() {
      throw new UnsupportedOperationException();
    }

    @Override
    public Vector.Builder read(DataEntry entry) {
      throw new UnsupportedOperationException();
    }

    @Override
    public int size() {
      return vector.size();
    }

    @Override
    public Vector getTemporaryVector() {
      return vector;
    }

    @Override
    public Vector build() {
      return vector;
    }
  }
}

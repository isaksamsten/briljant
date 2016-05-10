/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Isak Karlsson
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
package org.briljantframework.data.series;

import java.util.*;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Predicate;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.distribution.RealDistribution;
import org.apache.commons.math3.stat.descriptive.StatisticalSummary;
import org.briljantframework.Check;
import org.briljantframework.array.Array;
import org.briljantframework.array.ComplexArray;
import org.briljantframework.array.DoubleArray;
import org.briljantframework.array.IntArray;
import org.briljantframework.data.Is;
import org.briljantframework.data.Na;
import org.briljantframework.data.index.Index;
import org.briljantframework.data.index.RangeIndex;
import org.briljantframework.data.statistics.FastStatistics;
import org.briljantframework.util.sort.QuickSort;

/**
 * Utilities for handling series.
 *
 * @author Isak Karlsson
 */
public final class Vectors {

  private Vectors() {}

  public static Series remove(Series series, Object key) {
    return series.newCopyBuilder().remove(key).build();
  }

  public static Series removeAll(Series series, Collection<?> keys) {
    if (keys.isEmpty()) {
      return series;
    }
    return removeIf(series, keys::contains);
  }

  public static Series removeIf(Series series, Predicate<Object> predicate) {
    Series.Builder vectorBuilder = series.newBuilder();
    for (Object key : series.getIndex()) {
      if (!predicate.test(key)) {
        vectorBuilder.setFrom(key, series, key);
      }
    }
    return vectorBuilder.build();
  }

  public static Series retainAll(Series series, Collection<?> keys) {
    if (keys.isEmpty()) {
      return Series.of();
    }
    return removeIf(series, key -> !keys.contains(key));
  }

  public static Series rand(int size, RealDistribution source) {
    DoubleSeries.Builder v = new DoubleSeries.Builder(0, size);
    for (int i = 0; i < size; i++) {
      v.setElement(i, source.sample());
    }
    return v.build();
  }

  public static <T> void copy(Class<T> t, Series src, int srcStart, Array<T> dest, int destStart,
      int length) {
    Check.argument(src.size() <= srcStart + length, "illegal source");
    Check.argument(dest.size() <= destStart + length, "illegal destination");
    for (int i = srcStart; i < length; i++) {
      dest.set(destStart++, src.loc().get(t, i));
    }
  }

  public static <T> void copy(Class<T> t, Series src, Array<T> dest) {
    copy(t, src, 0, dest, 0, src.size());
  }

  public static void copy(Series src, int srcStart, DoubleArray dest, int destStart, int length) {
    Check.argument(src.size() <= srcStart + length, "illegal source");
    Check.argument(dest.size() <= destStart + length, "illegal destination");
    for (int i = srcStart; i < length; i++) {
      dest.set(destStart++, src.loc().getDouble(i));
    }
  }

  public static <T> void copy(Series src, DoubleArray dest) {
    copy(src, 0, dest, 0, src.size());
  }

  public static <T> Array<T> toArray(Class<T> t, Series v) {
    return Array.copyOf(v.asList(t));
  }

  public static DoubleArray toDoubleArray(Series v, DoubleUnaryOperator operator) {
    DoubleArray a = DoubleArray.zeros(v.size());
    for (int i = 0; i < v.size(); i++) {
      a.set(i, operator.applyAsDouble(v.loc().getDouble(i)));
    }
    return a;
  }

  public static DoubleArray toDoubleArray(Series v) {
    return toDoubleArray(v, DoubleUnaryOperator.identity());
  }

  public static ComplexArray toComplexArray(Series v) {
    return ComplexArray.copyOf(v.asList(Complex.class));
  }

  public static IntArray toIntArray(Series v) {
    return IntArray.copyOf(v.asList(Integer.class));
  }

  /**
   * Return a string representation of a series
   *
   * @param v the series
   * @param max the maximum number of elements to print before truncating
   * @return a string representation
   */
  public static String toString(Series v, int max) {
    Objects.requireNonNull(v);
    StringBuilder builder = new StringBuilder();
    Index index = v.getIndex();
    max = v.size() < max ? v.size() : 10;

    // Compute the longest string representation of a key
    int longestKey = String.valueOf(index.size() - 1).length();
    if (!(index instanceof RangeIndex)) {
      for (int i = 0; i < v.size(); i++) {
        Object key = index.get(i);
        int length = Is.NA(key) ? 2 : key.toString().length();
        if (i >= max) {
          int left = v.size() - i - 1;
          if (left > max) {
            i += left - max - 1;
          }
        }
        if (length > longestKey) {
          longestKey = length;
        }
      }
    }

    for (int i = 0; i < v.size(); i++) {
      Object key = index.get(i);
      String keyString = Is.NA(key) ? "NA" : key.toString();
      int keyPad = (longestKey - keyString.length());
      builder.append(keyString).append("   ");
      for (int j = 0; j < keyPad; j++) {
        builder.append(" ");
      }
      builder.append(Na.toString(v.get(String.class, key))).append("\n");
      if (i >= max) {
        int left = v.size() - i - 1;
        if (left > max) {
          builder.append("  ");
          for (int j = 0; j < longestKey; j++) {
            builder.append(" ");
          }
          builder.append("...\n");
          i += left - max - 1;
        }
      }
    }
    builder.append("Shape: ").append(Arrays.toString(v.getShape())).append(", type: ")
        .append(v.getType().toString());
    return builder.toString();
  }

  /**
   * Finds the index, in {@code series}, of the value at {@code index} in {@code values}. Hence,
   * given {@code Series a}, {@code Series b} and the index {@code i}, {@code find(a, b, i)} should
   * be preferred over {@code find(a, b.loc().get(i))}.
   *
   * @param haystack the series to search
   * @param needleSource the source of the needle
   * @param needle the needle in the source
   * @return the (first) index of {@code needleSource.get(needle)} in {@code haystack} or {@code -1}
   */
  public static int find(Series haystack, Series needleSource, int needle) {
    for (int i = 0; i < haystack.size(); i++) {
      if (haystack.loc().equals(i, needleSource, needle)) {
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
   * @return the index of {@code needle} or {@code -1}
   */
  @Deprecated
  public static int find(Series haystack, Object needle) {
    for (int i = 0; i < haystack.size(); i++) {
      Object v = haystack.loc().get(i);
      if (Is.equal(v, needle)) {
        return i;
      }
    }
    return -1;
  }

  /**
   * Finds the index of the first value for which {@code predicate} returns true.
   *
   * @param series the series
   * @param predicate the predicate
   * @return the index or {@code -1} if no value matched the predicate {@code true}
   */
  public static <T> int indexOf(Class<T> cls, Series series, Predicate<T> predicate) {
    for (int i = 0; i < series.size(); i++) {
      if (predicate.test(series.loc().get(cls, i))) {
        return i;
      }
    }
    return -1;
  }

  /**
   * <p>
   * Split {@code series} into {@code chunks}. Handles the case when {@code series.size()} is not
   * evenly dividable by chunks by making some chunks larger.
   * </p>
   * <p>
   * <p>
   * This implementation is lazy, i.e. chunking is done 'on-the-fly'. To get a list, {@code new
   * ArrayList<>(Vectors.split(vec, 10))}
   * </p>
   * <p>
   * <p>
   * Ensures that {@code series.getType()} is preserved.
   * </p>
   *
   * @param series the series
   * @param chunks the number of chunks
   * @return a collection of {@code chunk} chunks
   */
  public static Collection<Series> split(Series series, int chunks) {
    Check.argument(series.size() >= chunks, "size must be shorter than chunks");
    if (series.size() == chunks) {
      return Collections.singleton(series);
    }
    int bin = series.size() / chunks;
    int remainder = series.size() % chunks;

    return new AbstractCollection<Series>() {
      @Override
      public Iterator<Series> iterator() {
        return new Iterator<Series>() {
          private int current = 0;
          private int remainders = 0;

          @Override
          public boolean hasNext() {
            return current < series.size();
          }

          @Override
          public Series next() {
            int binSize = bin;
            if (remainders < remainder) {
              remainders++;
              binSize += 1;
            }
            Series.Builder builder = series.newBuilder();
            for (int i = 0; i < binSize; i++) {
              builder.addFromLocation(series, current++);
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
   * <p>
   * Computes the population standard deviation of {@code series}.
   * <p>
   * <p>
   * A series of all {@code NA} returns {@code NA}
   *
   * @param series the series
   * @return the standard deviation
   */
  public static double std(Series series) {
    return statisticalSummary(series).getStandardDeviation();
  }

  /**
   * Computes descriptive statistics of {@code series}
   *
   * @param series a series
   * @return the descriptive statistics
   */
  public static StatisticalSummary statisticalSummary(Series series) {
    FastStatistics r = new FastStatistics();
    for (int i = 0; i < series.size(); i++) {
      double v = series.loc().getDouble(i);
      if (!Is.NA(v)) {
        r.addValue(v);
      }
    }
    return r.getSummary();
  }

  /**
   * <p>
   * Computes the population standard deviation of {@code series} using an already computed
   * {@code mean}.
   * <p>
   * <p>
   * A series of all {@code NA} returns {@code NA}
   *
   * @param series the series
   * @param mean the mean
   * @return the standard deviation
   */
  public static double std(Series series, double mean) {
    double var = var(series, mean);
    return Is.NA(var) ? Na.DOUBLE : Math.sqrt(var);
  }

  /**
   * <p>
   * Computes the population variance of {@code series} using an already computed {@code mean}.
   * <p>
   * <p>
   * A series of all {@code NA} returns {@code NA}
   *
   * @param series the series
   * @param mean the mean
   * @return the variance; or NA
   */
  public static double var(Series series, double mean) {
    double var = 0;
    int nonNA = 0;
    for (int i = 0; i < series.size(); i++) {
      if (!series.loc().isNA(i)) {
        double residual = series.loc().getDouble(i) - mean;
        var += residual * residual;
        nonNA += 1;
      }
    }
    return nonNA == 0 ? Na.of(Double.class) : var / (double) nonNA;
  }

  /**
   * <p>
   * Computes the population variance of {@code series}.
   * <p>
   * <p>
   * A series of all {@code NA} returns {@code NA}
   *
   * @param series the series
   * @return the variance
   */
  public static double var(Series series) {
    return var(series, mean(series));
  }

  /**
   * <p>
   * Computes the sample mean of {@code series}.
   * <p>
   * <p>
   * A series of all {@code NA} returns {@code NA}
   *
   * @param series the series
   * @return the mean; or NA
   */
  public static double mean(Series series) {
    double mean = 0;
    int nonNA = 0;
    for (int i = 0; i < series.size(); i++) {
      if (!series.loc().isNA(i)) {
        mean += series.loc().getDouble(i);
        nonNA += 1;
      }
    }

    return nonNA == 0 ? Na.of(Double.class) : mean / (double) nonNA;
  }

  /**
   * Computes the sum of values in {@code series}. Ignores {@code NA} values.
   *
   * @param series the series
   * @return the sum
   */
  public static double sum(Series series) {
    double sum = 0;
    int nonNas = 0;
    for (int i = 0; i < series.size(); i++) {
      double d = series.loc().getDouble(i);
      boolean nonNa = !Is.NA(d);
      if (nonNa) {
        sum += d;
        nonNas++;
      }
    }
    return nonNas > 0 ? sum : Na.of(Double.class);
  }

  public static <T extends Number> double sum(Class<T> cls, Series series) {
    return series.asList(cls).stream().filter(x -> !Is.NA(x)).mapToDouble(Number::doubleValue)
        .sum();
  }

  public static <T extends Comparable<T>> Optional<T> min(Class<T> cls, Series series) {
    return series.stream(cls).min(Comparable::compareTo);
  }

  public static <T extends Comparable<T>> Optional<T> max(Class<T> cls, Series series) {
    return series.stream(cls).max(Comparable::compareTo);
  }

  /**
   * Finds the minimum value in {@code v}. Ignores {@code NA} values.
   *
   * @param v the series
   * @return the minimum value or {@code NA} if all values are {@code NA}
   */
  public static double min(Series v) {
    return v.doubleStream().filter(Is::NA).min().orElse(Na.DOUBLE);
  }

  /**
   * Finds the maximum value in {@code v}. Ignores {@code NA} values.
   *
   * @param v the series
   * @return the maximum value or {@code NA} if all values are {@code NA}
   */
  public static double max(Series v) {
    return v.doubleStream().filter(Is::NA).max().orElse(Na.DOUBLE);
  }

  /**
   * <p>
   * Returns a series consisting of the unique values in {@code series}
   * <p>
   * <p>
   * For example, given {@code a, b} and {@code c}
   * <p>
   * 
   * <pre>
   * {
   *   &#064;code
   *   Series a = new IntSeries(1, 2, 3, 4);
   *   Series b = new IntSeries(2, 3, 4, 5);
   *   Series c = new IntSeries(3, 4, 5, 6);
   *
   *   Series d = Vectors.unique(a, b, c);
   *   // d == [1,2,3,4,5,6];
   * }
   * </pre>
   */
  public static Series unique(Series... series) {
    series = Objects.requireNonNull(series);
    Check.argument(series.length > 0);
    Series.Builder builder = series[0].newBuilder();
    Set<Object> taken = new HashSet<>();
    for (Series serie : series) {
      for (int i = 0; i < serie.size(); i++) {
        Object value = serie.loc().get(i);
        if (!taken.contains(value)) {
          taken.add(value);
          builder.addFromLocation(serie, i);
        }
      }
    }
    return builder.build();
  }

  /**
   * <p>
   * Counts the number of occurrences for each value (of type {@code T}) in {@code series}
   * <p>
   * <p>
   * Since {@link Series#get(Class, Object)} returns {@code NA} if value is not an instance of
   * {@code T}, the resulting {@code Map} might contain a {@code null} key
   *
   * @param cls the class
   * @param series the series
   * @param <T> the type
   * @return a map of values to counts
   */
  public static <T> Map<T, Integer> count(Class<T> cls, Series series) {
    Map<T, Integer> count = new HashMap<>();
    for (T value : series.asList(cls)) {
      count.compute(value, (x, v) -> v == null ? 1 : v + 1);
    }
    return Collections.unmodifiableMap(count);
  }


  /**
   * <p>
   * Counts the number of occurrences for each value (wrapping the in a {@link Object}) in
   * {@code series}
   *
   * @param series the series
   * @return a map of values to counts
   */
  public static Map<Object, Integer> count(Series series) {
    Map<Object, Integer> freq = new HashMap<>();
    for (Object value : series.asList(Object.class)) {
      freq.compute(value, (x, i) -> i == null ? 1 : i + 1);
    }
    return Collections.unmodifiableMap(freq);
  }

  /**
   * @param series the series
   * @return the indexes of {@code series} sorted in increasing order by value
   */
  public static int[] indexSort(Series series) {
    return indexSort(series,
        (o1, o2) -> Double.compare(series.loc().getDouble(o1), series.loc().getDouble(o2)));
  }

  /**
   * @param series the series
   * @param comparator the comparator
   * @return the indexes of {@code series} sorted according to {@code comparator} by value
   */
  public static int[] indexSort(Series series, IntCmp comparator) {
    int[] indicies = new int[series.size()];
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
   * Compute the sigmoid between a and b, i.e. 1/(1+e^(a'(-b)))
   *
   * @param a a series
   * @param b a series
   * @return the sigmoid
   */
  public static double sigmoid(Series a, Series b) {
    return 1.0 / (1 + Math.exp(dot(a, b)));
  }

  /**
   * Inner product, i.e. the dot product x * y. Handles {@code NA} values by ignoring them.
   *
   * @param x a series
   * @param y a series
   * @return the dot product
   */
  public static double dot(Series x, Series y) {
    Check.dimension(x.size(), y.size());
    final int size = y.size();
    double dot = 0;
    for (int i = 0; i < size; i++) {
      double yv = y.loc().getDouble(i);
      double xv = x.loc().getDouble(i);
      if (!Is.NA(yv) && !Is.NA(xv)) {
        dot += xv * yv;
      }
    }
    return dot;
  }

  public static Series range(int size) {
    Series.Builder builder = Series.Builder.of(Integer.class);
    for (int i = 0; i < size; i++) {
      builder.addInt(i);
    }
    return builder.build();
  }

  @FunctionalInterface
  public interface IntCmp {

    int compare(int a, int b);
  }

}

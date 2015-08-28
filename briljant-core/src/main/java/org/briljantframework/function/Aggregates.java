/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Isak Karlsson
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.briljantframework.function;

import org.apache.commons.math3.stat.descriptive.AggregateSummaryStatistics;
import org.apache.commons.math3.stat.descriptive.StatisticalSummary;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.briljantframework.dataframe.ObjectIndex;
import org.briljantframework.vector.Is;
import org.briljantframework.vector.Logical;
import org.briljantframework.vector.Na;
import org.briljantframework.vector.TypeInferenceVectorBuilder;
import org.briljantframework.vector.Vector;
import org.briljantframework.vector.VectorType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * @author Isak Karlsson
 */
public final class Aggregates {

  private Aggregates() {
  }

  /**
   * Performs a transformation operation, mapping each element to a new value, adding it to the
   * {@code Vector.Builder} finishing it constructing a new {@code Vector}.
   *
   * @param supplier supply the vector builder
   * @param function the mapper
   * @param <T>      the input type
   * @param <O>      the output type
   * @return a transformation aggregator
   */
  public static <T, O> Collector<T, ?, Vector> transform(
      Supplier<Vector.Builder> supplier,
      Function<? super T, ? extends O> function) {
    return Collector.of(
        supplier,
        (acc, v) -> acc.add(function.apply(v)),
        (Vector.Builder left, Vector.Builder right) -> {
          left.addAll(right);
          return left;
        },
        Vector.Builder::build
    );
  }

  /**
   * Returns an aggregator that is able to filter values.
   *
   * @param supplier  the vector builder
   * @param predicate the predicate. If {@code true} include value.
   * @param <T>       the input type
   * @return a filtering aggregator
   */
  public static <T> Collector<T, ?, Vector> filter(Supplier<Vector.Builder> supplier,
                                                   Predicate<T> predicate) {
    return Collector.of(
        supplier,
        (acc, v) -> {
          if (predicate.test(v)) {
            acc.add(v);
          }
        },
        (Vector.Builder left, Vector.Builder right) -> {
          left.addAll(right);
          return left;
        },
        Vector.Builder::build);
  }

  /**
   * Returns an aggregator the performs a test on each value and returns a bit-vector with the
   * result of the test.
   *
   * @param predicate the predicate
   * @param <T>       the input type
   * @return a filter aggregator
   */
  public static <T> Collector<T, ?, Vector> test(Predicate<T> predicate) {
    return transform(() -> VectorType.from(Logical.class).newBuilder(), predicate::test);
  }

  /**
   * @return an aggregator for testing, and aggregating a bit-vector, for values that are {@code
   * NA}.
   */
  public static Collector<Object, ?, Vector> isNA() {
    return test(Is::NA);
  }

  /**
   * @return an aggregator for testing, and aggregating a bit-vector, for values that are not {@code
   * NA}.
   */
  public static Collector<Object, ?, Vector> nonNA() {
    return test(v -> !Is.NA(v));
  }

  /**
   * @param copies the number of copies of each element
   * @return an aggregator that repeats each value {@code copies} times.
   */
  public static <T> Collector<T, ?, Vector> each(Supplier<Vector.Builder> vb, int copies) {
    return Collector.of(
        vb,
        (acc, v) -> {
          for (int i = 0; i < copies; i++) {
            acc.add(v);
          }
        },
        (Vector.Builder left, Vector.Builder right) -> {
          left.addAll(right);
          return left;
        },
        Vector.Builder::build);
  }

  public static <T> Collector<T, ?, Vector> each(int copies) {
    return each(TypeInferenceVectorBuilder::new, copies);
  }

  public static <T> Collector<T, ?, Vector> repeat(Supplier<Vector.Builder> vb, int copies) {
    return Collector.of(
        vb,
        Vector.Builder::add,
        (Vector.Builder left, Vector.Builder right) -> {
          left.addAll(right);
          return left;
        },
        (v) -> {
          Vector temp = v.getTemporaryVector();
          int size = temp.size();
          for (int i = 1; i < copies; i++) {
            for (int j = 0; j < size; j++) {
              v.add(temp, j);
            }
          }
          return v.build();
        });
  }

  public static <T> Collector<T, ?, Vector> repeat(int copies) {
    return repeat(TypeInferenceVectorBuilder::new, copies);
  }

  public static <T> Collector<T, ?, Vector> valueCounts() {
    return Collector.of(
        HashMap::new,
        (map, t) -> map.compute(t, (v, c) -> c == null ? 1 : c + 1),
        new BinaryOperator<HashMap<T, Integer>>() {
          @Override
          public HashMap<T, Integer> apply(HashMap<T, Integer> left,
                                           HashMap<T, Integer> right) {
            right.forEach((k, v) -> left.merge(
                              k, v, (Integer o, Integer n) -> o == null ? n : o + n)
            );
            return left;
          }
        },
        (map) -> {
          Vector.Builder b = new TypeInferenceVectorBuilder();
          for (Map.Entry<T, Integer> e : map.entrySet()) {
            b.set(e.getKey(), e.getValue());
          }
          return b.build();
        },
        Collector.Characteristics.UNORDERED
    );
  }

  public static <T> Collector<T, ?, Vector> unique() {
    return Collector.of(
        HashSet::new,
        HashSet::add,
        (ts, ts2) -> {
          ts.addAll(ts2);
          return ts;
        },
        ts -> new TypeInferenceVectorBuilder().addAll(ts).build()
    );
  }

  public static <T> Collector<T, ?, Optional<T>> maxBy(Comparator<? super T> comparator) {
    return Collectors.reducing(BinaryOperator.maxBy(comparator));
  }

  public static <T> Collector<T, ?, Optional<T>> minBy(Comparator<? super T> comparator) {
    return Collectors.reducing(BinaryOperator.minBy(comparator));
  }

  public static <T> Collector<T, ?, T> mode() {
    return Collector.of(
        HashMap::new,
        (HashMap<T, Integer> map, T value) ->
            map.compute(value, (key, count) -> count == null ? 1 : count + 1),
        (left, right) -> {
          right.forEach((k, v) -> left.merge(k, v, (Integer o, Integer n) ->
              o == null ? n : o + n));
          return left;
        },
        (HashMap<T, Integer> map) -> {
          int max = 0;
          T value = null;
          for (Map.Entry<T, Integer> k : map.entrySet()) {
            if (k.getValue() > max) {
              value = k.getKey();
            }
          }
          return value;
        },
        Collector.Characteristics.UNORDERED
    );
  }

  public static <T> Collector<T, ?, Integer> nunique() {
    return Collector.of(HashSet::new, HashSet::add, (left, right) -> {
      left.addAll(right);
      return left;
    }, HashSet::size);
  }

  public static Collector<Number, ?, Double> sum() {
    return Collector.of(
        () -> new double[1],
        (s, v) -> {
          if (!Is.NA(v)) {
            s[0] += v.doubleValue();
          }
        },
        (left, right) -> {
          left[0] = left[0] + right[0];
          return left;
        },
        s -> s[0]);
  }

  public static Collector<Number, ?, Vector> summary() {
    return withFinisher(statisticalSummary(), v -> {
      Vector summary = Vector.of(
          v.getMean(),
          v.getSum(),
          v.getStandardDeviation(),
          v.getVariance(),
          v.getMin(),
          v.getMax(),
          v.getN()
      );
      summary.setIndex(ObjectIndex.from(
          "mean",
          "sum",
          "std",
          "var",
          "min",
          "max",
          "n"
      ));
      return summary;
    });
  }

  public static Collector<Number, ?, StatisticalSummary> statisticalSummary() {
    AggregateSummaryStatistics statistics = new AggregateSummaryStatistics();
    return Collector.of(
        statistics::createContributingStatistics,
        (SummaryStatistics a, Number v) -> {
          if (!Is.NA(v)) {
            a.addValue(v.doubleValue());
          }
        },
        (left, right) -> left,
        (stat) -> {
          return statistics.getSummary();
        }
    );
  }

  public static <T, A, R, F> Collector<T, ?, F> withFinisher(
      Collector<T, A, R> collector,
      Function<R, F> finisher) {
    Function<A, R> f = collector.finisher();

    Set<Collector.Characteristics> characteristics = collector.characteristics();
    Collector.Characteristics[] empty = new Collector.Characteristics[characteristics.size()];
    return Collector.of(
        collector.supplier(),
        collector.accumulator(),
        collector.combiner(),
        f.andThen(finisher),
        characteristics.toArray(empty)
    );
  }

  public static Collector<Number, ?, Double> mean() {
    return withFinisher(statisticalSummary(), StatisticalSummary::getMean);
  }

  public static Collector<Number, ?, Double> std() {
    return withFinisher(statisticalSummary(), StatisticalSummary::getStandardDeviation);
  }

  public static Collector<Number, ?, Double> var() {
    return withFinisher(statisticalSummary(), StatisticalSummary::getVariance);
  }

  /**
   * @return an aggregator that computes the median.
   */
  public static Collector<Number, ?, Double> median() {
    return Collector.of(
        ArrayList::new,
        ArrayList::add,
        (left, right) -> {
          left.addAll(right);
          return left;
        },
        (ArrayList<Number> list) -> {
          int size = list.size();
          if (size == 0) {
            return Na.from(Double.class);
          } else if (size == 1) {
            return list.get(0).doubleValue();
          } else if (size == 2) {
            return (list.get(0).doubleValue() + list.get(1).doubleValue()) / 2;
          } else {
            Collections.sort(list, (a, b) -> Double.compare(a.doubleValue(), b.doubleValue()));
            int index = (size - 1) / 2;
            if (size % 2 == 0) {
              return (list.get(index).doubleValue() + list.get(index + 1).doubleValue()) / 2;
            } else {
              return list.get(index).doubleValue();
            }
          }
        }
    );
  }

  public static Collector<Number, ?, Number> max() {
    return Collector.of(
        () -> new double[]{Double.NEGATIVE_INFINITY},
        (a, v) -> {
          if (!Is.NA(v)) {
            a[0] = Math.max(a[0], v.doubleValue());
          }
        },
        (left, right) -> {
          left[0] = Math.max(left[0], right[0]);
          return left;
        },
        (r) -> r[0]
    );
  }

  public static Collector<Number, ?, Number> min() {
    return Collector.of(
        () -> new double[]{Double.NEGATIVE_INFINITY},
        (a, v) -> {
          if (!Is.NA(v)) {
            a[0] = Math.min(a[0], v.doubleValue());
          }
        },
        (left, right) -> {
          left[0] = Math.min(left[0], right[0]);
          return left;
        },
        (r) -> r[0]
    );
  }

  public static <T> Collector<T, ?, Integer> count() {
    return Collector.of(
        () -> new int[1],
        (int[] a, T b) -> {
          if (!Is.NA(b)) {
            a[0] += 1;
          }
        },
        (int[] left, int[] right) -> {
          left[0] += right[0];
          return left;
        },
        (int[] a) -> a[0]
    );
  }

  public static <T> Collector<T, ?, Vector> fillNa(T fill) {
    return Collector.of(
        TypeInferenceVectorBuilder::new,
        (builder, t) -> {
          if (Is.NA(t)) {
            builder.add(fill);
          } else {
            builder.add(t);
          }
        },
        (left, right) -> {
          left.addAll(right);
          return left;
        },
        Vector.Builder::build
    );
  }
}

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
package org.briljantframework.data;

import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collector;

import org.apache.commons.math3.stat.descriptive.StatisticalSummary;
import org.briljantframework.data.dataframe.ColumnDataFrame;
import org.briljantframework.data.dataframe.DataFrame;
import org.briljantframework.data.index.Index;
import org.briljantframework.data.series.Series;
import org.briljantframework.data.series.TypeInferenceBuilder;
import org.briljantframework.data.series.Types;
import org.briljantframework.data.statistics.FastStatistics;

/**
 * Define som common collectors for vectors and data frames.
 * 
 * @author Isak Karlsson
 */
public final class Collectors {

  private Collectors() {}

  public static Collector<Series, ?, DataFrame> toDataFrame() {
    return toDataFrame(ColumnDataFrame.Builder::new);
  }

  public static Collector<Series, ?, DataFrame> toDataFrame(Supplier<DataFrame.Builder> supplier) {
    return Collector.of(supplier, (DataFrame.Builder acc, Series record) -> {
      if (acc.columns() > 0 && record.size() != acc.columns()) {
        throw new IllegalStateException("All records must have the same size.");
      } else {
        acc.addRow(record);
      }
    }, (DataFrame.Builder left, DataFrame.Builder right) -> {
      if (left.columns() > 0 && left.rows() != right.columns()) {
        throw new IllegalStateException("Columns must all have the same length.");
      } else {
        for (Series series : right.build().getRows()) {
          left.addRow(series);
        }
        return left;
      }
    }, DataFrame.Builder::build);
  }

  public static <T, O> Collector<T, ?, Series> map(Function<? super T, ? extends O> function) {
    return map(TypeInferenceBuilder::new, function);
  }

  /**
   * Performs a transformation operation, mapping each element to a new value, adding it to the
   * {@code Series.Builder} finishing it constructs a new {@code Series}.
   *
   * @param supplier supply the series builder
   * @param function the mapper
   * @param <T> the input type
   * @param <O> the output type
   * @return a transformation aggregator
   */
  public static <T, O> Collector<T, ?, Series> map(Supplier<Series.Builder> supplier,
      Function<? super T, ? extends O> function) {
    return Collector.of(supplier, (acc, v) -> acc.add(function.apply(v)),
        (Series.Builder left, Series.Builder right) -> {
          left.setAll(right.build());
          return left;
        }, Series.Builder::build);
  }

  /**
   * Returns an aggregator that filter values.
   *
   * @param supplier the series builder
   * @param predicate the predicate. If {@code true} include value.
   * @param <T> the input type
   * @return a filtering aggregator
   */
  public static <T> Collector<T, ?, Series> filter(Supplier<Series.Builder> supplier,
      Predicate<T> predicate) {
    return Collector.of(supplier, (acc, v) -> {
      if (predicate.test(v)) {
        acc.add(v);
      }
    }, (Series.Builder left, Series.Builder right) -> {
      left.setAll(right.build());
      return left;
    }, Series.Builder::build);
  }

  /**
   * @return an aggregator for testing, and aggregating a bit-series, for values that are {@code NA}
   *         .
   */
  public static Collector<Object, ?, Series> isNA() {
    return test(Is::NA);
  }

  /**
   * Returns an aggregator the performs a test on each value and returns a series with the result of
   * the test.
   *
   * @param predicate the predicate
   * @param <T> the input type
   * @return a filter aggregator
   */
  public static <T> Collector<T, ?, Series> test(Predicate<T> predicate) {
    return map(() -> Types.getType(Logical.class).newBuilder(), predicate::test);
  }

  /**
   * @return an aggregator for testing, and aggregating a series, for values that are not {@code NA}
   *         .
   */
  public static Collector<Object, ?, Series> nonNA() {
    return test(v -> !Is.NA(v));
  }

  public static <T> Collector<T, ?, Series> each(int copies) {
    return each(TypeInferenceBuilder::new, copies);
  }

  /**
   * @param copies the number of copies of each element
   * @return an aggregator that repeats each value {@code copies} times.
   */
  public static <T> Collector<T, ?, Series> each(Supplier<Series.Builder> vb, int copies) {
    return Collector.of(vb, (acc, v) -> {
      for (int i = 0; i < copies; i++) {
        acc.add(v);
      }
    }, (Series.Builder left, Series.Builder right) -> {
      left.setAll(right.build());
      return left;
    }, Series.Builder::build);
  }

  public static <T> Collector<T, ?, Series> repeat(int copies) {
    return repeat(TypeInferenceBuilder::new, copies);
  }

  public static <T> Collector<T, ?, Series> repeat(Supplier<Series.Builder> vb, int copies) {
    return Collector.of(vb, Series.Builder::add, (Series.Builder left, Series.Builder right) -> {
      left.setAll(right.build());
      return left;
    }, (v) -> {
      Series elements = v.build();
      int size = elements.size();
      Series.Builder builder = elements.newBuilder();
      for (int i = 0; i < copies; i++) {
        for (int j = 0; j < size; j++) {
          builder.addFromLocation(elements, j);
        }
      }
      return builder.build();
    });
  }

  public static <T> Collector<T, ?, Series> valueCounts() {
    return Collector.of(HashMap::new, (map, t) -> map.compute(t, (v, c) -> c == null ? 1 : c + 1),
        new BinaryOperator<HashMap<T, Integer>>() {
          @Override
          public HashMap<T, Integer> apply(HashMap<T, Integer> left, HashMap<T, Integer> right) {
            right.forEach(
                (k, v) -> left.merge(k, v, (Integer o, Integer n) -> o == null ? n : o + n));
            return left;
          }
        }, (map) -> {
          Series.Builder b = new TypeInferenceBuilder();
          for (Map.Entry<T, Integer> e : map.entrySet()) {
            b.set(e.getKey(), e.getValue());
          }
          return b.build();
        }, Collector.Characteristics.UNORDERED);
  }

  public static <T> Collector<T, ?, Series> unique() {
    return Collector.of(HashSet::new, HashSet::add, (ts, ts2) -> {
      ts.addAll(ts2);
      return ts;
    }, ts -> new TypeInferenceBuilder().addAll(ts).build());
  }

  public static <T> Collector<T, ?, T> mode() {
    return Collector.of(HashMap::new, (HashMap<T, Integer> map, T value) -> map.compute(value,
        (key, count) -> count == null ? 1 : count + 1), (left, right) -> {
          right
              .forEach((k, v) -> left.merge(k, v, (Integer o, Integer n) -> o == null ? n : o + n));
          return left;
        }, (HashMap<T, Integer> map) -> {
          int max = 0;
          T value = null;
          for (Map.Entry<T, Integer> k : map.entrySet()) {
            if (k.getValue() > max) {
              value = k.getKey();
            }
          }
          return value;
        }, Collector.Characteristics.UNORDERED);
  }

  public static <T> Collector<T, ?, Integer> nunique() {
    return Collector.of(HashSet::new, HashSet::add, (left, right) -> {
      left.addAll(right);
      return left;
    }, HashSet::size);
  }

  public static Collector<Object, ?, Series> factorize() {
    class Factorize {

      private Map<Object, Integer> map = new HashMap<>();
      private Series.Builder builder = Series.Builder.of(Integer.class);
      private int highest = 0;
    }

    // TODO: refactor into a real state-less collector
    Factorize factorize = new Factorize();
    return Collector.of(() -> factorize, (Factorize acc, Object value) -> {
      synchronized (factorize) {
        Integer code = acc.map.get(value);
        if (code == null) {
          code = acc.highest;
          acc.map.put(value, code);
          acc.highest++;
        }
        acc.builder.add(code);
      }
    }, (left, right) -> left, (acc) -> acc.builder.build());
  }

  public static Collector<Number, ?, Double> sum() {
    return withFinisher(statisticalSummary(), (summary) -> {
      if (summary.getN() > 0) {
        return summary.getSum();
      } else {
        return Na.DOUBLE;
      }
    });
  }

  public static Collector<Number, ?, StatisticalSummary> statisticalSummary() {
    return Collector.of(FastStatistics::new, (FastStatistics a, Number v) -> {
      if (!Is.NA(v)) {
        a.addValue(v.doubleValue());
      }
    }, (left, right) -> {
      throw new IllegalStateException("Can't collect statistics in parallel yet.");
    }, FastStatistics::getSummary);
  }

  public static <T, A, R, F> Collector<T, ?, F> withFinisher(Collector<T, A, R> collector,
      Function<R, F> finisher) {
    Function<A, R> f = collector.finisher();

    Set<Collector.Characteristics> characteristics = collector.characteristics();
    Collector.Characteristics[] empty = new Collector.Characteristics[characteristics.size()];
    return Collector.of(collector.supplier(), collector.accumulator(), collector.combiner(),
        f.andThen(finisher), characteristics.toArray(empty));
  }

  public static Collector<Number, ?, Series> summary() {
    return withFinisher(statisticalSummary(), v -> {
      Series summary = Series.of(v.getMean(), v.getSum(), v.getStandardDeviation(), v.getVariance(),
          v.getMin(), v.getMax(), v.getN());
      return summary.reindex(Index.of("mean", "sum", "std", "var", "min", "max", "n"));
    });
  }

  public static Collector<Number, ?, Double> mean() {
    return withFinisher(statisticalSummary(), (summary) -> {
      if (summary.getN() > 0) {
        return summary.getMean();
      } else {
        return Na.of(Double.class);
      }
    });
  }

  public static Collector<Number, ?, Double> std() {
    return withFinisher(statisticalSummary(), (summary) -> {
      if (summary.getN() > 0) {
        return summary.getStandardDeviation();
      } else {
        return Na.of(Double.class);
      }
    });
  }

  public static Collector<Number, ?, Double> var() {
    return withFinisher(statisticalSummary(), (summary) -> {
      if (summary.getN() > 0) {
        return summary.getVariance();
      } else {
        return Na.of(Double.class);
      }
    });
  }

  /**
   * @return an aggregator that computes the median.
   */
  public static Collector<Number, ?, Double> median() {
    return Collector.of(ArrayList::new, ArrayList::add, (left, right) -> {
      left.addAll(right);
      return left;
    }, (ArrayList<Number> list) -> {
      int size = list.size();
      if (size == 0) {
        return Na.of(Double.class);
      } else if (size == 1) {
        return list.get(0).doubleValue();
      } else if (size == 2) {
        return (list.get(0).doubleValue() + list.get(1).doubleValue()) / 2;
      } else {
        list.sort((a, b) -> Double.compare(a.doubleValue(), b.doubleValue()));
        int index = (size - 1) / 2;
        if (size % 2 == 0) {
          return (list.get(index).doubleValue() + list.get(index + 1).doubleValue()) / 2;
        } else {
          return list.get(index).doubleValue();
        }
      }
    });
  }

  public static Collector<Number, ?, Number> max() {
    class MaxBox {

      private double value = Double.NEGATIVE_INFINITY;
      private boolean hasValue = false;

      private void update(double v) {
        if (!Is.NA(v)) {
          hasValue = true;
          value = Math.max(v, value);
        }
      }
    }

    return Collector.of(MaxBox::new, (a, v) -> a.update(v.doubleValue()), (left, right) -> {
      left.update(right.value);
      return left;
    }, (r) -> r.hasValue ? r.value : Na.DOUBLE);
  }

  public static Collector<Number, ?, Number> min() {
    class MinBox {

      private double value = Double.NEGATIVE_INFINITY;
      private boolean hasValue = false;

      private void update(double v) {
        if (!Is.NA(v)) {
          hasValue = true;
          value = Math.min(v, value);
        }
      }
    }
    return Collector.of(MinBox::new, (a, v) -> a.update(v.doubleValue()), (left, right) -> {
      left.update(right.value);
      return left;
    }, (r) -> r.hasValue ? r.value : Na.DOUBLE);
  }

  /**
   * Returns a collector that counts non-NA values
   *
   * @return a collector that counts non-NA values
   */
  public static Collector<Object, ?, Integer> count() {
    return Collector.of(() -> new int[1], (int[] a, Object b) -> {
      if (!Is.NA(b)) {
        a[0] += 1;
      }
    }, (int[] left, int[] right) -> {
      left[0] += right[0];
      return left;
    }, (int[] a) -> a[0]);
  }

  /**
   * Returns a collector that collects objects into a series while filling NA-values with the
   * supplied value
   *
   * @param fill the value to fill NA with
   * @return a collector for filling NA values
   */
  public static <T> Collector<T, ?, Series> fillNa(T fill) {
    return Collector.of(TypeInferenceBuilder::new, (builder, t) -> {
      if (Is.NA(t)) {
        builder.add(fill);
      } else {
        builder.add(t);
      }
    }, (left, right) -> {
      left.setAll(right.build());
      return left;
    }, Series.Builder::build);
  }

}

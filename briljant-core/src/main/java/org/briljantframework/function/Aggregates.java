package org.briljantframework.function;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Ordering;

import org.briljantframework.stat.RunningStatistics;
import org.briljantframework.vector.BitVector;
import org.briljantframework.vector.Is;
import org.briljantframework.vector.Na;
import org.briljantframework.vector.Vec;
import org.briljantframework.vector.Vector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

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
  public static <T, O> Aggregator<T, Vector, ?> transform(Supplier<Vector.Builder> supplier,
                                                          Function<? super T, ? extends O> function) {
    return Aggregator.of(supplier, (acc, v) -> acc.add(function.apply(v)), Vector.Builder::build);
  }

  /**
   * Returns an aggregator that is able to filter values.
   *
   * @param supplier  the vector builder
   * @param predicate the predicate. If {@code true} include value.
   * @param <T>       the input type
   * @return a filtering aggregator
   */
  public static <T> Aggregator<T, Vector, ?> filter(Supplier<Vector.Builder> supplier,
                                                    Predicate<T> predicate) {
    return Aggregator.of(supplier, (acc, v) -> {
      if (predicate.test(v)) {
        acc.add(v);
      }
    }, Vector.Builder::build);
  }

  /**
   * Returns an aggregator the performs a test on each value and returns a bit-vector with the
   * result of the test.
   *
   * @param predicate the predicate
   * @param <T>       the input type
   * @return a filter aggregator
   */
  public static <T> Aggregator<T, Vector, ?> test(Predicate<T> predicate) {
    return transform(BitVector.Builder::new, predicate::test);
  }

  /**
   * @return an aggregator for testing, and aggregating a bit-vector, for values that are {@code
   * NA}.
   */
  public static Aggregator<Object, Vector, ?> isNA() {
    return test(Is::NA);
  }

  /**
   * @return an aggregator for testing, and aggregating a bit-vector, for values that are not {@code
   * NA}.
   */
  public static Aggregator<Object, Vector, ?> notNA() {
    return test(v -> !Is.NA(v));
  }

  /**
   * @param copies the number of copies of each element
   * @return an aggregator that repeats each value {@code copies} times.
   */
  public static <T> Aggregator<T, Vector, ?> each(Supplier<Vector.Builder> vb, int copies) {
    return Aggregator.of(vb, (acc, v) -> {
      for (int i = 0; i < copies; i++) {
        acc.add(v);
      }
    }, Vector.Builder::build);
  }

  public static <T> Aggregator<T, Vector, ?> each(int copies) {
    return each(Vec::inferringBuilder, copies);
  }

  public static <T> Aggregator<T, Vector, ?> repeat(Supplier<Vector.Builder> vb, int copies) {
    return Aggregator.of(vb, Vector.Builder::add, (v) -> {
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

  public static <T> Aggregator<T, Vector, ?> repeat(int copies) {
    return repeat(Vec::inferringBuilder, copies);
  }

  public static <T> Aggregator<T, Map<T, Integer>, ?> valueCounts() {
    return Aggregator.of(
        HashMap::new,
        (map, t) -> map.compute(t, (v, c) -> c == null ? 1 : c + 1),
        Function.identity()
    );
  }

  public static <T> Aggregator<T, Map<T, Double>, ?> normalizedValueCounts() {
    class MapCounter {

      private HashMap<T, Integer> map = new HashMap<>();
      private int count = 0;
    }
    return Aggregator.of(MapCounter::new, new BiConsumer<MapCounter, T>() {
      @Override
      public void accept(MapCounter mapCounter, T t) {
        mapCounter.count++;
        mapCounter.map.compute(t, (v, c) -> c == null ? 1 : c + 1);
      }
    }, mapCounter -> {
      Map<T, Double> map = new HashMap<>();
      mapCounter.map.entrySet().forEach(
          e -> map.put(e.getKey(), e.getValue() / (double) mapCounter.count)
      );
      return map;
    });
  }

  public static <T> Aggregator<T, T, ?> reducing(BinaryOperator<T> operator) {
    class Value implements Consumer<T> {

      private T value;

      @Override
      public void accept(T t) {
        if (value == null) {
          value = t;
        } else {
          operator.apply(value, t);
        }
      }
    }
    return Aggregator.of(Value::new, Value::accept, (acc) -> acc.value);
  }

  public static <T> Aggregator<T, Vector, ?> unique() {
    return Aggregator.of(
        HashSet::new, HashSet::add, (set) -> Vec.inferringBuilder().addAll(set).build()
    );
  }

  public static <T> Aggregator<T, T, ?> maxBy(Comparator<? super T> comparator) {
    return reducing(BinaryOperator.maxBy(comparator));
  }

  public static <T> Aggregator<T, T, ?> minBy(Comparator<? super T> comparator) {
    return reducing(BinaryOperator.minBy(comparator));
  }

  public static <T> Aggregator<T, T, ?> mode() {
    return Aggregator.of(
        HashMultiset::<T>create,
        (a, v) -> a.add(v),
        (HashMultiset<T> accum) -> Ordering.natural().onResultOf(
            new com.google.common.base.Function<Multiset.Entry<T>, Integer>() {
              public Integer apply(Multiset.Entry<T> entry) {
                return entry.getCount();
              }
            }).max(accum.entrySet()).getElement()
    );
  }

  public static <T> Aggregator<T, Integer, ?> nunique() {
    return Aggregator.of(HashSet::new, HashSet::add, HashSet::size);
  }

  public static Aggregator<Number, Double, ?> sum() {
    return Aggregator.of(() -> new double[0], (s, v) -> {
      if (!Is.NA(v)) {
        s[0] += v.doubleValue();
      }
    }, s -> s[0]);
  }

  public static Aggregator<Number, Double, ?> mean() {
    return Aggregator.of(RunningStatistics::new, (a, v) -> {
      if (!Is.NA(v)) {
        a.add(v.doubleValue());
      }
    }, RunningStatistics::getMean);
  }

  public static Aggregator<Number, Double, ?> std() {
    return Aggregator.of(RunningStatistics::new, (a, v) -> {
      if (!Is.NA(v)) {
        a.add(v.doubleValue());
      }
    }, RunningStatistics::getStandardDeviation);
  }

  public static Aggregator<Number, Double, ?> var() {
    return Aggregator.of(RunningStatistics::new, (a, v) -> {
      if (!Is.NA(v)) {
        a.add(v.doubleValue());
      }
    }, RunningStatistics::getVariance);
  }

  /**
   * @return an aggregator that computes the median.
   */
  public static Aggregator<Number, Double, ?> median() {
    Aggregator<Number, Double, ? extends List<Number>> of = Aggregator.of(
        ArrayList::new,
        ArrayList::add, (list) -> {
          int size = list.size();
          if (size == 0) {
            return Na.of(Double.class);
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
    return of;
  }

  public static Aggregator<Double, Double, ?> max() {
    return Aggregator.of(RunningStatistics::new, (a, v) -> {
      if (!Is.NA(v)) {
        a.add(v);
      }
    }, (r) -> {
      if (r.size() == 0) {
        return Na.of(Double.class);
      } else {
        return r.getMax();
      }
    });
  }

  public static Aggregator<Number, Number, ?> min() {
    return Aggregator.of(RunningStatistics::new, (a, v) -> {
      if (!Is.NA(v)) {
        a.add(v.doubleValue());
      }
    }, (r) -> {
      if (r.size() == 0) {
        return Na.of(Double.class);
      } else {
        return r.getMin();
      }
    });
  }

  public static <T> Aggregator<T, Integer, ?> count() {
    return Aggregator.of(() -> new int[1], (int[] a, T b) -> {
      if (!Is.NA(b)) {
        a[0] += 1;
      }
    }, (int[] a) -> a[0]);
  }

  public static Aggregator<Object, String, ?> join(CharSequence delimit) {
    return join(delimit, "", "");
  }

  public static Aggregator<Object, String, ?> join(CharSequence delimit,
                                                   CharSequence prefix,
                                                   CharSequence suffix) {
    return Aggregator.of(() -> new StringJoiner(delimit, prefix, suffix),
                         (j, s) -> j.add(!Is.NA(s) ? s.toString() : "NA"),
                         StringJoiner::toString);
  }


  static class AggregatorImpl<T, R, C> implements Aggregator<T, R, C> {

    private final Supplier<C> supplier;
    private final BiConsumer<C, T> accumulator;
    private final Function<C, R> finisher;

    AggregatorImpl(Supplier<C> supplier, BiConsumer<C, T> accumulator, Function<C, R> finisher) {
      this.supplier = Objects.requireNonNull(supplier);
      this.accumulator = Objects.requireNonNull(accumulator);
      this.finisher = Objects.requireNonNull(finisher);
    }

    @Override
    public Supplier<C> supplier() {
      return supplier;
    }

    @Override
    public BiConsumer<C, T> accumulator() {
      return accumulator;
    }

    @Override
    public Function<C, R> finisher() {
      return finisher;
    }
  }
}

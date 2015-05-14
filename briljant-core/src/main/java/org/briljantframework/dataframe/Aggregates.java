package org.briljantframework.dataframe;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Ordering;

import org.briljantframework.stat.RunningStatistics;
import org.briljantframework.vector.Is;
import org.briljantframework.vector.Na;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author Isak Karlsson
 */
public final class Aggregates {

  private Aggregates() {
  }

  public static <T, R, C> Aggregator<T, R, C> of(Supplier<C> supplier, BiConsumer<C, T> accumulator,
                                                 Function<C, R> finisher) {
    return new AggregatorImpl<>(supplier, accumulator, finisher);
  }

  public static <T> Aggregator<T, T, ?> mode() {
    return of(
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
    return of(HashSet::new, HashSet::add, HashSet::size);
  }

  public static Aggregator<Number, Double, ?> sum() {
    class Summation {

      double sum = 0;
    }
    return of(Summation::new, (s, v) -> {
      if (!Is.NA(v)) {
        s.sum += v.doubleValue();
      }
    }, s -> s.sum);
  }

  public static Aggregator<Number, Double, ?> mean() {
    return of(RunningStatistics::new, (a, v) -> {
      if (!Is.NA(v)) {
        a.add(v.doubleValue());
      }
    }, RunningStatistics::getMean);
  }

  public static Aggregator<Number, Double, ?> std() {
    return of(RunningStatistics::new, (a, v) -> {
      if (!Is.NA(v)) {
        a.add(v.doubleValue());
      }
    }, RunningStatistics::getStandardDeviation);
  }

  public static Aggregator<Number, Double, ?> var() {
    return of(RunningStatistics::new, (a, v) -> {
      if (!Is.NA(v)) {
        a.add(v.doubleValue());
      }
    }, RunningStatistics::getVariance);
  }

  /**
   * @return an aggregator that computes the median.
   */
  public static Aggregator<Number, Double, ?> median() {
    Aggregator<Number, Double, ? extends List<Number>> of = of(
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

  public static Aggregator<Number, Number, ?> max() {
    return of(RunningStatistics::new, (a, v) -> {
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

  public static Aggregator<Number, Number, ?> min() {
    return of(RunningStatistics::new, (a, v) -> {
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
    class Counter {

      int count = 0;
    }
    return of(Counter::new, (a, b) -> {
      if (!Is.NA(b)) {
        a.count += 1;
      }
    }, (a) -> a.count);
  }

  public static Aggregator<Object, String, ?> join(CharSequence delimit) {
    return join(delimit, "", "");
  }

  public static Aggregator<Object, String, ?> join(CharSequence delimit,
                                                         CharSequence prefix,
                                                         CharSequence suffix) {
    return of(() -> new StringJoiner(delimit, prefix, suffix),
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

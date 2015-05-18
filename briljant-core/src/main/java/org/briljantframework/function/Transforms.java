package org.briljantframework.function;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Created by isak on 16/05/15.
 */
public final class Transforms {

  private Transforms() {
  }

  public static <T extends Comparable<T>> Function<T, Boolean> lessThan(T value) {
    return v -> v.compareTo(value) < 0;
  }

  public static <T extends Comparable<T>> Function<T, Boolean> greaterThan(T value) {
    return v -> v.compareTo(value) > 0;
  }

  public static <T> Function<T, Boolean> equal(T value) {
    return v -> v.equals(value);
  }

  public static <T extends Comparable<T>> BiFunction<T, T, Boolean> lessThan() {
    return (a, b) -> a.compareTo(b) < 0;
  }

  public static <T extends Comparable<T>> BiFunction<T, T, Boolean> greaterThan() {
    return (a, b) -> a.compareTo(b) > 0;
  }

  public static <T> BiFunction<T, T, Boolean> equal() {
    return Object::equals;
  }

}

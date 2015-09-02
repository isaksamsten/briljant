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

package org.briljantframework.data;

import org.briljantframework.data.vector.Is;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.UnaryOperator;

/**
 * Created by isak on 16/05/15.
 */
public final class Transformations {

  private Transformations() {
  }

  public static <T extends Comparable<T>> Function<T, Boolean> lessThan(T value) {
    return v -> v.compareTo(value) < 0;
  }

  public static <T extends Comparable<T>> Function<T, Boolean> greaterThan(T value) {
    return v -> v.compareTo(value) > 0;
  }

  public static <T extends Comparable<T>> BiFunction<T, T, Boolean> lessThan() {
    return (a, b) -> a.compareTo(b) < 0;
  }

  public static <T extends Comparable<T>> BiFunction<T, T, Boolean> greaterThan() {
    return (a, b) -> a.compareTo(b) > 0;
  }

  public static <T> Function<T, Boolean> equal(T value) {
    return v -> v.equals(value);
  }

  public static <T> BiFunction<T, T, Boolean> equal() {
    return Object::equals;
  }

  public static <T> UnaryOperator<T> replaceNA(T replace) {
    return v -> Is.NA(v) ? replace : v;
  }

  public static UnaryOperator<Double> clip(double lower, double upper) {
    return v -> {
      if (v < lower) {
        return lower;
      } else if (v > upper) {
        return upper;
      } else {
        return v;
      }
    };
  }

}

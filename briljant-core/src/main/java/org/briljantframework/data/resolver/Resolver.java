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

package org.briljantframework.data.resolver;

import org.briljantframework.data.Na;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Resolver<R> {

  private final Class<R> cls;
  private final List<Holder<R>> converters = new CopyOnWriteArrayList<>();

  public Resolver(Class<R> cls) {
    this.cls = cls;
  }

  /**
   * Add a converter able to convert from values of {@code Class<T>} to values of {@code Class<R>}.
   *
   * @param cls       the class to convert from
   * @param converter the converter to do the conversion
   * @param <T>       the type
   */
  public <T> void put(Class<T> cls, Converter<T, R> converter) {
    for (Holder<R> holder : converters) {
      if (holder.cls.equals(cls)) {
        holder.converter = converter;
        return;
      }
    }
    converters.add(new Holder<>(cls, converter));
  }

  /**
   * Resolves the value of {@code value} to an instance of {@code R}. If it fails, returns the
   * value
   * denoting {@code NA} (for the type {@code R}) as returned by {@link
   * org.briljantframework.data.Na#of(Class)}.
   *
   * <p>Use {@link org.briljantframework.data.Is#NA(java.lang.Object)} to check for {@code
   * NA}
   * values.
   *
   * <p>The resolves values by sequentially scan the added converters and finds the first converter
   * where {@link Class#isAssignableFrom(Class)} returns true.
   *
   * <p>If the above becomes a bottleneck, it might be reconsidered (e.g., to only consider exact
   * class matches)
   *
   * @param value the value to resolve
   * @return the resolved value; or {@code Na.from(value.getClass())} otherwise
   */
  public R resolve(Object value) {
    return resolve(value.getClass(), value);
  }

  @SuppressWarnings("unchecked")
  private <T> Converter<T, R> get(Class<T> cls) {
    return (Converter<T, R>) getConverter(cls);
  }

  @SuppressWarnings("unchecked")
  private Converter<Object, R> getConverter(Class<?> cls) {
    for (Holder<R> converter : converters) {
      if (converter.cls.isAssignableFrom(cls)) {
        return (Converter<Object, R>) converter.converter;
      }
    }
    return null;
  }

  private R resolve(Class<?> cls, Object value) {
    Converter<Object, R> converter = getConverter(cls);
    if (converter != null) {
      R convert = converter.convert(value);
      return convert == null ? Na.of(this.cls) : convert;
    } else {
      return Na.of(this.cls);
    }
  }

  private static class Holder<R> {

    private final Class<?> cls;
    private Converter<?, R> converter;

    private Holder(Class<?> cls, Converter<?, R> converter) {
      this.cls = cls;
      this.converter = converter;
    }
  }
}

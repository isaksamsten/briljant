package org.briljantframework.io.reslover;

import org.briljantframework.vector.Na;

import java.util.ArrayList;
import java.util.List;

public class Resolver<R> {

  private final Class<R> cls;
  private final List<Holder<R>> converters = new ArrayList<>();

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
  public <T> void put(Class<T> cls, Converter<R, T> converter) {
    synchronized (converters) {
      for (Holder<R> holder : converters) {
        if (holder.cls.equals(cls)) {
          holder.converter = converter;
          return;
        }
      }
      converters.add(new Holder<>(cls, converter));
    }
  }

  /**
   * Resolves the value of {@code value} to an instance of {@code R}. If it fails, returns the value
   * denoting {@code NA} (for the type {@code R}) as returned by {@link org.briljantframework.vector.Na#of(Class)}.
   *
   * <p>Use {@link org.briljantframework.vector.Is#NA(java.lang.Object)} to check for {@code NA}
   * values.
   *
   * <p>The resolves values by sequentially scan the added converters and finds the first converter
   * where {@link Class#isAssignableFrom(Class)} returns true.
   *
   * <p>If the above becomes a bottleneck, it might be reconsidered (e.g., to only consider exact
   * class matches)
   *
   * @param value the value to resolve
   * @return the resolved value; or {@code Vectors.naValue(value.getClass())} otherwise
   */
  public R resolve(Object value) {
    return resolve(value.getClass(), value);
  }

  @SuppressWarnings("unchecked")
  private <T> Converter<R, T> get(Class<T> cls) {
    return (Converter<R, T>) getConverter(cls);
  }

  @SuppressWarnings("unchecked")
  private Converter<R, Object> getConverter(Class<?> cls) {
    for (Holder<R> converter : converters) {
      if (converter.cls.isAssignableFrom(cls)) {
        return (Converter<R, Object>) converter.converter;
      }
    }
    return null;
  }

  private R resolve(Class<?> cls, Object value) {
    Converter<R, Object> converter = getConverter(cls);
    if (converter != null) {
      R convert = converter.convert(value);
      return convert == null ? Na.of(this.cls) : convert;
    } else {
      return Na.of(this.cls);
    }
  }

  private static class Holder<R> {

    private final Class<?> cls;
    private Converter<R, ?> converter;

    private Holder(Class<?> cls, Converter<R, ?> converter) {
      this.cls = cls;
      this.converter = converter;
    }
  }
}

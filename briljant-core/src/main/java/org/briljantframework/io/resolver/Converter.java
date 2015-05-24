package org.briljantframework.io.resolver;

/**
 * @author Isak Karlsson
 */
public interface Converter<T, R> {

  /**
   * Converts from {@code t} (of type {@code T}) to a value of {@code R}.
   *
   * @param t the value to convert
   * @return the converted value
   */
  R convert(T t);
}

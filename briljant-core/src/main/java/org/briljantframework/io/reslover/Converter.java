package org.briljantframework.io.reslover;

/**
 * @author Isak Karlsson
 */
public interface Converter<R, T> {

  /**
   * Converts from {@code t} (of type {@code T}) to a value of {@code R}.
   *
   * @param t the value to convert
   * @return the converted value
   */
  R convert(T t);
}

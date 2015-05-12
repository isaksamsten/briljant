package org.briljantframework.io;

import org.briljantframework.complex.Complex;
import org.briljantframework.vector.Bit;

/**
 * Created by Isak Karlsson on 11/12/14.
 */
public interface DataEntry {

  /**
   * Reads the next entry and tries to resolve the value as {@code cls}. If this fails, {@code
   * next}
   * returns an appropriate {@code NA} value
   * (as defined in {@link org.briljantframework.vector.Na#of(Class)}).
   *
   * @param cls the class
   * @param <T> the type to return
   * @return a value of type {@code T}
   */
  <T> T next(Class<T> cls);

  /**
   * Reads the next string in this stream
   *
   * @return the next string
   */
  String nextString();

  /**
   * Reads the next int in this stream
   *
   * @return the next int
   */
  int nextInt();

  /**
   * Reads the next {@code double} in this stream
   *
   * @return the next {@code double}
   */
  double nextDouble();

  /**
   * Reads the next {@code Binary} in this stream.
   *
   * @return the next binary
   */
  Bit nextBinary();

  /**
   * Reads the next {@code Complex} in this stream.
   *
   * @return the next complex
   */
  Complex nextComplex();

  /**
   * Returns {@code true} if there are more values in the stream
   *
   * @return if has next
   */
  boolean hasNext();

  int size();
}

package org.briljantframework.io;

import org.briljantframework.complex.Complex;
import org.briljantframework.vector.Bit;

import java.io.IOException;

/**
 * Created by Isak Karlsson on 11/12/14.
 */
public interface DataEntry {

  <T> T next(Class<T> cls) throws IOException;

  /**
   * Reads the next string in this stream
   *
   * @return the next string
   */
  String nextString() throws IOException;

  /**
   * Reads the next int in this stream
   *
   * @return the next int
   */
  int nextInt() throws IOException;

  /**
   * Reads the next {@code double} in this stream
   *
   * @return the next {@code double}
   */
  double nextDouble() throws IOException;

  /**
   * Reads the next {@code Binary} in this stream.
   *
   * @return the next binary
   */
  Bit nextBinary() throws IOException;

  /**
   * Reads the next {@code Complex} in this stream.
   *
   * @return the next complex
   */
  Complex nextComplex() throws IOException;

  /**
   * Returns {@code true} if there are more values in the stream
   *
   * @return if has next
   */
  boolean hasNext() throws IOException;

  int size() throws IOException;
}

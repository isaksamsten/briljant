package org.briljantframework.io;

import java.io.IOException;

import org.briljantframework.complex.Complex;
import org.briljantframework.vector.Bit;

/**
 * Created by Isak Karlsson on 11/12/14.
 */
public interface DataEntry {

  /**
   * Reads the next string in this stream
   *
   * @return the next string
   * @throws java.io.IOException
   */
  String nextString() throws IOException;

  /**
   * Reads the next int in this stream
   *
   * @return the next int
   * @throws IOException
   * @throws java.lang.NumberFormatException
   */
  int nextInt() throws IOException;

  /**
   * Reads the next {@code double} in this stream
   *
   * @return the next {@code double}
   * @throws IOException
   * @throws java.lang.NumberFormatException
   */
  double nextDouble() throws IOException;

  /**
   * Reads the next {@code Binary} in this stream.
   *
   * @return the next binary
   * @throws IOException
   * @throws java.lang.NumberFormatException
   */
  Bit nextBinary() throws IOException;

  /**
   * Reads the next {@code Complex} in this stream.
   *
   * @return the next complex
   * @throws IOException
   * @throws NumberFormatException
   */
  Complex nextComplex() throws IOException;

  /**
   * Returns {@code true} if there are more values in the stream
   *
   * @return if has next
   * @throws java.io.IOException
   */
  boolean hasNext() throws IOException;

  int size() throws IOException;
}

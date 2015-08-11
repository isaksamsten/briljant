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

package org.briljantframework.io;

import org.apache.commons.math3.complex.Complex;
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

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

package org.briljantframework.data.reader;

import java.util.List;

/**
 * @author Isak Karlsson
 */
public interface DataEntry {

  /**
   * Reads the next entry and tries to resolve the value as {@code cls}. If this fails, {@code
   * next}
   * returns an appropriate {@code NA} value
   * (as defined by {@link org.briljantframework.data.Na#of(Class)}).
   *
   * @param cls the class
   * @param <T> the type to return
   * @return a value of type {@code T}
   */
  <T> T next(Class<T> cls);

  /**
   * Reads the next string in this entry
   *
   * @return the next string
   */
  String nextString();

  /**
   * Reads the next int in this entry
   *
   * @return the next int
   */
  int nextInt();

  /**
   * Reads the next {@code double} in this entry
   *
   * @return the next {@code double}
   */
  double nextDouble();

  /**
   * Returns {@code true} if there are more values in the entry
   *
   * @return if has next
   */
  boolean hasNext();

  /**
   * Skip the first n data entries
   *
   * @param no the number of entries to skip
   */
  void skip(int no);

  /**
   * Returns the size of the entry (if known).
   *
   * @return the size
   */
  int size();
}

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

package org.briljantframework.index;

import org.briljantframework.vector.Vector;

/**
 * @author Isak Karlsson
 */
public interface VectorLocationSetter {

  /**
   * Add NA at {@code index}. If {@code index > size()} the resulting vector should be padded
   * with NA:s between {@code size} and {@code index} and {@code index} set to NA.
   *
   * @param i the index
   */
  void setNA(int i);

  /**
   * Add {@code value} at {@code index}. Padding with NA:s between {@code atIndex} and {@code
   * size()} if {@code atIndex > size()}. <p> If value {@code value} cannot be added to this
   * vector type, a NA value is added instead.
   *
   * <p>How values are resolved depend on the implementation.
   *
   * <p>This must hold:
   *
   * <ul>
   * <li>{@code null} always result in {@code NA}</li>
   * <li>If {@link org.briljantframework.io.resolver.Resolvers#find(Class)} return a
   * non-null value the returned {@link org.briljantframework.io.resolver.Resolver#resolve(Class,
   * Object)} shall be used to produce the converted value. </li>
   * </ul>
   *
   * @param i     the index
   * @param value the value
   */
  void set(int i, Object value);

  void set(int index, double value);

  void set(int index, int value);

  /**
   * Add value at {@code fromIndex} in {@code from} to {@code atIndex}. Padding with NA:s between
   * {@code atIndex} and {@code size()} if {@code atIndex > size()}.
   *
   * @param t    the index
   * @param from the vector to take the value from
   * @param f    the index
   */
  void set(int t, Vector from, int f);

  void set(int atIndex, Vector from, Object fromKey);

  /**
   * Removes value at {@code index} and shifts element to the left.
   *
   * @param i the index
   * @return a modified builder with element at {@code index} removed
   */
  void remove(int i);

  /**
   * Swaps value at {@code a} with value at {@code b}
   *
   * @param a the first index
   * @param b the seconds index
   */
  void swap(int a, int b);
}

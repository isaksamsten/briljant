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

package org.briljantframework.vector;

/**
 * Provides information of a particular vectors type.
 */
public interface VectorType {

  /**
   * Creates a new builder able to build vectors of this type
   *
   * @return a new builder
   */
  Vector.Builder newBuilder();

  /**
   * Creates a new builder able to build vectors of this type
   *
   * @param size initial size (the vector is padded with NA)
   * @return a new builder
   */
  Vector.Builder newBuilder(int size);

  /**
   * Copy (and perhaps convert) {@code vector} to this type
   *
   * @param vector the vector to copy
   * @return a new vector
   */
  default Vector copy(Vector vector) {
    return newBuilder(vector.size()).addAll(vector).build();
  }

  /**
   * Get the underlying class used to represent values of this vector type
   *
   * @return the class
   */
  Class<?> getDataClass();

  /**
   * Returns true if this object is NA for this value type
   *
   * @param value the value
   * @return true if value is NA
   */
  boolean isNA(Object value);

  /**
   * Compare value at position {@code a} from {@code va} to value at position {@code b} from {@code
   * ba}.
   *
   * @param a  the index in va
   * @param va the vector
   * @param b  the index in ba
   * @param ba the vector
   * @return the comparison
   */
  int compare(int a, Vector va, int b, Vector ba);

  /**
   * Returns the scale of this type. If the scale is {@link Scale#NOMINAL}, the {@link
   * Vector#getAsString(int)} is expected to return a meaningful value. On the other hand, if the
   * value is {@link Scale#NUMERICAL} {@link Vector#getAsDouble(int)} is expected to return a
   * meaning ful value (or NA).
   *
   * @return the scale
   */
  Scale getScale();

  /**
   * Check if value at position {@code a} from {@code va} and value at position {@code b} from
   * {@code va} are equal.
   *
   * @param a  the index in va
   * @param va the vector
   * @param b  the index in ba
   * @param ba the vector
   * @return true if equal false otherwise
   */
  default boolean equals(int a, Vector va, int b, Vector ba) {
    return compare(a, va, b, ba) == 0;
  }
}

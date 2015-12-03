/**
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
package org.briljantframework.array;

/**
 * @author Isak Karlsson
 */
public interface Range extends IntArray {

  /**
   * Return a range from 0 (inclusive) to end (exclusive).
   * 
   * @param end the end
   * @return a new range
   */
  static Range of(int end) {
    return Arrays.range(end);
  }

  /**
   * Return a range from start (inclusive) to end (exclusive).
   * 
   * @param start the start
   * @param end the end
   * @return a new range
   */
  static Range of(int start, int end) {
    return Arrays.range(start, end);
  }

  /**
   * Return a range from start (inclusive) to end (exclusive) with the specified step size.
   * 
   * @param start the start
   * @param end the end
   * @param step the step size
   * @return a new range
   */
  static Range of(int start, int end, int step) {
    return Arrays.range(start, end, step);
  }

  /**
   * The start value of this range.
   *
   * @return the start value
   */
  int start();

  /**
   * The end value of this range.
   *
   * @return the end value
   */
  int end();

  /**
   * The step size of this range.
   *
   * @return the step size
   */
  int step();

  /**
   * Determines if the given value is included in the range.
   *
   * @param value the value
   * @return true if the given value is included
   */
  boolean contains(int value);
}

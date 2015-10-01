/*
 * The MIT License (MIT)
 * 
 * Copyright (c) 2015 Isak Karlsson
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.briljantframework.exceptions;

/**
 * Exception to throw when sizes does not match
 *
 * @author Isak Karlsson
 */
public class SizeMismatchException extends RuntimeException {

  /**
   * Produces message "Expected size {expected} but got {actual}.".
   *
   * @param expected expected size
   * @param actual actual size
   */
  public SizeMismatchException(int expected, int actual) {
    super(String.format("Expected size %d but got %d.", expected, actual));
  }

  /**
   * For example, {@code throw new SizeMismatchException("Size %d is not the same as %d", 10, 11)}.
   *
   * The {@code message} must contain 2 {@code %d} to format the {@code expected} and {@code actual}
   * . The first {@code %d} is {@code expected} and the second {@code actual}.
   *
   * @param message the message; a format string which must contain 2 {@code %d}.
   */
  public SizeMismatchException(String message) {
    super(message);
  }
}

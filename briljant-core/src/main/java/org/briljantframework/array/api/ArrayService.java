/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Isak Karlsson
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
package org.briljantframework.array.api;

/**
 * An array service is used by the service loader to create a {@link ArrayBackend}.
 *
 * <p/>
 *
 * Note that successive calls to {@link #getArrayBackend()} should return the same instance and that
 * implementors must provide a default implementation.
 */
public interface ArrayService {
  /**
   * Returns true if the backend is available.
   *
   * @return true if the backend is available
   */
  boolean isAvailable();

  /**
   * Returns the priority of the backend (the larger; the larger priority)
   *
   * @return the priority
   */
  int getPriority();

  /**
   * Returns the array backend
   * @return the array backend
   */
  ArrayBackend getArrayBackend();
}

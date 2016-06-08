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
 * An array backend provides {@link ArrayFactory factories}, {@link ArrayRoutines array routines}
 * and {@link LinearAlgebraRoutines linear algebra routines}.
 * 
 * <p/>
 * The arrays provided by a backend can be optimized in different regards, e.g., for CPU or GPU.
 *
 * <p/>
 * The default backend delegates many operations to <a href="http://www.netlib.org/">Netlib</a>.
 * 
 * @author Isak Karlsson
 * @see org.briljantframework.array.netlib.NetlibArrayBackend
 */
public interface ArrayBackend {

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
   * Get the array factory.
   * 
   * @return the array factory
   */
  ArrayFactory getArrayFactory();

  /**
   * Get the array routines (specialized for performing operations on the arrays returned by the
   * factory).
   * 
   * @return the routines
   */
  ArrayRoutines getArrayRoutines();

  /**
   * Get the linear algebra routines (specialized for the arrays returned by the factory).
   * 
   * @return the linear algebra routines
   */
  LinearAlgebraRoutines getLinearAlgebraRoutines();
}

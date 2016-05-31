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
package org.briljantframework.array;

/**
 * A super interface for interfaces representing arrays over values that are convertible to the
 * primive types (and {@link org.apache.commons.math3.complex.Complex Complex}.
 *
 * The specific semantics of the conversion from the numeric values of a particular
 * {@code NumberArray} implementation to a given primitive type is defined by the number array in
 * question.
 *
 * For the default implementations, the conversion follows the narrowing or widening of primitives
 * as defined by Java.
 *
 * Implementations may or may not return views.
 * 
 * @author Isak Karlsson
 */
public interface NumberArray {

  DoubleArray doubleArray();

  IntArray intArray();

  LongArray longArray();

  ComplexArray complexArray();
}

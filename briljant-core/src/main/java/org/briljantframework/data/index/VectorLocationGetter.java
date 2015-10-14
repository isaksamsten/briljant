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

package org.briljantframework.data.index;

import java.util.function.Supplier;

import org.briljantframework.data.vector.Vector;

/**
 * Created by isak on 18/08/15.
 */
public interface VectorLocationGetter {

  /**
   * Returns the value at {@code index} as an instance of {@code T}. If value at {@code index} is
   * not an instance of {@code cls}, returns an appropriate {@code NA} value. For references types
   * (apart from {@code Complex} and {@code Bit}) this means {@code null} and for {@code primitive}
   * types their respective {@code NA} value. Hence, checking for {@code null} does not always work.
   * Instead {@link org.briljantframework.data.Is#NA(Object)} should be used.
   *
   * <pre>
   * {@code
   * Vector v = new GenericVector(Date.class, Arrays.asList(new Date(), new Date());
   * Date date = v.get(Date.class, 0);
   * if(Is.NA(date)) { // or date == null
   *   // got a NA value
   * }
   * 
   * Vector v = ...; // for example an IntVector
   * int value = v.get(Integer.class, 32);
   * if(Is.NA(value)) { // or value == IntVector.NA (but not value == null)
   *   // got a NA value
   * }}
   * </pre>
   *
   * <p>
   * {@link java.lang.ClassCastException} should not be thrown, instead {@code NA} should be
   * returned
   *
   * @param cls the class
   * @param i the index
   * @param <T> the type
   * @return a value of type; returns {@code NA} if value is not an instance of {@code cls}
   * @throws java.lang.IndexOutOfBoundsException if {@code index < 0 || index > size()}
   */
  <T> T get(Class<T> cls, int i);

  default Object get(int i) {
    return get(Object.class, i);
  }

  /**
   * Get the value at the specified index. If the value is {@code NA}, the supplied default value is
   * returned.
   *
   * @param cls the class
   * @param index the index
   * @param defaultValue the default value supplier
   * @param <T> the type
   * @return the value at the specified index or the default value
   */
  <T> T get(Class<T> cls, int index, Supplier<T> defaultValue);

  /**
   * Returns value as {@code double} if applicable. Otherwise returns
   * {@link org.briljantframework.data.Na#DOUBLE}.
   *
   * @param i the index
   * @return a double
   * @throws java.lang.IndexOutOfBoundsException if {@code index < 0 || index > size()}
   */
  double getAsDouble(int i);

  /**
   * Returns value as {@code int} if applicable. Otherwise returns
   * {@link org.briljantframework.data.Na#INT}
   *
   * @param i the index
   * @return an int
   * @throws java.lang.IndexOutOfBoundsException if {@code index < 0 || index > size()}
   */
  int getAsInt(int i);

  /**
   * Returns true if value at {@code index} is NA
   *
   * @param i the index
   * @return true or false
   * @throws java.lang.IndexOutOfBoundsException if {@code index < 0 || index > size()}
   */
  boolean isNA(int i);

  /**
   * Returns {@code true} if value at {@code index} is considered to be true.
   * <p>
   * The following conventions apply:
   *
   * <ul>
   * <li>{@code 1.0+-0i == TRUE}</li>
   * <li>{@code 1.0 == TRUE}</li>
   * <li>{@code 1 == TRUE}</li>
   * <li>{@code &quot;true&quot; == TRUE}</li>
   * <li>{@code Binary.TRUE == TRUE}</li>
   * </ul>
   *
   * <p>
   * All other values are considered to be FALSE
   *
   * @param index the index
   * @return true or false
   * @throws java.lang.IndexOutOfBoundsException if {@code index < 0 || index > size()}
   */
  /**/
  boolean isTrue(int index);

  /**
   * Return the string representation of the value at {@code index}
   *
   * @param index the index
   * @return the string representation. Returns "NA" if value is missing.
   */
  String toString(int index);

  int indexOf(Object o);

  int lastIndexOf(Object o);



  Vector get(int... locations);

  /**
   * Follows the conventions from {@link Comparable#compareTo(Object)}.
   *
   * <p>
   * Returns value {@code < 0} if value at index {@code a} is less than {@code b}. value {@code > 0}
   * if value at index {@code b} is larger than {@code a} and 0 if they are equal.
   *
   * @param a the index a
   * @param b the index b
   * @return comparing int
   * @throws java.lang.IndexOutOfBoundsException if {@code index < 0 || index > size()}
   * @see Comparable#compareTo(Object)
   */
  int compare(int a, int b);

  /**
   * Compare value at {@code a} in {@code this} to value at {@code b} in {@code ba}. Equivalent to
   * {@code this.get(a).compareTo(other.get(b))}, but in most circumstances with greater
   * performance.
   *
   * @param a the index in {@code this}
   * @param other the other vector
   * @param b the index in {@code other}
   * @return the comparison
   * @throws java.lang.IndexOutOfBoundsException if {@code index < 0 || index > size()}
   * @see java.lang.Comparable#compareTo(Object)
   */
  int compare(int a, Vector other, int b);

  /**
   * Returns true if element at {@code a} in {@code this} equals element at {@code b} in
   * {@code other}.
   *
   * @param a the index in this
   * @param other the other vector
   * @param b the index in other
   * @return true if values are equal
   * @throws java.lang.IndexOutOfBoundsException if {@code index < 0 || index > size()}
   */
  boolean equals(int a, Vector other, int b);
}

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
package org.briljantframework.data.series;

/**
 * Provides information of a particular vectors type.
 * 
 * @author Isak Karlsson
 */
public abstract class Type {

  /**
   * Creates a new builder able to build vectors of this type
   *
   * @return a new builder
   */
  public abstract Series.Builder newBuilder();

  /**
   * Creates a new builder with the specified initial capacity
   *
   * @param capacity the initial capacity
   * @return a new builder with the specified initial capacity
   */
  public abstract Series.Builder newBuilderWithCapacity(int capacity);

  /**
   * Copy (and perhaps convert) {@code series} to this type
   *
   * @param series the series to copy
   * @return a new series
   */
  public Series copy(Series series) {
    return newBuilder(series.size()).setAll(series).build();
  }

  /**
   * Creates a new builder able to build vectors of this type
   *
   * @param size initial size (the series is padded with NA)
   * @return a new builder
   */
  public abstract Series.Builder newBuilder(int size);

  /**
   * Returns true if the specified type is assignable to the current type.
   * 
   * @param type the type
   * @return true if assignable
   */
  public boolean isAssignableTo(Type type) {
    return isAssignableTo(type.getDataClass());
  }

  /**
   * Returns true if the specified class is assignable to the current type.
   * 
   * @param cls the class
   * @return true if assignable
   */
  public boolean isAssignableTo(Class<?> cls) {
    return cls.isAssignableFrom(getDataClass());
  }

  /**
   * Get the underlying class used to represent values of this series type
   *
   * @return the class
   */
  public abstract Class<?> getDataClass();

}

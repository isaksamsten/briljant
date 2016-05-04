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
package org.briljantframework.data.index;

import java.util.Comparator;

import org.briljantframework.data.Is;

/**
 * Sort values of different types into individually sorted bins.
 *
 * @author Isak Karlsson
 */
public final class NaturalOrdering<T> implements Comparator<T> {

  private static final NaturalOrdering<Object> INSTANCE = new NaturalOrdering<>();

  private NaturalOrdering() {}

  @SuppressWarnings("unchecked")
  public static <T> NaturalOrdering<T> ascending() {
    return (NaturalOrdering<T>) INSTANCE;
  }

  @SuppressWarnings("unchecked")
  public static <T> NaturalOrdering<T> descending() {
    return (NaturalOrdering<T>) INSTANCE.reversed();
  }

  @Override
  @SuppressWarnings("unchecked")
  public int compare(Object o1, Object o2) {
    if (Is.NA(o1) && Is.NA(o2)) {
      return 0;
    } else if (Is.NA(o1)) {
      return -1;
    } else if (Is.NA(o2)) {
      return 1;
    } else {
      if (o1.getClass().equals(o2.getClass()) && o1 instanceof Comparable
          && o2 instanceof Comparable) {
        return ((Comparable) o1).compareTo(o2);
      } else {
        return o1.getClass().getName().compareTo(o2.getClass().getName());
      }
    }
  }
}

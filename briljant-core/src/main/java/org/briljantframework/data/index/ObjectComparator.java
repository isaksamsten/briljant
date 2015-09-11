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

package org.briljantframework.data.index;

import java.util.Comparator;

/**
 * Sort values of different types into individually sorted bins.
 *
 * @author Isak Karlsson
 */
public final class ObjectComparator implements Comparator<Object> {

  private static final ObjectComparator INSTANCE = new ObjectComparator();

  public static ObjectComparator getInstance() {
    return INSTANCE;
  }

  private ObjectComparator() {
  }

  @Override
  @SuppressWarnings("unchecked")
  public int compare(Object o1, Object o2) {
    if (o1 == null && o2 == null) {
      return 0;
    } else if (o1 == null) {
      return -1;
    } else if (o2 == null) {
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

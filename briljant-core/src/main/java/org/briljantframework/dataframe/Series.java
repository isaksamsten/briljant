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

package org.briljantframework.dataframe;

import org.briljantframework.complex.Complex;
import org.briljantframework.vector.Bit;
import org.briljantframework.vector.Vector;

/**
 * @author Isak Karlsson
 */
public interface Series extends Vector {

  Object name();

  Index getIndex();

  default <T> T get(Class<T> cls, Object key) {
    return get(cls, getIndex().index(key));
  }

  default int getAsInt(Object key) {
    return getAsInt(getIndex().index(key));
  }

  default double getAsDouble(Object key) {
    return getAsDouble(getIndex().index(key));
  }

  default Complex getAsComplex(Object key) {
    return getAsComplex(getIndex().index(key));
  }

  default Bit getAsBit(Object key) {
    return getAsBit(getIndex().index(key));
  }

  default String toString(Object key) {
    return toString(getIndex().index(key));
  }
}

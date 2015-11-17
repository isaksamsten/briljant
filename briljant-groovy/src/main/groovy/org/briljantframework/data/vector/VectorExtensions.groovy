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

package org.briljantframework.data.vector

import groovy.transform.CompileStatic
import org.briljantframework.data.index.VectorLocationGetter

/**
 * Created by isak on 04/06/15.
 */
@CompileStatic
class VectorExtensions {

  static <T> T asType(Vector self, Class<T> cls) {
    if (Collection.isAssignableFrom(cls)) {
      return cls.cast(self.toList(Object))
    }
    throw new ClassCastException()
  }

  static VectorLocationGetter getLoc(Vector self) {
    return self.loc()
  }


  static Object propertyMissing(Vector self, String name) {
    return self.get(Object, name)
  }

  static Object getAt(Vector self, Object k) {
    return self.get(k)
  }

  static Vector plus(Vector self, Number o) {
    return self.plus(o)
  }

  static Vector plus(Vector self, Vector o) {
    return self.plus(o)
  }

  static Vector minus(Vector self, Number o) {
    return self.minus(o)
  }

  static Vector minus(Vector self, Vector o) {
    return self.minus(o)
  }

  static Vector multiply(Vector self, Number o) {
    return self.times(o)
  }

  static Vector multiply(Vector self, Vector o) {
    return self.times(o)
  }

  static Vector div(Vector self, Number o) {
    return self.div(o)
  }

  static Vector div(Vector self, Vector o) {
    return self.div(o)
  }

}

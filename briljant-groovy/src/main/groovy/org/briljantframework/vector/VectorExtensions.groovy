package org.briljantframework.vector

import groovy.transform.CompileStatic
import groovy.transform.TypeChecked

/**
 * Created by isak on 04/06/15.
 */
@CompileStatic
class VectorExtensions {

  static <T> T asType(Vector self, Class<T> cls) {
    if (Collection.isAssignableFrom(cls)) {
      return cls.cast(self.asList(Object))
    }
    throw new ClassCastException()
  }

  static <T> T getAt(Vector self, int i) {
    return self.get(T, i)
  }

  static <T> T getAt(Vector self, Object k) {
    return self.get(T, k)
  }

  static Vector plus(Vector self, Number o) {
    return self.add(o)
  }

  static Vector plus(Vector self, Vector o) {
    return self.add(o)
  }

  static Vector minus(Vector self, Number o) {
    return self.sub(o)
  }

  static Vector minus(Vector self, Vector o) {
    return self.sub(o)
  }

  static Vector multiply(Vector self, Number o) {
    return self.mul(o)
  }

  static Vector multiply(Vector self, Vector o) {
    return self.mul(o)
  }

  static Vector div(Vector self, Number o) {
    return self.div(o)
  }

  static Vector div(Vector self, Vector o) {
    return self.div(o)
  }

}

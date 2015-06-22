package org.briljantframework.matrix

import groovy.transform.TypeChecked
import org.briljantframework.Bj

/**
 * Created by isak on 03/06/15.
 */
@TypeChecked
class MatrixExtensions {

  static <T extends Array<T>> T asType(Array<T> self, Class<T> cls) {
    switch (cls) {
      case DoubleArray: return cls.cast(self.asDoubleMatrix())
      case IntArray: return cls.cast(self.asIntMatrix())
      case LongArray: return cls.cast(self.asLongMatrix())
      case ComplexArray: return cls.cast(self.asLongMatrix())
      case BitArray: return cls.cast(self.asBitMatrix())
      default:
        throw new ClassCastException("Can't convert ${self.getClass()} to $cls")
    }
  }

  static double getAt(DoubleArray self, int r, int c) {
    return self.get(r, c)
  }

  static double getAt(DoubleArray self, int i) {
    return self.get(i)
  }

  static <T extends Array<T>> T getAt(T self, IntRange rows, IntRange cols) {
    return self.slice(Bj.range(rows.fromInt, rows.toInt),
                      Bj.range(cols.fromInt, cols.toInt))
  }

  static DoubleArray power(DoubleArray self, double power) {
    return self.map {Math.pow(it, power)}
  }

  static DoubleArray power(Number self, DoubleArray power) {
    return power.map {Math.pow(self.doubleValue(), it)}
  }

  static DoubleArray plus(DoubleArray self, double v) {
    return self.add(v)
  }

  static <T extends Array<T>> T plus(T self, T other) {
    return self.add(other)
  }

  static DoubleArray plus(Number self, DoubleArray other) {
    return other.add(self.doubleValue())
  }

  static DoubleArray minus(DoubleArray self, double v) {
    return self.sub(v)
  }

  static <T extends Array<T>> T minus(T self, T other) {
    return self.sub(other)
  }

  static DoubleArray minus(Number self, DoubleArray other) {
    return other.rsub(self.doubleValue())
  }

  static DoubleArray multiply(DoubleArray self, double v) {
    return self.mul(v)
  }

  static <T extends Array<T>> T multiply(T self, T other) {
    return self.mul(other)
  }

  static DoubleArray multiply(Number self, DoubleArray other) {
    return other.mul(self.doubleValue())
  }

  static DoubleArray div(Number self, DoubleArray other) {
    return other.rdiv(self.doubleValue())
  }

}

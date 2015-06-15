package org.briljantframework.matrix

import groovy.transform.TypeChecked
import org.briljantframework.Bj

/**
 * Created by isak on 03/06/15.
 */
@TypeChecked
class MatrixExtensions {

  static <T extends Matrix<T>> T asType(Matrix<T> self, Class<T> cls) {
    switch (cls) {
      case DoubleMatrix: return cls.cast(self.asDoubleMatrix())
      case IntMatrix: return cls.cast(self.asIntMatrix())
      case LongMatrix: return cls.cast(self.asLongMatrix())
      case ComplexMatrix: return cls.cast(self.asLongMatrix())
      case BitMatrix: return cls.cast(self.asBitMatrix())
      default:
        throw new ClassCastException("Can't convert ${self.getClass()} to $cls")
    }
  }

  static double getAt(DoubleMatrix self, int r, int c) {
    return self.get(r, c)
  }

  static double getAt(DoubleMatrix self, int i) {
    return self.get(i)
  }

  static <T extends Matrix<T>> T getAt(T self, IntRange range, Dim dim = null) {
    if (!dim) {
      return self.slice(Bj.range(range.fromInt, range.toInt))
    } else {
      return self.slice(Bj.range(range.fromInt, range.toInt), dim)
    }
  }

  static <T extends Matrix<T>> T getAt(T self, IntRange rows, IntRange cols) {
    return self.slice(Bj.range(rows.fromInt, rows.toInt),
                      Bj.range(cols.fromInt, cols.toInt))
  }

  static DoubleMatrix power(DoubleMatrix self, double power) {
    return self.map {Math.pow(it, power)}
  }

  static DoubleMatrix power(Number self, DoubleMatrix power) {
    return power.map {Math.pow(self.doubleValue(), it)}
  }

  static DoubleMatrix plus(DoubleMatrix self, double v) {
    return self.add(v)
  }

  static <T extends Matrix<T>> T plus(T self, T other) {
    return self.add(other)
  }

  static DoubleMatrix plus(Number self, DoubleMatrix other) {
    return other.add(self.doubleValue())
  }

  static DoubleMatrix minus(DoubleMatrix self, double v) {
    return self.sub(v)
  }

  static <T extends Matrix<T>> T minus(T self, T other) {
    return self.sub(other)
  }

  static DoubleMatrix minus(Number self, DoubleMatrix other) {
    return other.rsub(self.doubleValue())
  }

  static DoubleMatrix multiply(DoubleMatrix self, double v) {
    return self.mul(v)
  }

  static <T extends Matrix<T>> T multiply(T self, T other) {
    return self.mul(other)
  }

  static DoubleMatrix multiply(Number self, DoubleMatrix other) {
    return other.mul(self.doubleValue())
  }

  static DoubleMatrix div(Number self, DoubleMatrix other) {
    return other.rdiv(self.doubleValue())
  }

}

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

package org.briljantframework.array

import org.briljantframework.Bj

/**
 * Created by isak on 03/06/15.
 * TODO: fix me!
 */
//@CompileStatic
class ArrayExtensions {

  static <T extends BaseArray<T>> T asType(BaseArray<T> self, Class<T> cls) {
    switch (cls) {
      case DoubleArray: return cls.cast(self.asDouble())
      case IntArray: return cls.cast(self.asInt())
      case LongArray: return cls.cast(self.asLong())
      case ComplexArray: return cls.cast(self.asLong())
      case BooleanArray: return cls.cast(self.asBoolean())
      default:
        throw new ClassCastException("Can't convert ${self.getClass()} to $cls")
    }
  }

  static double getAt(DoubleArray self, int[] index) {
    return self.get(index)
  }

  static double getAt(DoubleArray self, int r, int c) {
    return self.get(r, c)
  }

  static double getAt(DoubleArray self, int i) {
    return self.get(i)
  }

  static <T extends BaseArray<T>> T get(T self, IntRange[] ranges) {
    return self.get(ranges.collect {IntRange it -> Bj.range(it.fromInt, it.toInt)} as Range[])
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

  static <T extends BaseArray<T>> T plus(T self, T other) {
    return self.add(other)
  }

  static DoubleArray plus(Number self, DoubleArray other) {
    return other.add(self.doubleValue())
  }

  static DoubleArray minus(DoubleArray self, double v) {
    return self.sub(v)
  }

  static <T extends BaseArray<T>> T minus(T self, T other) {
    return self.sub(other)
  }

  static DoubleArray minus(Number self, DoubleArray other) {
    return other.rsub(self.doubleValue())
  }

  static DoubleArray multiply(DoubleArray self, double v) {
    return self.mul(v)
  }

  static <T extends BaseArray<T>> T multiply(T self, T other) {
    return self.mul(other)
  }

  static DoubleArray multiply(Number self, DoubleArray other) {
    return other.mul(self.doubleValue())
  }

  static DoubleArray div(Number self, DoubleArray other) {
    return other.rdiv(self.doubleValue())
  }

}

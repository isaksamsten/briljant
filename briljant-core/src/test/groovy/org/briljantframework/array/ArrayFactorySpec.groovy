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

import org.apache.commons.math3.complex.Complex
import org.briljantframework.array.api.ArrayFactory
import spock.lang.Shared
import spock.lang.Specification

import java.util.function.DoubleSupplier
import java.util.function.IntSupplier
import java.util.function.LongSupplier
import java.util.function.Supplier

/**
 * Created by isak on 29/07/15.
 */
abstract class ArrayFactorySpec extends Specification {

  @Shared
  ArrayFactory bj;

  def "assigning a supplied element value"() {
    when:
    array.assign(element)

    then:
    array == result

    where:
    array << getElementArray(3, 0)
    element << getValue(3)
    result << getElementArray(3, 3)
  }

  def "assigning a supplied array"() {
    when:
    array.assign(other)

    then:
    array == other

    where:
    array << getElementArray(3, 0)
    other << getElementArray(3, 3)
  }

  def "assigning a supplied native array"() {
    when:
    array.assign(other)

    then:
    array == result

    where:
    array << getElementArray(3, 0)
    other << getNativeArray(3, 3)
    result << getElementArray(3, 3)
  }

  def "assigning using a supplier"() {
    when:
    array.assign(supplier)

    then:
    array == result

    where:
    array << getElementArray([3, 3], 0)
    result << getElementArray([3, 3], 3)
    supplier << getSupplier(3)
  }

  def "adding two arrays"() {
    when:
    def c = a + b

    then:
    c == result

    where:
    a << getElementArray([3, 3], 10)
    b << getElementArray([3, 3], 20)
    result << getElementArray([3, 3], 10 + 20)
  }

  def "adding an array and a scalar"() {
    when:
    def c = a + b

    then:
    c == result

    where:
    a << getElementArray([3, 3], 10)
    b << getValue(20)
    result << getElementArray([3, 3], 10 + 20)
  }

  def "multiplying two arrays"() {
    when:
    def c = a.times(b)

    then:
    c == result

    where:
    a << getElementArray([3, 3], 10)
    b << getElementArray([3, 3], 20)
    result << getElementArray([3, 3], 10 * 20)
  }

  def "multiplying an array and a scalar"() {
    when:
    def c = a.times(b)

    then:
    c == result

    where:
    a << getElementArray([3, 3], 10)
    b << getValue(20)
    result << getElementArray([3, 3], 10 * 20)
  }

  def "subtracting two arrays"() {
    when:
    def c = a.minus(b)

    then:
    c == result

    where:
    a << getElementArray([3, 3], 10)
    b << getElementArray([3, 3], 20)
    result << getElementArray([3, 3], 10 - 20)
  }

  def "subtracting an array and a scalar"() {
    when:
    def c = a - b

    then:
    c == result

    where:
    a << getElementArray([3, 3], 10)
    b << getValue(20)
    result << getElementArray([3, 3], 10 - 20)
  }

  def "subtraction an array from a scalar"() {
    when:
    def c = a.reverseMinus(b)

    then:
    c == result

    where:
    a << getElementArray([3, 3], 10)
    b << getValue(20)
    result << getElementArray([3, 3], 20 - 10)
  }

  def "dividing two arrays"() {
    when:
    def c = a.div(b)

    then:
    c == result

    where:
    a << getElementArray([3, 3], 10)
    b << getElementArray([3, 3], 20)
    result << getElementArray([3, 3], 10 / 20)
  }

  def "dividing an array and a scalar"() {
    when:
    def c = a.div(b)

    then:
    c == result

    where:
    a << getElementArray([3, 3], 10)
    b << getValue(20)
    result << getElementArray([3, 3], 10 / 20)
  }

  def "dividing an array from a scalar"() {
    when:
    def c = a.reverseDiv(b)

    then:
    c == result

    where:
    a << getElementArray([3, 3], 10)
    b << getValue(20)
    result << getElementArray([3, 3], 20 / 10)
  }

  def "create new range"() {
    expect:
    def range = bj.range(start, end, step)
    range.data() == result as int[]
    range.start() == start
    range.end() == end
    range.size() == (end - start) / step

    where:
    start << [0, 1, 2]
    end << [4, 5, 6]
    step << [1, 1, 2]
    result << [
        [0, 1, 2, 3],
        [1, 2, 3, 4],
        [2, 4]
    ]
  }

  def "reshape range"() {
    expect:
    def range = bj.range(start, end, step).reshape(rows, columns)
    range.rows() == rows
    range.columns() == columns
    range.size() == (end - start) / step

    where:
    start << [0, 1, 2]
    end << [4, 7, 10]
    step << [1, 1, 2]
    rows << [2, 3, 1]
    columns << [2, 2, 4]
    result << [
        [0, 1, 2, 3],
        [1, 2, 3, 4, 5, 6],
        [2, 4, 6, 8]
    ]
  }

  def "range equals"() {
    expect:
    a == a

    where:
    a << [bj.range(10), bj.range(3, 8), bj.range(0, 10, 3)]
  }

  def "range contains"() {
    expect:
    for (b in data) {
      a.contains(b)
    }

    where:
    a << [bj.range(10), bj.range(2, 1000), bj.range(3, 33, 6)]
    data << [
        [1, 2, 8, 9],
        [4, 99, 999, 322, 421],
        [3, 9, 21]
    ]
  }

  def "get element outside index should throw exception"() {
    setup:
    def r = bj.range(0, 10)

    when:
    r.get(10)

    then:
    thrown(IndexOutOfBoundsException)
  }

  def "get element"() {
    setup:
    def r = bj.range(2, 20, 2)

    when:
    r = r.reshape(3, 3)

    then:
    r.rows() == 3
    r.columns() == 3
    r.get(2, 1) == 12
    r.get(0, 2) == 14
    r.get(7) == 16
  }

  def "set(int,int) element should throw exception"() {
    setup:
    def r = bj.range(10)

    when:
    r.set(2, 321)

    then:
    thrown(UnsupportedOperationException)
  }

  def "slice should throw exception if range is outside bound"() {

  }


  def "set should throw exception"() {
    setup:
    def r = bj.range(10)

    when:
    r.set(2, 2, 321)

    then:
    thrown(IllegalArgumentException)

    when:
    r.set(0, 2)

    then:
    thrown(UnsupportedOperationException)
  }

  def "copy returns a mutable IntMatrix"() {
    setup:
    def r = bj.range(10)

    when:
    def c = r.copy().reshape(2, 5)
    c.set(0, 0, 20)
    c.set(2, 33)

    then:
    c.get(0, 0) == 20
    c.get(2) == 33
    c.get(0, 2) == 4
  }

  def "range should have a size and be flattable"() {
    expect:
    def range = bj.range(a, b, c)
    range.size() == d
    range.toList().last() == l

    where:
    a << [1, 2, 3, 4]
    b << [10, 20, 20, 8]
    c << [1, 4, 8, 2]
    d << [9, 5, 3, 2]
    l << [9, 18, 19, 6]
  }

  def "range should be exclusive"() {
    given:
    def r = bj.range(1, 10, 2)

    when:
    def last = r.toList().last()

    then:
    r.size() == 5
    last == 9
  }

  def "ranges should support negative values"() {
    given:
    def r = bj.range(0, -10, -1)

    expect:
    r.toList().last() == -9
  }


  def toList(value, n) {
    [value] * n
  }

  def range(n) {
    (1..n).collect()
  }

  def getSupplier(value) {
    [
        new IntSupplier() {

          @Override
          int getAsInt() {
            return value as int
          }
        },
        new DoubleSupplier() {

          @Override
          double getAsDouble() {
            return value as double;
          }
        },
        new LongSupplier() {

          @Override
          long getAsLong() {
            return value as long
          }
        },
        new Supplier<Complex>() {

          @Override
          Complex get() {
            return Complex.valueOf(value as double)
          }
        }
    ]
  }

  def getNativeArray(value, size) {
    def arr = [value] * size
    [
        arr as int[],
        arr as double[],
        arr as long[],
        (arr.collect { Complex.valueOf(it) }) as Complex[]

    ]
  }

  def getValue(value) {
    return [
        value as int,
        value as double,
        value as long,
        Complex.valueOf(value as double)
    ]
  }

  def getElementArray(shape, value) {
    if (!(shape instanceof List)) {
      shape = [shape]
    }
    shape = shape as int[]
    if (value instanceof List) {
      return [
          bj.array(value as int[]).reshape(shape),
          bj.array(value as double[]).reshape(shape),
          bj.array(value as long[]).reshape(shape),
          bj.array(value.collect { Complex.valueOf(it) } as Complex[]).reshape(shape)
      ]
    } else {
      def intArr = bj.intArray(shape)
      intArr.assign(value as int)
      def doubleArr = bj.doubleArray(shape)
      doubleArr.assign(value as double)
      def longArr = bj.longArray(shape)
      longArr.assign(value as long)
      def complexArr = bj.complexArray(shape)
      complexArr.assign(value as double)
      return [intArr, doubleArr, longArr, complexArr]
    }
  }

}

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
import org.briljantframework.array.base.BaseArrayBackend
import spock.lang.Specification

/**
 * Created by isak on 24/06/15.
 */
class BaseArraySpec extends Specification {

  static bj = new BaseArrayBackend().arrayFactory

  def "Constructing a new array"() {
    expect:
    size == a.size()

    where:
    a << getArrays(3 * 3 * 3)
    size << [27, 27, 27, 27, 27]
  }

  def "A 2d-array has both rows and columns"() {
    when:
    def i = a.reshape(3, 3);

    then:
    i.rows() == 3
    i.columns() == 3

    where:
    a << getArrays(3 * 3)
  }

  def "For non 2d-arrays rows() throws an exception"() {
    when:
    i.reshape(3, 3, 3).rows()

    then:
    thrown(IllegalStateException)

    where:
    i << getArrays(3 * 3 * 3)
  }

  def "For non 2d-arrays columns() throws an exception"() {
    when:
    i.reshape(3, 3, 3).columns()

    then:
    thrown(IllegalStateException)

    where:
    i << getArrays(3 * 3 * 3)
  }

  def "For 2d-arrays, size(0) == rows() and size(1) == columns()"() {
    when:
    def a = i.reshape(3, 3)

    then:
    a.size(0) == a.rows()
    a.size(1) == a.columns()

    where:
    i << getArrays(3 * 3)
  }

  def "For 2d-arrays, getRow(int) returns a row vector"() {
    given:
    def x = bj.range(3 * 3).reshape(3, 3)

    when:
    def a = x.getRow(row)
    def b = x.getRow(row).transpose()

    then:
    a.shape == [1, 3] as int[]
    b.shape == [3, 1] as int[]
    a == bj.array([value] as int[][])
    b == bj.array([value] as int[][]).transpose()

    where:
    row | value
    0   | [0, 3, 6]
    1   | [1, 4, 7]
    2   | [2, 5, 8]
  }

  def "For 2d-arrays, getColumn(int) returns a column vector"() {
    given: "a matrix"
    def x = bj.range(3 * 3).reshape(3, 3)

    when: "a column is extracted"
    def a = x.getColumn(column)
    def b = x.getColumn(column).transpose()

    then: "the column has the correct values and shape"
    a.shape == [3, 1] as int[]
    b.shape == [1, 3] as int[]
    a == bj.array([value] as int[][]).transpose()
    b == bj.array([value] as int[][])

    where:
    column | value
    0      | [0, 1, 2]
    1      | [3, 4, 5]
    2      | [6, 7, 8]
  }

  def "For 2d-arrays, getDiagonal() returns a vector of diagonal entries"() {
    expect:
    square.getDiagonal() == squareDiagonal
    moreRows.getDiagonal() == moreRowsDiagonal
    moreColumns.getDiagonal() == moreColumnsDiagonal

    where:
    square << getElementArray([3, 3], [0, 1, 2, 3, 4, 5, 6, 7, 8])
    squareDiagonal << getElementArray(3, [0, 4, 8])
    moreRows << getElementArray([4, 3], (0..11).toList())
    moreRowsDiagonal << getElementArray(3, [0, 5, 10])
    moreColumns << getElementArray([3, 4], (0..11).toList())
    moreColumnsDiagonal << getElementArray(3, [0, 4, 8])
  }

  def "ravel flattens arrays"() {
    expect:
    def y = x.reshape(2, 2, 2)
    y.ravel() == result
    y.transpose().ravel() == resultT

    where:
    x << getRangeArrays(2 * 2 * 2)
    result << getRangeArrays(2 * 2 * 2)
    resultT << getElementArray(2 * 2 * 2, [0, 4, 2, 6, 1, 5, 3, 7])
  }

  def "Using one-dimensional indexing, a transposed view is iterated in column major order"() {
    given:
    def x = bj.range(2 * 2 * 3).reshape(2, 2, 3)

    when:
    def y = x.transpose()

    then:
    y.shape == [3, 2, 2] as int[]
    y == bj.array(value as int[]).reshape(y.shape)

    where:
    value = [0, 4, 8, 2, 6, 10, 1, 5, 9, 3, 7, 11]
  }

  def "Selecting a range of elements from a 1d-array should return a view of the selected elements"() {
    given:
    def arr = bj.range(10)

    when:
    def i = arr.get(range)

    then:
    i.shape == selected.shape
    i.size() == range.size()
    i == selected

    where:
    range             | selected
    bj.range(0, 3)    | bj.array(0, 1, 2)
    bj.range(1, 5)    | bj.array(1, 2, 3, 4)
    bj.range(2, 7, 2) | bj.array(2, 4, 6)
  }

  def "Selecting a range of elements from a 2d-array should return a view of the selected elements"() {
    given:
    def arr = bj.range(3 * 5).reshape(3, 5)

    when:
    def i = arr.get(ranges)

    then:
    i.shape == selected.shape
    i.size() == ranges.collect {it.size()}.inject(1) {p, v -> p * v}
    i == selected
    i.transpose() == selected.transpose()

    where:
    ranges                           | selected
    [bj.range(2), bj.range(3)]       | bj.array(0, 1, 3, 4, 6, 7).reshape(2, 3)
    [bj.range(1, 3), bj.range(2)]    | bj.array(1, 2, 4, 5).reshape(2, 2)
    [bj.range(2), bj.range(0, 5, 2)] | bj.array(0, 1, 6, 7, 12, 13).reshape(2, 3)
  }

  def "Selecting a range of elements from a nd-array should return a view of the selected elements"() {

  }

  def "When selecting ranges, ranges[i] where i > ranges.size() and <= dims() should be interpreted as all"() {
    when:
    def i = arr.get(ranges)
    def b = arr.get(ranges + ([bj.range(2)] * (Math.abs(ranges.size() - arr.dims()))))

    then:
    i == b
    i.transpose() == b.transpose()

    where:
    arr                                         | ranges
    bj.range(2 * 2).reshape(2, 2)               | [bj.range(1)]
    bj.range(2 * 2 * 2).reshape(2, 2, 2)        | [bj.range(2), bj.range(1)]
    bj.range(2 * 2 * 2 * 2).reshape(2, 2, 2, 2) | [bj.range(2), bj.range(1)]
  }

  def "When selecting a set of indexes a correct copy should be returned"() {
    given:
    def i = bj.range(2 * 3 * 4).reshape(2, 3, 4)

    when:
    def x = i.select(indexes)

    then:
    x == result

    where:
    indexes                  | result
    [[1, 1]]                 | bj.array(1, 1, 3, 3,
                                        5, 5, 7, 7,
                                        9, 9, 11, 11,
                                        13, 13, 15, 15,
                                        17, 17, 19, 19,
                                        21, 21, 23, 23).reshape(2, 3, 4)
    [[1, 1], [0, 2]]         | bj.array(1, 5, 7, 11,
                                        13, 17, 19, 23).reshape(2, 4)
    [[1, 1], [1, 1], [1, 3]] | bj.array(9, 21)
  }

  def "Selecting a set of indexes should work for vectors"() {
    given:
    def i = bj.range(6)

    when:
    def x = i.select(indexes)

    then:
    x == result

    where:
    indexes              | result
    [[1, 1, 1, 2]]       | bj.array(1, 1, 1, 2)
    [[0, 1, 2, 4, 5, 5]] | bj.array(0, 1, 2, 4, 5, 5)
  }

  def "An exception is thrown if a selected index is out of bounds"() {
    when:
    i.select(indexes)

    then:
    thrown(IndexOutOfBoundsException)

    where:
    indexes              | i
    [[0, 0], [2, 2]]     | bj.range(2 * 2).reshape(2, 2)
    [[2, 2]]             | bj.range(2)
    [[0], [1], [2], [5]] | bj.range(2 * 2 * 3 * 2).reshape(2, 2, 3, 2)
    [[0, 1, 3]]          | bj.range(3 * 3 * 3).reshape(3, 3, 3)
  }

  def "A null pointer exception is thrown if a null key is selected"() {
    when:
    i.select(indexes)

    then:
    thrown(NullPointerException)

    where:
    indexes       | i
    [[null]]      | bj.range(3)
    [[0], [null]] | bj.range(2 * 2).reshape(2, 2)
  }


  def "Reshaping an array should change the dimensions"() {
    expect:
    array.dims() == 1
    array.reshape(shape).shape == shape
    array.reshape(shape).dims() == 4
    array.reshape(shape).size() == 2 * 3 * 4 * 5
    array.reshape(shape).reshape(Indexer.reverse(shape)).shape == Indexer.reverse(shape)

    where:
    array << getArrays(2 * 3 * 4 * 5);
    shape << getShapes([2, 3, 4, 5], 5);
  }

  def "Selecting a view of the current sub-array at index should return a view"() {
    expect:
    array.size(0) == 2
    array.select(0).shape == dims1
    array.select(1).shape == dims1

    def s = array.select(0)
    s.size(0) == 3
    s.select(0).shape == dims2
    s.select(1).shape == dims2
    s.select(2).shape == dims2

    array.select(0).assign(value)
    def f = array.select(0)
    for (int i = 0; i < f.size(); i++) {
      f.getAt(i) == value
    }

    where:
    array << getArrays(2, 3, 4);
    dims1 << getShapes([3, 4], 5)
    dims2 << getShapes([4], 5)
    value << [true, 1, 1.0, 1L, Complex.valueOf(1)]
  }

  def "Selecting the current sub-array returns the correct sub-array"() {
    expect:
    def a = range.reshape(2, 2, 2)
    a.select(0) == slice0
    a.select(0).select(0) == slice0.select(0)
    a.select(0).transpose() == slice0T
    a.select(0).transpose().select(0) == slice0T.select(0)

    a.select(1) == slice1
    a.select(1).select(1) == slice1.select(1)
    a.select(1).transpose() == slice1T
    a.select(1).transpose().select(1) == slice1T.select(1)

    where:
    range << getRangeArrays(8)
    slice0 << [
        bj.array([false, false, false, false] as boolean[]).reshape(2, 2),
        bj.array([0, 2, 4, 6] as int[]).reshape(2, 2),
        bj.array([0, 2, 4, 6] as double[]).reshape(2, 2),
        bj.array([0, 2, 4, 6] as long[]).reshape(2, 2),
        bj.array([Complex.valueOf(0), Complex.valueOf(2),
                  Complex.valueOf(4), Complex.valueOf(6)] as Complex[]).reshape(2, 2),
    ]
    slice0T << [
        bj.array([false, false, false, false] as boolean[]).reshape(2, 2),
        bj.array([0, 4, 2, 6] as int[]).reshape(2, 2),
        bj.array([0, 4, 2, 6] as double[]).reshape(2, 2),
        bj.array([0, 4, 2, 6] as long[]).reshape(2, 2),
        bj.array([Complex.valueOf(0), Complex.valueOf(4),
                  Complex.valueOf(2), Complex.valueOf(6)] as Complex[]).reshape(2, 2),
    ]
    slice1 << [
        bj.array([true, false, false, false] as boolean[]).reshape(2, 2),
        bj.array([1, 3, 5, 7] as int[]).reshape(2, 2),
        bj.array([1, 3, 5, 7] as double[]).reshape(2, 2),
        bj.array([1, 3, 5, 7] as long[]).reshape(2, 2),
        bj.array([Complex.valueOf(1), Complex.valueOf(3),
                  Complex.valueOf(5), Complex.valueOf(7)] as Complex[]).reshape(2, 2),
    ]
    slice1T << [
        bj.array([true, false, false, false] as boolean[]).reshape(2, 2),
        bj.array([1, 5, 3, 7] as int[]).reshape(2, 2),
        bj.array([1, 5, 3, 7] as double[]).reshape(2, 2),
        bj.array([1, 5, 3, 7] as long[]).reshape(2, 2),
        bj.array([Complex.valueOf(1), Complex.valueOf(5),
                  Complex.valueOf(3), Complex.valueOf(7)] as Complex[]).reshape(2, 2),
    ]
  }

  def "Array#getVector returns a vector along a given dimension"() {
    given: "an array"
    def r = bj.range(2 * 2 * 2).asDouble().reshape(2, 2, 2)

    expect: "#getVector returns the correct vector along the specified dimension"
    r.getVector(dim, idx) == vector

    where:
    dim | idx | vector
    0   | 0   | bj.array([0.0, 1.0] as double[])
    0   | 1   | bj.array([2.0, 3.0] as double[])
    0   | 2   | bj.array([4.0, 5.0] as double[])
    0   | 3   | bj.array([6.0, 7.0] as double[])

    1   | 0   | bj.array([0.0, 2] as double[])
    1   | 1   | bj.array([1.0, 3] as double[])
    1   | 2   | bj.array([4.0, 6] as double[])
    1   | 3   | bj.array([5.0, 7] as double[])

    2   | 0   | bj.array([0.0, 4] as double[])
    2   | 1   | bj.array([1.0, 5] as double[])
    2   | 2   | bj.array([2.0, 6] as double[])
    2   | 3   | bj.array([3.0, 7] as double[])
  }

  def "Array#getVector returns the correct vector when the array is a matrix"() {
    given: "a matrix"
    def m = bj.range(6).reshape(2, 3)

    expect:
    m.getVector(dim, idx) == vector

    where:
    dim | idx | vector
    1   | 0   | bj.array(0, 2, 4)
    1   | 1   | bj.array(1, 3, 5)

    0   | 0   | bj.array(0, 1)
    0   | 1   | bj.array(2, 3)
    0   | 2   | bj.array(4, 5)
  }

  def "Array#getVector returns the correct vector when the array is transposed"() {
    given: "an array"
    def r = bj.range(2 * 3 * 2).reshape(2, 3, 2)

    when: "the array is transposed"
    def t = r.transpose()

    then: "the correct vector is returned"
    t.getVector(dim, idx) == vector

    where:
    dim | idx | vector
    0   | 0   | bj.array(0, 6)
    0   | 1   | bj.array(1, 7)
    0   | 2   | bj.array(2, 8)
    0   | 3   | bj.array(3, 9)
    0   | 4   | bj.array(4, 10)
    0   | 5   | bj.array(5, 11)

    1   | 0   | bj.array(0, 2, 4)
    1   | 1   | bj.array(1, 3, 5)
    1   | 2   | bj.array(6, 8, 10)
    1   | 3   | bj.array(7, 9, 11)

    2   | 0   | bj.array(0, 1)
    2   | 1   | bj.array(2, 3)
    2   | 2   | bj.array(4, 5)
    2   | 3   | bj.array(6, 7)
    2   | 4   | bj.array(8, 9)
    2   | 5   | bj.array(10, 11)
  }

  def "Array#getVector returns the correct vector when a slice is selected"() {
    given: "an array"
    def r = bj.range(2 * 3 * 2).reshape(2, 3, 2)

  }

  def getShapes(j, int n) {
    return ([j as int[]] * n);
  }

  def getArrays(int[] shape) {
    return [
        bj.booleanArray(shape),
        bj.intArray(shape),
        bj.doubleArray(shape),
        bj.longArray(shape),
        bj.complexArray(shape)
    ]
  }

  def getRangeArrays(int length) {
    return [
        bj.range(length).asBit().copy(),
        bj.range(length).asInt().copy(),
        bj.range(length).asDouble().copy(),
        bj.range(length).asLong().copy(),
        bj.range(length).asComplex().copy(),
    ]
  }

  def getElementArray(shape, value) {
    if (!(shape instanceof List)) {
      shape = [shape]
    }
    shape = shape as int[]
    if (value instanceof List) {
      return [
          bj.array(value.collect {it == 1} as boolean[]).reshape(shape),
          bj.array(value as int[]).reshape(shape),
          bj.array(value as double[]).reshape(shape),
          bj.array(value as long[]).reshape(shape),
          bj.array(value.collect {Complex.valueOf(it)} as Complex[]).reshape(shape)
      ]
    } else {
      return [
          bj.booleanArray(shape).assign(value as int == 1),
          bj.intArray(shape).assign(value as int),
          bj.doubleArray(shape).assign(value as double),
          bj.longArray(shape).assign(value as long),
          bj.complexArray(shape).assign(value as double)
      ]
    }
  }
}

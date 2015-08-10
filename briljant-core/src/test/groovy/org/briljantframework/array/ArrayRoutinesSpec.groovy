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

import org.briljantframework.array.api.ArrayFactory
import org.briljantframework.array.api.ArrayRoutines
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created by isak on 31/07/15.
 */
abstract class ArrayRoutinesSpec extends Specification {

  @Shared
  ArrayFactory bj

  @Shared
  ArrayRoutines bjr


  def "dot product"() {
    expect:
    bjr.dot(a, b) == c

    where:
    a << [
        bj.array([1, 2, 3, 4] as double[]), // simple
        bj.range(10).asDouble().copy().get(bj.range(1, 10, 2)) // slice
    ]

    b << [
        bj.array([1, 2, 3, 4] as double[]),
        bj.range(10).asDouble().copy().get(bj.range(1, 10, 2)) // slice
    ]

    c << [
        30,
        165
    ]
  }

  def "arithmetic mean of an array"() {
    expect:
    bjr.mean(a) == b

    where:
    a << [
        bj.array([1, 2, 3, 4, 5, 6] as double[]),
        bj.array([-1, -2, -3, -4, -5, -6] as double[])
    ]
    b << [3.5, -3.5]
  }

  def "arithmetic mean of an array along the specified dimension"() {
    given:
    def a = bj.range(16).reshape(2, 4, 2).mapToDouble {it.doubleValue()}

    expect:
    bjr.mean(dim, a) == result

    where:
    dim | result
    0   | bj.array([0.5, 2.5, 4.5, 6.5, 8.5, 10.5, 12.5, 14.5] as double[]).reshape(4, 2)
    1   | bj.array([3, 4, 11, 12] as double[]).reshape(2, 2)
    2   | bj.array([4, 5, 6, 7, 8, 9, 10, 11] as double[]).reshape(2, 4)
  }

  @Unroll
  def "#min is min and #max is max of #array"() {
    expect:
    bjr.min(array) == min

    where:
    array                                        | min   | max
    bj.array("aaaa", "a", "ssda", "dsa")         | "a"   | "ssda"
    bj.array([0.1, 0.0001, -1, -23] as double[]) | -23.0 | 0.1
    bj.array([1, 2, 3, 4, 5, -2] as int[])       | -2    | 5
    bj.array([1, 2, 3, -22] as long[])           | -22   | 3
  }

  def "sorting returns a sorted array"() {
    expect:
    bjr.sort(array) == sorted

    where:
    array                        | sorted
    bj.array("a", "c", "b", "d") | bj.array("a", "b", "c", "d")
  }


}
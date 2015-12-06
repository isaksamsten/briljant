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

import org.apache.commons.math3.util.Precision
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
    bjr.inner(a, b) == c

    where:
    a << [
        bj.newDoubleVector([1, 2, 3, 4] as double[]), // simple
        bj.range(10).asDouble().copy().get([bj.range(1, 10, 2)]), // slice
        bj.linspace(0, 15, 16).reshape(4, 4).getColumn(0)
    ]

    b << [
        bj.newDoubleVector([1, 2, 3, 4] as double[]),
        bj.range(10).asDouble().copy().get([bj.range(1, 10, 2)]), // slice
        bj.linspace(0, 11, 12).reshape(3, 4).getRow(2)
    ]

    c << [
        30,
        165,
        54
    ]
  }

  def "absolute sum"() {
    expect:
    bjr.asum(x) == sum

    where:
    x << [
        bj.newDoubleVector([1, 2, 3, -5] as double[]),
        bj.newDoubleVector([1, 2, -3, -4, 10, 2] as double[]).get([bj.range(1, 6, 2)])
    ]

    sum << [
        11,
        8
    ]
  }

  def "axpy"() {
    when:
    bjr.axpy(alpha, x, y)

    then:
    y == result

    where:
    x << [
        bj.newDoubleVector([1, 2, 3, 4] as double[]),
        bj.newDoubleVector([0, 0, 0, 1, 0, 2, 0, 3] as double[]).get([bj.range(1, 8, 2)]),
        bj.linspace(0, 4, 10),
        bj.linspace(0, 9, 10).reshape(2, 5).getRow(0).transpose(),
        bj.linspace(0, 11, 12).reshape(3, 4).getRow(2)
    ]

    y << [
        bj.newDoubleArray(4),
        bj.newDoubleVector([1, 2, 3, 4] as double[]),
        bj.linspace(0, 4, 10),
        bj.linspace(0, 9, 10).reshape(2, 5).getRow(0),
        bj.linspace(0, 11, 12).reshape(4, 3).getColumn(0)
    ]

    alpha << [
        2.0,
        1.0,
        0.0,
        1.0,
        1
    ]

    result << [
        bj.newDoubleVector([2, 4, 6, 8] as double[]),
        bj.newDoubleVector([1, 3, 5, 7] as double[]),
        bj.linspace(0, 4, 10),
        bj.newDoubleVector([0, 4, 8, 12, 16] as double[]).reshape(1, 5),
        bj.newDoubleVector([2, 6, 10, 14] as double[]).reshape(4, 1)
    ]
  }

  def "norm2"() {
    expect:
    Precision.equals(bjr.norm2(a), Math.sqrt(sum), 10e-6)

    where:
    a << [
        bj.linspace(0, 3, 4),
        bj.range(10).asDouble(),
        bj.linspace(0, 9, 10).reshape(2, 5).getRow(1)
    ]

    sum << [
        14.0,
        285.0,
        165.0
    ]
  }

  def "gemm"() {
    when:
    bjr.gemm(transA, transB, alpha, a, b, beta, c)

    then:
    c == result

    where:
    [transA, transB, alpha, a, b, beta, c] << [
        [ArrayOperation.KEEP, ArrayOperation.KEEP, 1.0,
         bj.newDoubleMatrix([[1, 1],
                             [2, 2],
                             [3, 3]] as double[][]),
         bj.newDoubleMatrix([[1, 2, 3],
                             [1, 2, 3]] as double[][]),
         0.0,
         bj.newDoubleArray(3, 3)
        ],
        [ArrayOperation.TRANSPOSE, ArrayOperation.KEEP, 1.0,
         bj.newDoubleMatrix([[1, 1],
                             [2, 2],
                             [3, 3]] as double[][]),
         bj.newDoubleMatrix([[1, 1],
                             [2, 2],
                             [3, 3]] as double[][]),
         0.0,
         bj.newDoubleArray(2, 2)
        ],
        [ArrayOperation.KEEP, ArrayOperation.TRANSPOSE, 1.0,
         bj.newDoubleMatrix([[1, 1],
                             [2, 2],
                             [3, 3]] as double[][]),
         bj.newDoubleMatrix([[1, 1],
                             [2, 2],
                             [3, 3]] as double[][]),
         0.0,
         bj.newIntArray(3, 3).asDouble() // test a non-native view as output
        ],
        [ArrayOperation.KEEP, ArrayOperation.KEEP, 2.0,
         bj.newDoubleMatrix([[1, 1],
                             [2, 2],
                             [3, 3]] as double[][]),
         bj.newDoubleMatrix([[1, 2, 3],
                             [1, 2, 3]] as double[][]),
         3.0,
         bj.ones(3, 3)
        ]
    ]

    result << [
        bj.newDoubleMatrix([[2, 4, 6],
                            [4, 8, 12],
                            [6, 12, 18]] as double[][]),

        bj.newDoubleMatrix([[14, 14],
                            [14, 14]] as double[][]),

        bj.newDoubleMatrix([[2, 4, 6],
                            [4, 8, 12],
                            [6, 12, 18]] as double[][]),

        bj.newDoubleMatrix([[7, 11, 15],
                            [11, 19, 27],
                            [15, 27, 39]] as double[][])
    ]
  }

  def "arithmetic mean of an array"() {
    expect:
    bjr.mean(a) == b

    where:
    a << [
        bj.newDoubleVector([1, 2, 3, 4, 5, 6] as double[]),
        bj.newDoubleVector([-1, -2, -3, -4, -5, -6] as double[])
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
    0   | bj.newDoubleVector([0.5, 2.5, 4.5, 6.5, 8.5, 10.5, 12.5, 14.5] as double[]).reshape(4, 2)
    1   | bj.newDoubleVector([3, 4, 11, 12] as double[]).reshape(2, 2)
    2   | bj.newDoubleVector([4, 5, 6, 7, 8, 9, 10, 11] as double[]).reshape(2, 4)
  }

  @Unroll
  def "#min is min and #max is max of #array"() {
    expect:
    bjr.min(array) == min

    where:
    array                                                  | min   | max
    bj.newVector("aaaa", "a", "ssda", "dsa")               | "a"   | "ssda"
    bj.newDoubleVector([0.1, 0.0001, -1, -23] as double[]) | -23.0 | 0.1
    bj.newIntVector([1, 2, 3, 4, 5, -2] as int[])          | -2    | 5
    bj.newLongVector([1, 2, 3, -22] as long[])             | -22   | 3
  }

  def "sorting returns a sorted array"() {
    expect:
    bjr.sort(array) == sorted

    where:
    array                            | sorted
    bj.newVector("a", "c", "b", "d") | bj.newVector("a", "b", "c", "d")
  }


}

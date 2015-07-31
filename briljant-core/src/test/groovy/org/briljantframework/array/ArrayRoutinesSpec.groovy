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


}
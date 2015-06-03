package org.briljantframework.matrix.base

import spock.lang.Specification

/**
 * Created by isak on 30/05/15.
 */
class BaseRangeSpec extends Specification {

  static bj = new BaseMatrixFactory()

  def "create new range"() {
    expect:
    def range = bj.range(start, end, step)
    range.data() == result as int[]
    range.start() == start
    range.end() == end
    range.size() == (end - start) / step
    range.rows() == range.size()
    range.columns() == 1

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


  def "set(int,int,int) element should throw exception"() {
    setup:
    def r = bj.range(10)

    when:
    r.set(2, 2, 321)

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

  def "range should have a size"() {
    expect:
    def range = bj.range(a, b, c)
    range.size() == d
    range.flat().last() == l

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
    def last = r.flat().last()

    then:
    r.size() == 5
    last == 9
  }
}

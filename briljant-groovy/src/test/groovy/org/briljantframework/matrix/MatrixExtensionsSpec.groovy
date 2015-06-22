package org.briljantframework.matrix

import org.briljantframework.Bj
import spock.lang.Specification

import static org.briljantframework.Bj.matrix

/**
 * Created by isak on 03/06/15.
 */
class MatrixExtensionsSpec extends Specification {

  def "test a + b"() {
    expect:
    a + b == c

    where:
    a                                | b                             | c

    matrix([1, 2, 3, 4] as double[]) | Bj.ones(4)                    |
    matrix([2, 3, 4, 5] as double[])
    matrix([1, 2, 3, 4] as int[])    | matrix([1, 1, 1, 1] as int[]) | matrix([2, 3, 4, 5] as int[])
  }

  def "test getAt"() {
    given:
    def x = matrix([1, 2, 3, 4] as double[]).reshape(2, 2)

    when:
    def c = x[1, 1]

    then:
    c == 4
  }

  def "test slice using getAt"() {
    given:
    def x = matrix([1, 2, 3, 4, 5, 6, 7, 8, 9] as double[]).reshape(3, 3)

    when:
    def a = x[0..1, Dim.C]
    def b = x[0..1, Dim.R]
    def c = x[0..2, 0..2]
    def d = x[0..4]

    then:
    a == matrix([1, 2, 3] as double[])
    b == matrix([1, 4, 7] as double[]).transpose()
    c == matrix([1, 2, 4, 5] as double[]).reshape(2, 2)
    d == matrix([1, 2, 3, 4] as double[])
  }

  def "test plus"() {
    given:
    def x = matrix([1, 2, 3, 4] as double[])
    def y = matrix([1, 2, 3, 4] as double[])

    when:
    def z = x + y

    then:
    z == matrix([2, 4, 6, 8] as double[])
  }

  def "test double matrix operators"() {
    when:
    def x = matrix([1, 2, 3, 4] as double[])

    then:
    x + 10 == 10 + x
    x * 10 == 10 * x
  }

  def "test as operator"() {
    when:
    def i = matrix([1, 2, 3] as int[])
    def d = matrix([1, 2, 3] as double[])

    then:
    (i as DoubleArray) instanceof DoubleArray
    (d as IntArray) instanceof IntArray

    (i as DoubleArray).view == true
    (d as IntArray).view == true
  }

}

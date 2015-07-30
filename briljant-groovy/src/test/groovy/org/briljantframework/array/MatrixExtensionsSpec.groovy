package org.briljantframework.array

import org.briljantframework.Bj
import spock.lang.Specification

import static org.briljantframework.Bj.array

/**
 * Created by isak on 03/06/15.
 */
class MatrixExtensionsSpec extends Specification {

  def "test a + b"() {
    expect:
    a + b == c

    where:
    a                               | b                            | c

    array([1, 2, 3, 4] as double[]) | Bj.ones(4)                   |
    array([2, 3, 4, 5] as double[])
    array([1, 2, 3, 4] as int[])    | array([1, 1, 1, 1] as int[]) | array([2, 3, 4, 5] as int[])
  }

  def "test getAt"() {
    given:
    def x = array([1, 2, 3, 4] as double[]).reshape(2, 2)

    when:
    def c = x[1, 1]

    then:
    c == 4
  }

  def "test slice using getAt"() {
    given:
    def x = array(*[1.0, 2, 3, 4, 5, 6, 7, 8, 9]).reshape(3, 3)

    when:
//    def a = x[0..1]
//    def b = x[0..1]
    def c = x.get(0..3, 0..3)
//    def d = x[0..4]

    then:
//    a == array([1, 2, 3] as double[])
//    b == array([1, 4, 7] as double[]).transpose()
    c == array([1, 2, 4, 5] as double[]).reshape(2, 2)
//    d == array([1, 2, 3, 4] as double[])
  }

  def "test plus"() {
    given:
    def x = array([1, 2, 3, 4] as double[])
    def y = array([1, 2, 3, 4] as double[])

    when:
    def z = x + y

    then:
    z == array([2, 4, 6, 8] as double[])
  }

  def "test double array operators"() {
    when:
    def x = array([1, 2, 3, 4] as double[])

    then:
    x + 10 == 10 + x
    x * 10 == 10 * x
  }

  def "test as operator"() {
    when:
    def i = array([1, 2, 3] as int[])
    def d = array([1, 2, 3] as double[])

    then:
    (i as DoubleArray) instanceof DoubleArray
    (d as IntArray) instanceof IntArray

    (i as DoubleArray).view == true
    (d as IntArray).view == true
  }

}

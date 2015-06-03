package org.briljantframework.matrix

import org.briljantframework.Bj
import spock.lang.Specification

/**
 * Created by isak on 03/06/15.
 */
class MatrixExtensionsSpec extends Specification {

  def "test getAt"() {
    given:
    def x = Bj.matrix([1, 2, 3, 4] as double[]).reshape(2, 2)

    when:
    def c = x[1, 1]

    then:
    c == 4
  }

  def "test slice using getAt"() {
    given:
    def x = Bj.matrix([1, 2, 3, 4, 5, 6, 7, 8, 9] as double[]).reshape(3, 3)

    when:
    def a = x[0..1, Dim.C]
    def b = x[0..1, Dim.R]
    def c = x[0..2, 0..2]
    def d = x[0..4]

    then:
    a == Bj.matrix([1, 2, 3] as double[])
    b == Bj.matrix([1, 4, 7] as double[]).transpose()
    c == Bj.matrix([1, 2, 4, 5] as double[]).reshape(2, 2)
    d == Bj.matrix([1, 2, 3, 4] as double[])
  }

  def "test plus"() {
    given:
    def x = Bj.matrix([1, 2, 3, 4] as double[])
    def y = Bj.matrix([1, 2, 3, 4] as double[])

    when:
    def z = x + y

    then:
    z == Bj.matrix([2, 4, 6, 8] as double[])
  }

  def "test double matrix operators"() {
    when:
    def x = Bj.matrix([1, 2, 3, 4] as double[])

    then:
    x + 10 == 10 + x
    x * 10 == 10 * x
  }

  def "test as operator"() {
    when:
    def i = Bj.matrix([1, 2, 3] as int[])
    def d = Bj.matrix([1, 2, 3] as double[])

    then:
    (i as DoubleMatrix) instanceof DoubleMatrix
    (d as IntMatrix) instanceof IntMatrix

    (i as DoubleMatrix).view == true
    (d as IntMatrix).view == true
  }

}

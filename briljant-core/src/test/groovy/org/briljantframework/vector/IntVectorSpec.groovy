package org.briljantframework.vector

import org.briljantframework.complex.Complex
import spock.lang.Specification

/**
 * Created by isak on 31/05/15.
 */
class IntVectorSpec extends Specification {

  def "Vector.of(Integer...) returns an IntVector"() {
    when:
    def x = Vector.of(1, 2, 3, 4)

    then:
    x instanceof IntVector
  }

  def "IntVector arithmetic"() {
    setup:
    def a = Vector.of([1, 2, 3, 4])
    def b = Vector.of([1, 2, 3, 4])

    when:
    def c = a.add(b)
    def d = a.sub(b)
    def e = a.mul(b)
    def f = a.div(b)

    then:
    c == Vector.of([2, 4, 6, 8])
    d == Vector.of([0, 0, 0, 0])
    e == Vector.of([1, 4, 9, 16])
    f == Vector.of([1, 1, 1, 1])
  }

  def "IntVector builder adds NA values"() {
    setup:
    def ib = new IntVector.Builder()

    when:
    def a = ib.add(1).add(2).add(3).addNA().add(5).build()

    then:
    a.getAsInt(0) == 1
    a.get(Integer, 1) == 2
    a.getAsInt(2) == 3
    a.isNA(3)
    a.get(Number, 4) == 5
  }

  def "IntVector converts to correct values"() {
    when:
    def a = Vector.of([1, 2, 3, null, 5])

    then:
    a.getAsDouble(0) == 1.0
    a.getAsBit(0) == Bit.TRUE
    a.getAsBit(2) == Bit.NA
    a.get(Bit, 0) == a.getAsBit(0)
    a.get(Double, 1) == a.getAsDouble(1)
    a.get(Complex, 1) == a.getAsComplex(1)
    a.get(String, 3) == null
    a.get(Integer, 1) == a.getAsInt(1)
  }

}

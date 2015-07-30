package org.briljantframework.array

import org.briljantframework.array.api.ArrayFactory
import org.briljantframework.array.netlib.NetlibArrayBackend
import org.briljantframework.complex.Complex
import spock.lang.Shared
import spock.lang.Specification

import java.util.function.DoubleSupplier
import java.util.function.IntSupplier
import java.util.function.LongSupplier
import java.util.function.Supplier

/**
 * Created by isak on 29/07/15.
 */
class NumericArraySpec extends Specification {

  @Shared
  ArrayFactory bj;

  def setupSpec() {
    bj = new NetlibArrayBackend().arrayFactory
  }

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
    def c = a.add(b)

    then:
    c == result

    where:
    a << getElementArray([3, 3], 10)
    b << getElementArray([3, 3], 20)
    result << getElementArray([3, 3], 10 + 20)
  }

  def "adding an array and a scalar"() {
    when:
    def c = a.add(b)

    then:
    c == result

    where:
    a << getElementArray([3, 3], 10)
    b << getValue(20)
    result << getElementArray([3, 3], 10 + 20)
  }

  def "multiplying two arrays"() {
    when:
    def c = a.mul(b)

    then:
    c == result

    where:
    a << getElementArray([3, 3], 10)
    b << getElementArray([3, 3], 20)
    result << getElementArray([3, 3], 10 * 20)
  }

  def "multiplying an array and a scalar"() {
    when:
    def c = a.mul(b)

    then:
    c == result

    where:
    a << getElementArray([3, 3], 10)
    b << getValue(20)
    result << getElementArray([3, 3], 10 * 20)
  }

  def "subtracting two arrays"() {
    when:
    def c = a.sub(b)

    then:
    c == result

    where:
    a << getElementArray([3, 3], 10)
    b << getElementArray([3, 3], 20)
    result << getElementArray([3, 3], 10 - 20)
  }

  def "subtracting an array and a scalar"() {
    when:
    def c = a.sub(b)

    then:
    c == result

    where:
    a << getElementArray([3, 3], 10)
    b << getValue(20)
    result << getElementArray([3, 3], 10 - 20)
  }

  def "subtraction an array from a scalar"() {
    when:
    def c = a.rsub(b)

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
    def c = a.rdiv(b)

    then:
    c == result

    where:
    a << getElementArray([3, 3], 10)
    b << getValue(20)
    result << getElementArray([3, 3], 20 / 10)
  }

  def "for 2d-arrays, multiplying a matrix with a matrix"() {
    when:
    def c = a.mmul(b)

    then:
    c == result

    where:
    a << getElementArray([2, 3], [1, 2, 3, 4, 5, 6])
    b << getElementArray([3, 2], [1, 2, 3, 4, 5, 6])
    result << getElementArray([2, 2], [22, 28, 49, 64])
  }

  def "for 2d-arrays, multiplying a matrix with a matrix while transposing this"() {
    when:
    def c = a.mmul(transA, b, transB)

    then:
    c == result

    where:
    a << getElementArray([3, 2], range(6))
    b << getElementArray([3, 2], range(6))
    transA << toList(Op.TRANSPOSE, 4)
    transB << toList(Op.KEEP, 4)
    result << getElementArray([2, 2], [14, 32, 32, 77])
  }

  def "for 2d-arrays, multiplying a matrix with a matrix while transposing the argument"() {
    when:
    def c = a.mmul(transA, b, transB)

    then:
    c == result

    where:
    a << getElementArray([2, 3], range(6))
    b << getElementArray([2, 3], range(6))
    transA << toList(Op.KEEP, 4)
    transB << toList(Op.TRANSPOSE, 4)
    result << getElementArray([2, 2], [35, 44, 44, 56])
  }

  def "for 2d-arrays, multiplyign a matrix with a matrix while transposing both"() {
    when:
    def c = a.mmul(transA, b, transB)

    then:
    c == result

    where:
    a << getElementArray([3, 2], range(6))
    b << getElementArray([2, 3], range(6))
    transA << toList(Op.TRANSPOSE, 4)
    transB << toList(Op.TRANSPOSE, 4)
    result << getElementArray([2, 2], [22, 49, 28, 64])
  }

  def "for 2d-arrays, multiplying a matrix with a matrix using slices"() {
    when:
    a = a.get(bj.range(1, 3), bj.range(3))
    def c = a.mmul(b)
    then:
    c == result

    where:
    a << getElementArray([3, 4], [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12])
    b << getElementArray([3, 2], [1, 2, 3, 4, 5, 6])
    result << getElementArray([2, 2], [36, 42, 81, 96])
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
        (arr.collect {Complex.valueOf(it)}) as Complex[]

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
          bj.array(value.collect {Complex.valueOf(it)} as Complex[]).reshape(shape)
      ]
    } else {
      return [
          bj.intArray(shape).assign(value as int),
          bj.doubleArray(shape).assign(value as double),
          bj.longArray(shape).assign(value as long),
          bj.complexArray(shape).assign(value as double)
      ]
    }
  }

}
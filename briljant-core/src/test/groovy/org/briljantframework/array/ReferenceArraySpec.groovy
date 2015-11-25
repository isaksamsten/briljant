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
import org.apache.commons.math3.complex.ComplexFormat
import org.briljantframework.array.base.BaseArrayBackend
import spock.lang.Specification

import java.util.function.Supplier

/**
 * Created by isak on 29/07/15.
 */
class ReferenceArraySpec extends Specification {

  def bj = new BaseArrayBackend().arrayFactory

  def "assigning a single element"() {
    given:
    def x = bj.referenceArray(3)

    when:
    x.assign("hello world")

    then:
    x.forEach(0, {it == "hello world"})
  }

  def "assigning a supplied element value"() {
    given:
    def y = bj.referenceArray(3)

    when:
    y.assign(new Supplier() {

      @Override
      Object get() {
        return "hello world"
      }
    })

    then:
    y.forEach(0, {it == "hello world"})
  }

  def "assigning another array while transforming the value"() {
    given:
    def x = bj.referenceArray(3)
    def y = bj.referenceArray(3)

    when:
    x.assign(32)
    y.assign(x) {String.valueOf(it)}


    then:
    y.forEach(0) {it == "32"}
  }

  def "mapping an array to a double array"() {
    given:
    Array<String> x = bj.referenceArray(3)
    x.assign("320")

    when:
    def d = x.mapToDouble {
      Double.valueOf(it)
    }

    then:
    d.size() == x.size()
    d.forEach {
      it == 320.0
    }
  }

  def "mapping an array to a long array"() {
    given:
    Array<String> x = bj.referenceArray(3)
    x.assign("320")

    when:
    def d = x.mapToLong {
      Long.valueOf(it)
    }
    then:
    d.size() == x.size()
    d.forEach {it == 320L}
  }

  def "mapping an array to an int array"() {
    given: "a reference array of strings"
    Array<String> x = bj.referenceArray(3)
    x.assign("320")

    when: "the array is mapped to an int array"
    def y = x.mapToInt {Integer.valueOf(it)}

    then: "the array has the same size and int values"
    y.size() == x.size()
    y.forEach {it == 320}
  }

  def "mapping an array to a complex array"() {
    given:
    Array<String> x = bj.referenceArray(3)
    x.assign("320")

    when:
    def y = x.mapToComplex {
      ComplexFormat.getInstance().parse(it)
    }

    then:
    y.size() == x.size()
    y.forEach {it == Complex.valueOf(320)}
  }

  def "mapping an array of value of type T to an array of type U"() {
    given:
    Array<String> x = bj.referenceArray(3)
    x.assign("hello")

    when:
    Array<char[]> y = x.map {it.toCharArray()}

    then:
    x.size() == y.size()
    y.forEach(0) {it == ['h', 'e', 'l', 'l', 'o'] as char[]}
  }
//
//  def "test asDouble"() {
//    given:
//
//    when:
//    // TODO implement stimulus
//    then:
//    // TODO implement assertions
//  }
//
//  def "test asDouble1"() {
//    given:
//
//    when:
//    // TODO implement stimulus
//    then:
//    // TODO implement assertions
//  }
//
//  def "test asInt"() {
//    given:
//
//    when:
//    // TODO implement stimulus
//    then:
//    // TODO implement assertions
//  }
//
//  def "test asInt1"() {
//    given:
//
//    when:
//    // TODO implement stimulus
//    then:
//    // TODO implement assertions
//  }
//
//  def "test asLong"() {
//    given:
//
//    when:
//    // TODO implement stimulus
//    then:
//    // TODO implement assertions
//  }
//
//  def "test asLong1"() {
//    given:
//
//    when:
//    // TODO implement stimulus
//    then:
//    // TODO implement assertions
//  }
//
//  def "test asBit"() {
//    given:
//
//    when:
//    // TODO implement stimulus
//    then:
//    // TODO implement assertions
//  }
//
//  def "test asBit1"() {
//    given:
//
//    when:
//    // TODO implement stimulus
//    then:
//    // TODO implement assertions
//  }
//
//  def "test asComplex"() {
//    given:
//
//    when:
//    // TODO implement stimulus
//    then:
//    // TODO implement assertions
//  }
//
//  def "test asComplex1"() {
//    given:
//
//    when:
//    // TODO implement stimulus
//    then:
//    // TODO implement assertions
//  }
//
//  def "test filter"() {
//    given:
//
//    when:
//    // TODO implement stimulus
//    then:
//    // TODO implement assertions
//  }
//
//  def "test satisfies"() {
//    given:
//
//    when:
//    // TODO implement stimulus
//    then:
//    // TODO implement assertions
//  }
//
//  def "test satisfies1"() {
//    given:
//
//    when:
//    // TODO implement stimulus
//    then:
//    // TODO implement assertions
//  }
//
//  def "test get"() {
//    given:
//
//    when:
//    // TODO implement stimulus
//    then:
//    // TODO implement assertions
//  }
//
//  def "test set"() {
//    given:
//
//    when:
//    // TODO implement stimulus
//    then:
//    // TODO implement assertions
//  }
//
//  def "test get1"() {
//    given:
//
//    when:
//    // TODO implement stimulus
//    then:
//    // TODO implement assertions
//  }
//
//  def "test set1"() {
//    given:
//
//    when:
//    // TODO implement stimulus
//    then:
//    // TODO implement assertions
//  }
//
//  def "test get2"() {
//    given:
//
//    when:
//    // TODO implement stimulus
//    then:
//    // TODO implement assertions
//  }
//
//  def "test set2"() {
//    given:
//
//    when:
//    // TODO implement stimulus
//    then:
//    // TODO implement assertions
//  }
//
//  def "test stream"() {
//    given:
//
//    when:
//    // TODO implement stimulus
//    then:
//    // TODO implement assertions
//  }
//
//  def "test list"() {
//    given:
//
//    when:
//    // TODO implement stimulus
//    then:
//    // TODO implement assertions
//  }
//
//  def "test data"() {
//    given:
//
//    when:
//    // TODO implement stimulus
//    then:
//    // TODO implement assertions
//  }
}

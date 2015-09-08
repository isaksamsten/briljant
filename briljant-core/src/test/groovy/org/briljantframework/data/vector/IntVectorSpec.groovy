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

package org.briljantframework.data.vector

import org.briljantframework.data.Logical
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
    a.loc().getAsInt(0) == 1
    a.loc().get(Integer, 1) == 2
    a.loc().getAsInt(2) == 3
    a.loc().isNA(3)
    a.loc().get(Number, 4) == 5
  }

  def "IntVector converts to correct values"() {
    when:
    def a = Vector.of([1, 2, 3, null, 5])

    then:
    a.loc().getAsDouble(0) == 1.0
    a.get(Logical, 0) == Logical.TRUE
    a.get(Logical, 2) == Logical.FALSE
    a.loc().get(Double, 1) == a.loc().getAsDouble(1)
    a.loc().get(String, 3) == null
    a.loc().get(Integer, 1) == a.loc().getAsInt(1)
  }

}

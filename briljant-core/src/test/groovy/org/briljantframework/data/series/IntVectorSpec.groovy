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

package org.briljantframework.data.series

import org.briljantframework.data.Logical
import spock.lang.Specification

/**
 * Created by isak columnKeys 31/05/15.
 */
class IntVectorSpec extends Specification {

  def "Vector.of(Integer...) returns an IntVector"() {
    when:
    def x = IntSeries.of(1, 2, 3, 4)

    then:
    x instanceof IntSeries
  }


  def "IntVector builder adds NA values"() {
    setup:
    def ib = new IntSeries.Builder()

    when:
    def a = ib.addInt(1).addInt(2).addInt(3).addNA().addInt(5).build()

    then:
    a.values().getInt(0) == 1
    a.values().get(Integer, 1) == 2
    a.values().getInt(2) == 3
    a.values().isNA(3)
    a.values().get(Number, 4) == 5
  }

  def "IntVector converts to correct values"() {
    when:
    def a = Series.copyOf([1, 2, 3, null, 5])

    then:
    a.values().getDouble(0) == 1.0
    a.get(Logical, 0) == Logical.TRUE
    a.get(Logical, 2) == Logical.FALSE
    a.values().get(Double, 1) == a.values().getDouble(1)
    a.values().get(String, 3) == null
    a.values().get(Integer, 1) == a.values().getInt(1)
  }

}

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

package org.briljantframework.data.dataframe

import org.apache.commons.math3.complex.Complex
import org.briljantframework.data.BoundType
import org.briljantframework.data.Na
import spock.lang.Specification

/**
 * Created by isak on 07/06/15.
 */
class ObjectIndexSpec extends Specification {

  def "create HashIndex from list"() {
    when:
    def i = ObjectIndex.create(["a", "b", "c"])

    then:
    i.keySet() as ArrayList == ["a", "b", "c"]
    i.getLocation("a") == 0
    i.getLocation("b") == 1
    i.locations(["a", "b"] as Object[]) as ArrayList == [0, 1]
    i.locations() as ArrayList == [0, 1, 2]
    i.getKey(2) == "c"
    i.size() == 3
    i.newCopyBuilder().build().getLocation("a") == 0
  }

  def "handles NA-index"() {
    expect:
    ObjectIndex.create([na]).getLocation(na) == 0

    where:
    na << [Na.of(Double), Na.of(Integer), Na.of(Object), Na.of(Complex)]
  }


  def "select range of locations from sorted index"() {
    given:
    def b = new ObjectIndex.Builder()
    b.add("a")
    b.add("b")
    b.add("c")
    b.add("d")
    b.sort()
    def i = b.build()

    when:
    Set<Object> range = i.selectRange("a", BoundType.INCLUSIVE, "d", BoundType.EXCLUSIVE)

    then:
    range == ["a", "b", "c"] as HashSet

  }

  def "remove element from hash index"() {
    given:
    def b = new ObjectIndex.Builder()

    when:
    b.add(0)
    b.add(1)
    b.add(2)
    b.add(3)

    and:
    b.remove(1)

    and:
    def i = b.build()

    then:
    i.getLocation(0) == 0
    i.getLocation(2) == 1
    i.getLocation(3) == 2
  }

}

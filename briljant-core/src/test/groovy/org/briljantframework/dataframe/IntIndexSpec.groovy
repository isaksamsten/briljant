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

package org.briljantframework.dataframe

import spock.lang.Specification

/**
 * Created by isak on 07/06/15.
 */
class IntIndexSpec extends Specification {

  def "index and key are equal"() {
    when:
    def i = new IntIndex(10)

    then:
    i.index(3) == 3
    i.get(2) == 2
    new ArrayList<>(i.indices()) == [0, 1, 2, 3, 4, 5, 6, 7, 8, 9]
    new ArrayList<>(i.indices([1, 2, 3] as Object[])) == [1, 2, 3]
    i.size() == 10

  }

  def "index throws exception if key is outside of size"() {
    given:
    def i = new IntIndex(10)

    when:
    i.index(11)

    then:
    thrown(NoSuchElementException)
  }

  def "can't contain negative keys"() {
    given:
    def i = new IntIndex(10)

    when:
    i.index(-1)

    then:
    thrown(NoSuchElementException)
  }

  def "builder build IntIndex when using monotonically increasing keys with the same index"() {
    given:
    def b = new IntIndex(10).newBuilder()

    when:
    b.add(0)
    b.set(1, 1)
    b.add(2)
    b.set(3, 3)
    b.set(4, 4)
    b.set(5, 5)

    then:
    def build = b.build()
    build instanceof IntIndex
    build as ArrayList == [0, 1, 2, 3, 4, 5]
  }

  def "copy builder"() {
    given:
    def b = new IntIndex(10).newCopyBuilder()

    when:
    b.add("key")

    then:
    def index = b.build()
    index instanceof HashIndex
    index as ArrayList == [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, "key"]
  }


  def "builder falls back to HashIndex when non monotonically increasing keys are used"() {
    given:
    def b = new IntIndex(10).newBuilder()

    when:
    b.add(0)
    b.add(1)
    b.add(2)
    b.set(3, 3)
    b.add(10)

    then:
    def build = b.build()
    build instanceof HashIndex
    build as ArrayList == [0, 1, 2, 3, 10]
  }

  def "builder falls back to HashIndex when non-int keys are used"() {
    given:
    def b = new IntIndex(10).newBuilder()

    when:
    b.add(0)
    b.add(1)
    b.add("hello")
    b.add("world")

    then:
    def i = b.build()
    i instanceof HashIndex
    i as ArrayList == [0, 1, "hello", "world"]
  }

}

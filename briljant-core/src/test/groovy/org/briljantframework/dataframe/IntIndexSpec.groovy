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

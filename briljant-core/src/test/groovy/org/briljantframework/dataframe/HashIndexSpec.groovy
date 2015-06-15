package org.briljantframework.dataframe

import spock.lang.Specification

/**
 * Created by isak on 07/06/15.
 */
class HashIndexSpec extends Specification {

  def "create HashIndex from list"() {
    when:
    def i = HashIndex.from(["a", "b", "c"])

    then:
    i as ArrayList == ["a", "b", "c"]
    i.index("a") == 0
    i.index("b") == 1
    i.indices(["a", "b"] as Object[]) as ArrayList == [0, 1]
    i.indices() as ArrayList == [0, 1, 2]
    i.get(2) == "c"
    i.size() == 3
  }
}

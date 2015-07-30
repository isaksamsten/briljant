package org.briljantframework.array

import spock.lang.Specification

/**
 * Created by isak on 15/06/15.
 */
class IndexerSpec extends Specification {

  def "reverse int array"() {
    when:
    def a = [1, 2, 3] as int[]

    then:
    Indexer.reverse(a) == [3, 2, 1] as int[]
  }
}

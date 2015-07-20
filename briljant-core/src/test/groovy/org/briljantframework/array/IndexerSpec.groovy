package org.briljantframework.array

import spock.lang.Specification

/**
 * Created by isak on 15/06/15.
 */
class IndexerSpec extends Specification {

  def "column-major order index"() {
    when:
    def a = Indexer.columnMajor(0, 1, 2, 10, 10)
    def b = Indexer.columnMajorStride([1, 2] as int[], getOffset(),
                                      Indexer.computeStride(1, [10, 10] as int[]))

    then:
    a == b
  }

  def "reverse int array"(){
    when:
    def a = [1,2,3] as int[]

    then:
    Indexer.reverse(a) == [3,2,1] as int[]
  }
}

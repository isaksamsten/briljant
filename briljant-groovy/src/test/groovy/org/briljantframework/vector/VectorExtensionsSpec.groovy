package org.briljantframework.vector

import spock.lang.Specification

/**
 * Created by isak on 05/06/15.
 */
class VectorExtensionsSpec extends Specification {

  def "Get at return a value"() {
    when:
    def v = Vector.of([1, 2, 3, 4, 5, 6, 7] as int[])

    then:
    int i = v[0]
    i == 1
    v[1] == 2
  }
}

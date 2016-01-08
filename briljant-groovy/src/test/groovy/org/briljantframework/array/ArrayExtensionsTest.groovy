package org.briljantframework.array

import spock.lang.Specification

/**
 * @author Isak Karlsson <isak-kar@dsv.su.se>
 */
class ArrayExtensionsTest extends Specification {

  def "testit"() {
    given:
    def a = Arrays.linspace(0, 1, 20).reshape(4, 5)

    expect:
    println a
    println a[0, 1]

  }
}

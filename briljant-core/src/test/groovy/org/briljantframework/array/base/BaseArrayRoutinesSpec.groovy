package org.briljantframework.array.base

import org.briljantframework.array.ArrayRoutinesSpec
import spock.lang.Specification

/**
 * Created by isak on 01/06/15.
 */
class BaseArrayRoutinesSpec extends ArrayRoutinesSpec {

  void setupSpec() {
    def b = new BaseArrayBackend()
    bj = b.arrayFactory
    bjr = b.arrayRoutines
  }
}

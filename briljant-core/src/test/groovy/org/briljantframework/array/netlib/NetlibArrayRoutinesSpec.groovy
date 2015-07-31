package org.briljantframework.array.netlib

import org.briljantframework.array.ArrayRoutinesSpec
import spock.lang.Specification

/**
 * Created by isak on 31/07/15.
 */
class NetlibArrayRoutinesSpec extends ArrayRoutinesSpec {

  def setupSpec(){
    def b = new NetlibArrayBackend()
    bj = b.arrayFactory
    bjr = b.arrayRoutines
  }
}
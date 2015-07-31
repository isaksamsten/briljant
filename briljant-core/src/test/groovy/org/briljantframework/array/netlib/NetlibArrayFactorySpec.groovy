package org.briljantframework.array.netlib

import org.briljantframework.array.ArrayFactorySpec

/**
 * Created by isak on 31/07/15.
 */
class NetlibArrayFactorySpec extends ArrayFactorySpec {

  def setupSpec(){
    bj = new NetlibArrayBackend().arrayFactory
  }

}

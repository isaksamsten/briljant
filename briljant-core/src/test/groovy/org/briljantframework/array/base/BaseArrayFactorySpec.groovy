package org.briljantframework.array.base

import org.briljantframework.array.ArrayFactorySpec

/**
 * Created by isak on 31/07/15.
 */
class BaseArrayFactorySpec extends ArrayFactorySpec {

  def setupSpec() {
    bj = new BaseArrayBackend().arrayFactory
  }
}
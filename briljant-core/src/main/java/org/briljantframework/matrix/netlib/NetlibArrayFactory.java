package org.briljantframework.matrix.netlib;

import org.briljantframework.matrix.DoubleArray;
import org.briljantframework.matrix.base.BaseArrayFactory;

/**
 * @author Isak Karlsson
 */
class NetlibArrayFactory extends BaseArrayFactory {

  public NetlibArrayFactory() {
  }

  @Override
  public DoubleArray array(double[] data) {
    return new NetlibDoubleArray(this, data);
  }

  @Override
  public DoubleArray doubleArray(int... shape) {
    return new NetlibDoubleArray(this, shape);
  }
}

package org.briljantframework.array.netlib;

import org.briljantframework.array.DoubleArray;
import org.briljantframework.array.base.BaseArrayFactory;

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

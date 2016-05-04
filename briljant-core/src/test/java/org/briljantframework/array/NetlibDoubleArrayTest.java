package org.briljantframework.array;

import org.briljantframework.array.api.ArrayFactory;
import org.briljantframework.array.netlib.NetlibArrayBackend;

/**
 * Created by isak on 5/4/16.
 */
public class NetlibDoubleArrayTest extends DoubleArrayTest {
  @Override
  protected ArrayFactory getArrayFactory() {
    return new NetlibArrayBackend().getArrayFactory();
  }
}

package org.briljantframework.array.base;

import org.briljantframework.array.api.ArrayFactory;
import org.briljantframework.array.api.ArrayFactoryTest;

/**
 * @author Isak Karlsson <isak-kar@dsv.su.se>
 */
public class BaseArrayFactoryTest extends ArrayFactoryTest {

  private final ArrayFactory factory = new BaseArrayBackend().getArrayFactory();

  @Override
  public ArrayFactory getFactory() {
    return factory;
  }
}

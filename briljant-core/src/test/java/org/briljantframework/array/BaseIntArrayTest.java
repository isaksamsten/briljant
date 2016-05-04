package org.briljantframework.array;

import org.briljantframework.array.api.ArrayBackend;
import org.briljantframework.array.api.ArrayFactory;
import org.briljantframework.array.base.BaseArrayBackend;

/**
 * Created by isak on 5/4/16.
 */
public class BaseIntArrayTest extends IntArrayTest {

  @Override protected ArrayFactory getArrayFactory() {
    return new BaseArrayBackend().getArrayFactory();
  }
}

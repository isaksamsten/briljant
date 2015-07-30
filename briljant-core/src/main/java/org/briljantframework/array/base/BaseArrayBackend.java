package org.briljantframework.array.base;

import org.briljantframework.linalg.api.LinearAlgebraRoutines;
import org.briljantframework.array.api.ArrayBackend;
import org.briljantframework.array.api.ArrayFactory;
import org.briljantframework.array.api.ArrayRoutines;

/**
 * @author Isak Karlsson
 */
public class BaseArrayBackend implements ArrayBackend {

  private ArrayFactory arrayFactory;
  private ArrayRoutines arrayRoutines;
  private LinearAlgebraRoutines linearAlgebraRoutines;

  @Override
  public boolean isAvailable() {
    return true;
  }

  @Override
  public int getPriority() {
    return Integer.MIN_VALUE;
  }

  @Override
  public ArrayFactory getArrayFactory() {
    if (arrayFactory == null) {
      arrayFactory = new BaseArrayFactory();
    }
    return arrayFactory;
  }

  @Override
  public ArrayRoutines getArrayRoutines() {
    if (arrayRoutines == null) {
      arrayRoutines = new BaseArrayRoutines();
    }
    return arrayRoutines;
  }

  @Override
  public LinearAlgebraRoutines getLinearAlgebraRoutines() {
    if (linearAlgebraRoutines == null) {
      linearAlgebraRoutines = new BaseLinearAlgebraRoutines(this);
    }
    return linearAlgebraRoutines;
  }
}

package org.briljantframework.linalg.api;

import com.google.common.base.Preconditions;

import org.briljantframework.array.api.ArrayBackend;

/**
 * @author Isak Karlsson
 */
public abstract class AbstractLinearAlgebraRoutines implements LinearAlgebraRoutines {

  private final ArrayBackend arrayBackend;

  protected AbstractLinearAlgebraRoutines(ArrayBackend matrixFactory) {
    this.arrayBackend = Preconditions.checkNotNull(matrixFactory);
  }

  protected ArrayBackend getArrayBackend() {
    return arrayBackend;
  }
}

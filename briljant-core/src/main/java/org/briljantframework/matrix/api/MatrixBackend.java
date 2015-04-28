package org.briljantframework.matrix.api;

import org.briljantframework.linalg.api.LinearAlgebraRoutines;

/**
 * @author Isak Karlsson
 */
public interface MatrixBackend {

  boolean isAvailable();

  int getPriority();

  MatrixFactory getMatrixFactory();

  MatrixRoutines getMatrixRoutines();

  LinearAlgebraRoutines getLinearAlgebraRoutines();
}

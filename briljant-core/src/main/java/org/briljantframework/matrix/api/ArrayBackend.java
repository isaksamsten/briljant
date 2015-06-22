package org.briljantframework.matrix.api;

import org.briljantframework.linalg.api.LinearAlgebraRoutines;

/**
 * @author Isak Karlsson
 */
public interface ArrayBackend {

  boolean isAvailable();

  int getPriority();

  ArrayFactory getArrayFactory();

  ArrayRoutines getArrayRoutines();

  LinearAlgebraRoutines getLinearAlgebraRoutines();
}

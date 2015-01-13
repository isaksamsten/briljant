package org.briljantframework.exceptions;

import org.briljantframework.vector.VectorType;

/**
 * Exception to throw when the expected and actual type of data frames does not match
 * 
 * @author Isak Karlsson
 */
public class TypeMismatchException extends RuntimeException {
  public TypeMismatchException(VectorType expected, VectorType actual) {
    super(String.format("Got %s but expected %s", actual, expected));
  }
}

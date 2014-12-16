package org.briljantframework.dataframe.exceptions;

import org.briljantframework.vector.Type;

/**
 * Exception to throw when the expected and actual type of data frames does not match
 * 
 * @author Isak Karlsson
 */
public class TypeMismatchException extends RuntimeException {
  public TypeMismatchException(Type expected, Type actual) {
    super(String.format("Got %s but expected %s", actual, expected));
  }
}

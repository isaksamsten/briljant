package org.briljantframework.exceptions;

/**
 * @author Isak Karlsson
 */
public class ImmutableModificationException extends UnsupportedOperationException {

  public ImmutableModificationException() {
  }

  public ImmutableModificationException(String message) {
    super(message);
  }
}

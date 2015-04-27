package org.briljantframework.exceptions;

/**
 * Created by Isak Karlsson on 13/01/15.
 */
public class ImmutableModificationException extends UnsupportedOperationException {
  public ImmutableModificationException() {}

  public ImmutableModificationException(String message) {
    super(message);
  }
}

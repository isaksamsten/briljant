package org.briljantframework.exceptions;

/**
 * @author Isak Karlsson
 */
public class BlasException extends Error {

  private final int errorCode;

  public BlasException(int val, String s) {
    super(s);
    this.errorCode = val;
  }

  public int getErrorCode() {
    return errorCode;
  }
}

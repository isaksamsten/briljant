package org.briljantframework.exceptions;

/**
 * Created by Isak Karlsson on 02/12/14.
 */
public class BlasException extends Error {
  public BlasException(String func, int val, String s) {
    super(String.format("%s faild with %d and %s", func, val, s));
  }
}

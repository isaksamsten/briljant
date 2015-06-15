package org.briljantframework.matrix.netlib;

/**
 * @author Isak Karlsson
 */
public class NetlibLapackException extends RuntimeException {

  private final int errorCode;

  public NetlibLapackException(int val, String s) {
    super(s);
    this.errorCode = val;
  }

  public int getErrorCode() {
    return errorCode;
  }
}

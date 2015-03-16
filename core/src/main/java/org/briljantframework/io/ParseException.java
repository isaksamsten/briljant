package org.briljantframework.io;

/**
 * Created by isak on 16/03/15.
 */
public class ParseException extends RuntimeException {

  public ParseException(long line, long column) {
    super(String.format("Failed to parse %d:%d.", line, column));
  }
}

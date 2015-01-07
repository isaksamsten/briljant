package org.briljantframework.exceptions;

/**
 * Exception to throw when sizes does not match
 * 
 * @author Isak Karlsson
 */
public class SizeMismatchException extends RuntimeException {

  /**
   * Produces message "Expected size {expected} but got {actual}".
   * 
   * @param expected expected size
   * @param actual actual size
   */
  public SizeMismatchException(int expected, int actual) {
    super(String.format("Expected size %d but got %d", expected, actual));
  }

  /**
   * Produces message "Expected size {expected} but got {actual} ({message})".
   * 
   * @param message the message
   * @param expected expected size
   * @param actual actual size
   */
  public SizeMismatchException(String message, int expected, int actual) {
    super(String.format("Expected size %d but got %d (%s)", expected, actual, message));
  }
}

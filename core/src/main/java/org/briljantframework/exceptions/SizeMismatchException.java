package org.briljantframework.exceptions;

/**
 * Exception to throw when sizes does not match
 * 
 * @author Isak Karlsson
 */
public class SizeMismatchException extends RuntimeException {

  /**
   * Produces message "Expected size {expected} but got {actual}.".
   * 
   * @param expected expected size
   * @param actual actual size
   */
  public SizeMismatchException(long expected, long actual) {
    super(String.format("Expected size %d but got %d.", expected, actual));
  }

  /**
   * For example, {@code throw new SizeMismatchException("Size %d is not the same as %d", 10, 11)}.
   * 
   * The {@code message} must contain 2 {@code %d} to format the {@code expected} and {@code actual}
   * . The first {@code %d} is {@code expected} and the second {@code actual}.
   * 
   * @param message the message; a format string which must contain 2 {@code %d}.
   * @param expected expected size
   * @param actual actual size
   */
  public SizeMismatchException(String message, long expected, long actual) {
    super(String.format(message, expected, actual));
  }
}

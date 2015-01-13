package org.briljantframework.vector;

/**
 * Created by Isak Karlsson on 20/11/14.
 */
public enum Bit {
  TRUE(1), FALSE(0), NA(IntVector.NA);

  private final int value;

  /**
   * Instantiates a new Binary.
   *
   * @param value the value
   */
  Bit(int value) {
    this.value = value;
  }

  public static Bit valueOf(boolean value) {
    return value ? TRUE : FALSE;
  }

  public static Bit valueOf(int value) {
    switch (value) {
      case 1:
        return TRUE;
      case 0:
        return FALSE;
      default:
        return NA;
    }
  }

  public int asInt() {
    return value;
  }
}

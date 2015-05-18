package org.briljantframework.vector;

/**
 * @author Isak Karlsson
 */
public enum Bit {
  TRUE(1), FALSE(0), NA(IntVector.NA);

  private final int value;

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

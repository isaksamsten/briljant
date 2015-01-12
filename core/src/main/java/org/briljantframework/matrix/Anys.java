package org.briljantframework.matrix;

/**
 * Created by Isak Karlsson on 11/01/15.
 */
public final class Anys {
  private Anys() {}

  public static AnyMatrix take(AnyMatrix a, IntMatrix indexes) {
    AnyMatrix taken = a.newEmptyMatrix(indexes.size(), 1);
    for (int i = 0; i < indexes.size(); i++) {
      taken.set(i, a, indexes.get(i));
    }
    return taken;
  }

  public static AnyMatrix mask(AnyMatrix a, BitMatrix mask, AnyMatrix value) {
    assert a.rows() == mask.rows() && a.columns() == mask.columns();
    AnyMatrix masked = a.newEmptyMatrix(a.rows(), a.columns());
    putMask(masked, mask, value);
    return masked;
  }

  public static void putMask(AnyMatrix a, BitMatrix mask, AnyMatrix value) {
    for (int i = 0; i < a.size(); i++) {
      if (mask.get(i)) {
        a.set(i, value, i);
      }
    }
  }
}

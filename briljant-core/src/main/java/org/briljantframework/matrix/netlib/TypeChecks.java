package org.briljantframework.matrix.netlib;

/**
 * Created by isak on 15/04/15.
 */
class TypeChecks {

  public static void ensureInstanceOf(Class<?> cls, Object... storages) {
    for (Object storage : storages) {
      if (!cls.isInstance(storage)) {
        throw new IllegalArgumentException(String.format(
            "Unsupported storage unit !(%s instanceof %s)",
            storage.getClass().getSimpleName(), cls.getSimpleName()));
      }
    }
  }
}

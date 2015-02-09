package org.briljantframework.function;

/**
 * @author Isak Karlsson
 */
@FunctionalInterface
public interface ToIntIntObjBiFunction<T> {
  int applyAsInt(T t, int v);
}

package org.briljantframework.function;

/**
 * @author Isak Karlsson
 */
@FunctionalInterface
public interface ToIntObjIntBiFunction<T> {
  int applyAsInt(T t, int v);
}

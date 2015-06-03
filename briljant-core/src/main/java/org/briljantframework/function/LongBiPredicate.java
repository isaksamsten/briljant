package org.briljantframework.function;

/**
 * @author Isak Karlsson
 */
@FunctionalInterface
public interface LongBiPredicate {

  boolean test(long a, long b);
}

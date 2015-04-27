package org.briljantframework.function;

/**
 * Created by isak on 2/4/15.
 */
@FunctionalInterface
public interface LongBiPredicate {
  boolean test(long a, long b);
}

package org.briljantframework.dataseries;

import org.briljantframework.vector.Type;
import org.briljantframework.vector.Vector;

/**
 * <p>
 * Performs an aggregation of a particular data series, represented as a vector. For example, one
 * approach to aggregate {@code [1,2,3,4,5,6]}, from {@code 6} elements to, lets say, 3 elements, is
 * to divide the vector into {@code 3} equally sized bins: {@code [1,2], [3,4], [5,6]}. These bins
 * can then be aggregated by, e.g., averaging to produce the vector {@code [1.5, 3.5, 5.5]}.
 * </p>
 * 
 * <p>
 * The {@link #partialAggregate(org.briljantframework.vector.Vector)}, takes an input vector and
 * produces a mutable {@link org.briljantframework.vector.Vector.Builder} for futher transformation.
 * The {@link #aggregate(org.briljantframework.vector.Vector)} produces a new, aggregated, vector.
 * </p>
 * 
 * @author Isak Karlsson
 */
public interface Aggregator {

  /**
   * Perform aggregation on {@code in}. Return a mutable {@code Builder} for further processing.
   * 
   * @param in the vector
   * @return a mutable aggregate of {@code in}
   */
  Vector.Builder partialAggregate(Vector in);

  /**
   * @return the vector type of the approximation
   */
  Type getAggregatedType();

  /**
   * Performs aggregation on {@code in}. Returns the resulting vector.
   * 
   * @param in the vector
   * @return an aggregate of {@code in}
   */
  default Vector aggregate(Vector in) {
    return partialAggregate(in).build();
  }
}

package org.briljantframework.evaluation;

import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.vector.Vector;

/**
 * The partitioner represents a strategy of how to partition a {@code DataFrame} and {@code Vector}
 * into training and validation partitions.
 *
 * @author Isak Karlsson
 */
public interface Partitioner {

  /**
   * Partitions {@code x} and {@code y} into training and validation partitions
   *
   * @param x the data
   * @param y the target
   * @return an
   */
  Iterable<Partition> partition(DataFrame x, Vector y);
}

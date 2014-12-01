package org.briljantframework.learning.evaluation;

import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.vector.Vector;

/**
 * Created by Isak Karlsson on 01/12/14.
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

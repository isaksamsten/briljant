package org.briljantframework.evaluation;

import org.briljantframework.Check;
import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.vector.Vector;

/**
 * The leave-one-out partitioner can be used to implement Leave-one-out cross-validation, a
 * commonly employed strategy for evaluating small and expensive to gather datasets.
 *
 * <p>The {@code DataFrame} (with {@code m} rows) and {@code Vector} (of length {@code m}) are
 * partitioned into {@code m} partitions. At each iteration {@code m-1} data points are returned as
 * the training set and {@code 1} data point as the validation set. All data points are used as
 * validation points exactly once.
 *
 * @author Isak Karlsson
 */
public class LeaveOneOutPartitioner implements Partitioner {

  @Override
  public Iterable<Partition> partition(DataFrame x, Vector y) {
    Check.size(x, y);
    return () -> new FoldIterator(x, y, x.rows());
  }
}

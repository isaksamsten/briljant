package org.briljantframework.evaluation;

import org.briljantframework.Check;
import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.vector.Vector;

/**
 * Creates a k-fold partitioner
 * <p>
 * 
 * @author Isak Karlsson
 */
public class FoldPartitioner implements Partitioner {

  private final int folds;

  public FoldPartitioner(int folds) {
    this.folds = folds;
  }

  @Override
  public Iterable<Partition> partition(DataFrame x, Vector y) {
    Check.size(x, y);
    return () -> new FoldIterator(x, y, folds);
  }
}

package org.briljantframework.evaluation;

import org.briljantframework.Check;
import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.vector.Vector;

/**
 * Created by isak on 09/03/15.
 */
public class LeaveOneOutPartitioner implements Partitioner {
  @Override
  public Iterable<Partition> partition(DataFrame x, Vector y) {
    Check.size(x, y);
    return () -> new FoldIterator(x, y, x.rows());
  }
}

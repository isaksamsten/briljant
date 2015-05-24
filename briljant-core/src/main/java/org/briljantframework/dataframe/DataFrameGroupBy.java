package org.briljantframework.dataframe;

import org.briljantframework.function.Aggregator;
import org.briljantframework.vector.Vector;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.UnaryOperator;

/**
 * @author Isak Karlsson
 */
public interface DataFrameGroupBy extends Iterable<Group> {

  /**
   * Get the groups and indices of the entries in each group.
   *
   * @return a set of entries
   */
  Set<Map.Entry<Object, Vector>> groups();

  /**
   * <p> Perform an aggregation of each column of each group.
   *
   * <p> Please note that the performance of this aggregation is usually worse than for {@linkplain
   * #aggregate(Class, org.briljantframework.function.Aggregator)}
   *
   * @param function the function to perform on each column
   * @return a data frame
   */
  DataFrame aggregate(Function<Vector, Object> function);

  <T, C> DataFrame aggregate(Class<? extends T> cls,
                             Aggregator<? super T, ? extends T, C> aggregator);

  <T> DataFrameGroupBy transform(Class<T> cls, UnaryOperator<T> op);

}

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

  Set<Map.Entry<Object, Vector>> groups();

  DataFrame aggregate(Function<Vector, Object> function);

  <T, C> DataFrame aggregate(Class<? extends T> cls,
                             Aggregator<? super T, ? extends T, C> aggregator);

  <T> DataFrame transform(Class<T> cls, UnaryOperator<T> op);

}

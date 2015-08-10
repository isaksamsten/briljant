/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Isak Karlsson
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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
   * Get a data frame of the elements grouped as {@code key}.
   *
   * @param key the key
   * @return a new data frame
   */
  DataFrame get(Object key);

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

  /**
   * Select and aggregate on all columns of type {@code cls}
   *
   * @param cls        the class
   * @param aggregator the aggregator
   * @param <T>        the input type
   * @param <C>        the mutable container
   * @return a new data frame
   */
  <T, C> DataFrame aggregate(Class<? extends T> cls,
                             Aggregator<? super T, ? extends T, C> aggregator);

  <T> DataFrameGroupBy transform(Class<T> cls, UnaryOperator<T> op);

}

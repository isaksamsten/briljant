/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Isak Karlsson
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.briljantframework.data.dataframe;

import java.util.*;

import org.briljantframework.data.dataframe.join.*;

/**
 * Implements various ways of joining data frames.
 * 
 * @author Isak Karlsson
 */
public final class Join {
  private final DataFrame a, b;
  private final JoinOperation operation;

  private Join(DataFrame a, DataFrame b, JoinOperation operation) {
    this.a = Objects.requireNonNull(a);
    this.b = Objects.requireNonNull(b);
    this.operation = Objects.requireNonNull(operation);
  }

  /**
   * Create an inner join.
   *
   * @param a left
   * @param b right
   * @return a join
   */
  public static Join inner(DataFrame a, DataFrame b) {
    return new Join(a, b, InnerJoin.getInstance());
  }

  /**
   * Create an outer join.
   *
   * @param a left
   * @param b right
   * @return a join
   */
  public static Join outer(DataFrame a, DataFrame b) {
    return new Join(a, b, OuterJoin.getInstance());
  }

  /**
   * Create an left outer join.
   *
   * @param a left
   * @param b right
   * @return a join
   */
  public static Join leftOuter(DataFrame a, DataFrame b) {
    return new Join(a, b, LeftOuterJoin.getInstance());
  }

  /**
   * Create an right outer join.
   *
   * @param a left
   * @param b right
   * @return a join
   */
  public static Join rightOuter(DataFrame a, DataFrame b) {
    return new Join(a, b, RightOuterJoin.getInstance());
  }

  /**
   * Join the data frames on the specified column(s).
   * 
   * @param columns the columns to use for the join
   * @return a new data frame
   */
  public DataFrame on(Collection<?> columns) {
    return operation.createJoiner(a, b, columns).join(a, b);
  }

  /**
   * Join the data frames on the specified column
   * 
   * @param key the join column
   * @return a new data frame
   */
  public DataFrame on(Object key) {
    return on(Collections.singletonList(key));
  }

  /**
   * Join the data frames on the specified columns
   * 
   * @param key the first column key
   * @param keys the rest of the columns
   * @return a new data frame
   */
  public DataFrame on(Object key, Object... keys) {
    List<Object> on = new ArrayList<>();
    on.add(key);
    Collections.addAll(on, keys);
    return on(on);
  }

  /**
   * Join the data frames on their indices.
   * 
   * @return a new data from
   */
  public DataFrame onIndex() {
    return operation.createJoiner(JoinUtils.createJoinKeys(a.getIndex(), b.getIndex())).join(a, b);
  }

  /**
   * Join the data frames on intersecting columns
   * 
   * @return a new data frame
   */
  public DataFrame natural() {
    Set<Object> columns = new HashSet<>();
    columns.addAll(a.getColumnIndex());
    columns.addAll(b.getColumnIndex());
    return on(columns);
  }
}

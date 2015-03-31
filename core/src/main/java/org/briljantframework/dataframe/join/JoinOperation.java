package org.briljantframework.dataframe.join;

/**
 * A join-operation produces a {@link org.briljantframework.dataframe.join.Joiner}. For example,
 * {@link org.briljantframework.dataframe.join.InnerJoin}.
 *
 * @author Isak Karlsson
 */
public interface JoinOperation {

  Joiner createJoiner(JoinKeys keys);
}

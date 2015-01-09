package org.briljantframework.dataframe.join;

/**
 * Created by Isak on 2015-01-08.
 */
public interface JoinOperation {

  Joiner createJoiner(JoinKeys keys);
}

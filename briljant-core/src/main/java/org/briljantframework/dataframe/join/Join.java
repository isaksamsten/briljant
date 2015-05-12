package org.briljantframework.dataframe.join;

import org.briljantframework.dataframe.DataFrame;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by isak on 10/05/15.
 */
public class Join {

  private static final Map<JoinType, JoinOperation> JOINERS;

  static {
    EnumMap<JoinType, JoinOperation> joiners = new EnumMap<>(JoinType.class);
    joiners.put(JoinType.INNER, InnerJoin.getInstance());
    joiners.put(JoinType.OUTER, OuterJoin.getInstance());
    joiners.put(JoinType.LEFT, LeftOuterJoin.getInstance());
    joiners.put(JoinType.RIGHT, LeftOuterJoin.getInstance());
    JOINERS = Collections.unmodifiableMap(joiners);
  }

}

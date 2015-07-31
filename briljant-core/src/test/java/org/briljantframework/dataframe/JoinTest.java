package org.briljantframework.dataframe;

import org.briljantframework.dataframe.join.JoinType;
import org.briljantframework.vector.Vector;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class JoinTest {

  @Test
  public void testSimpleInnerJoin() throws Exception {
    DataFrame left = MixedDataFrame.of("key", Vector.of("foo", "foo", "ko"),
                                       "lval", Vector.of(1, 2, 4));
    DataFrame right = MixedDataFrame.of("key", Vector.of("foo", "bar"),
                                        "rval", Vector.of(3, 5));

    DataFrame actual = left.join(JoinType.INNER, right);
    DataFrame expected = MixedDataFrame.of(
        "key", Vector.of("foo", "foo"),
        "lval", Vector.of(1, 2),
        "rval", Vector.of(3, 3)
    );
    assertEquals(expected, actual);
  }

  @Test
  public void testComplexInnerJoin() throws Exception {
    DataFrame left = MixedDataFrame.of("key1", Vector.of("foo", "foo", "bar"),
                                       "key2", Vector.of("one", "two", "one"),
                                       "lval", Vector.of(1, 2, 3));
    DataFrame right = MixedDataFrame.of("key1", Vector.of("foo", "foo", "bar", "bar"),
                                        "key2", Vector.of("one", "one", "one", "two"),
                                        "rval", Vector.of(4, 5, 6, 7));

    DataFrame actual = left.join(JoinType.INNER, right);
    DataFrame expected = MixedDataFrame.of(
        "key1", Vector.of("foo", "foo", "bar"),
        "key2", Vector.of("one", "one", "one"),
        "lval", Vector.of(1, 1, 3),
        "rval", Vector.of(4, 5, 6)
    );
    assertEquals(expected, actual);
  }
}

package org.briljantframework.dataframe;

import junit.framework.TestCase;

import org.briljantframework.vector.IntVector;
import org.briljantframework.vector.StringVector;

public class JoinTest extends TestCase {

  public void testJoin() throws Exception {
    DataFrame a =
        MixedDataFrame.of("xx", new StringVector("a", "b", "a", "a", "d", "c", "b"), "yy",
            new IntVector(1, 2, 3, 4, 5, 6, 7), "dd", new IntVector(2, 2, 3, 4, 54, 6, 2));

    DataFrame b =
        MixedDataFrame.of("xx", new StringVector("b", "fz", "a", "c", "d", "d", "q"), "yy",
            new IntVector(1, 2, 3, 4, 5, 6, 7), "dd", new IntVector(2, 2, 3, 4, 54, 6, 2));


    System.out.println(a);
    System.out.println(b);
    Join join = new Join(2);
    System.out.println(join.join(a, b));
  }
}

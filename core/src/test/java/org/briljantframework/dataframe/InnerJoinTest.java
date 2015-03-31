package org.briljantframework.dataframe;

import junit.framework.TestCase;

import org.briljantframework.vector.IntVector;
import org.briljantframework.vector.StringVector;

import java.util.Arrays;

public class InnerJoinTest extends TestCase {

  public void testGuesstimatePerformance() throws Exception {
//    DataFrame cats =
//        MixedDataFrame.of("User", new StringVector("a", "a", "c", "d"), "Number of Cats",
//                          new IntVector(1, 2, 3, 4));
//
//    DataFrame dogs =
//        MixedDataFrame.of("User", new StringVector("b", "a", "a", "a"), "Number of dogs",
//                          new IntVector(1, 2, 2, 3), "Poop", new IntVector(1, 2, 3, 4));
//
//    System.out.println(cats);
//    System.out.println(dogs);
//    System.out.println(DataFrames.outerJoin(cats, dogs, Arrays.asList("User")));
//
    DataFrame connect4 = Datasets.loadConnect4();
    connect4 = connect4.addColumn(0, IntVector.range(connect4.rows()));
    connect4.setColumnName(0, "index");

    long s = System.nanoTime();
    for (int i = 0; i < 50; i++) {
      DataFrame f = DataFrames.innerJoin(connect4, connect4, Arrays.asList("index"));
    }
    System.out.println((System.nanoTime() - s) / 1e6 / 50);
    // System.out.println(DataFrames.leftOuterJoin(dogs, cats, Arrays.asList(0)));

  }

  public void testSimpleMerge() throws Exception {
    DataFrame left = MixedDataFrame.of("key", new StringVector("foo", "foo"),
                                       "lval", new IntVector(1, 2));
    DataFrame right = MixedDataFrame.of("key", new StringVector("foo", "foo"),
                                        "rval", new IntVector(3, 5));
    DataFrame j = DataFrames.outerJoin(left, right);
    System.out.println(j);
  }

  public void testComplexMerge() throws Exception {
    DataFrame left = MixedDataFrame.of("key1", new StringVector("foo", "foo", "bar"),
                                       "key2", new StringVector("one", "two", "one"),
                                       "lval", new IntVector(1, 2, 3));
    DataFrame right = MixedDataFrame.of("key1", new StringVector("foo", "foo", "bar", "bar"),
                                        "key2", new StringVector("one", "one", "one", "two"),
                                        "rval", new IntVector(4, 5, 6, 7));
    System.out.println(DataFrames.outerJoin(left, right));

  }
}

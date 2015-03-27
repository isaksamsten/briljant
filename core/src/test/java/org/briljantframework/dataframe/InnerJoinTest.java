package org.briljantframework.dataframe;

import junit.framework.TestCase;

import org.briljantframework.vector.IntVector;
import org.briljantframework.vector.StringVector;

import java.util.Arrays;

public class InnerJoinTest extends TestCase {

  public void testJoin() throws Exception {
    DataFrame cats =
        MixedDataFrame.of("User", new StringVector("a", "a", "c", "d"), "Number of Cats",
                          new IntVector(1, 2, 3, 4));

    DataFrame dogs =
        MixedDataFrame.of("User", new StringVector("b", "a", "a", "a"), "Number of dogs",
                          new IntVector(1, 2, 2, 3), "Poop", new IntVector(1, 2, 3, 4));

    System.out.println(cats);
    System.out.println(dogs);
//    System.out.println(DataFrames.leftOuterJoin(cats, dogs, Arrays.asList("User")));

    DataFrame connect4 = Datasets.loadConnect4();
    connect4 = connect4.addColumn(0, IntVector.range(connect4.rows()));
    connect4.setColumnName(0, "index");
//    DataFrame a = connect4.getRecords(Range.range(0, 4000).flat());
//    DataFrame b = connect4.getRecords(Range.range(0, 4000).flat());

    for (int i = 0; i < 10; i++) {
      long s = System.nanoTime();
      DataFrame f = DataFrames.innerJoin(connect4, connect4, Arrays.asList("index"));
      System.out.println((System.nanoTime() - s) / 1e6);
      System.out.println(f.columns());
    }

    // System.out.println(DataFrames.leftOuterJoin(dogs, cats, Arrays.asList(0)));

  }
}

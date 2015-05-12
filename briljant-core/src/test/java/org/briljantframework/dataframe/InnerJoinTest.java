package org.briljantframework.dataframe;

import junit.framework.TestCase;

import org.briljantframework.dataframe.join.JoinType;
import org.briljantframework.vector.IntVector;
import org.briljantframework.vector.Is;
import org.briljantframework.vector.Na;
import org.briljantframework.vector.StringVector;

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
    connect4 = connect4.insert(0, "index", IntVector.range(connect4.rows()));
//    connect4.setColumnName(0, "index");
//    System.out.println(connect4);
    long s = System.nanoTime();
    DataFrame f = null;
    for (int i = 0; i < 10; i++) {
      f = connect4.join(connect4, 0);
    }
    System.out.println((System.nanoTime() - s) / 1e6);
    // System.out.println(DataFrames.leftOuterJoin(dogs, cats, Arrays.asList(0)));

  }

  public void testSimpleMerge() throws Exception {
    DataFrame left = MixedDataFrame.of("key", new StringVector("foo", "foo", "ko"),
                                       "lval", new IntVector(1, 2, 4));
    DataFrame right = MixedDataFrame.of("key", new StringVector("foo", "bar"),
                                        "rval", new IntVector(3, 5));

    System.out.println(left);
    System.out.println(right);
//    DataFrame j = DataFrames.innerJoin(left, right);
//    System.out.println(j);
    System.out.println(
        left.join(JoinType.OUTER, right)
            .setRecordIndex(HashIndex.from("q", "A", "b", "D"))
            .sort(SortOrder.DESC)
    );

  }

  public void testComplexMerge() throws Exception {
    DataFrame left = MixedDataFrame.of("key1", new StringVector("foo", "foo", "bar"),
                                       "key2", new StringVector("one", "two", "one"),
                                       "lval", new IntVector(1, 2, 3));
    DataFrame right = MixedDataFrame.of("key1", new StringVector("foo", "foo", "bar", "bar"),
                                        "key2", new StringVector("one", "one", "one", "two"),
                                        "rval", new IntVector(4, 5, 6, 7));

    System.out
        .println(left.apply(Integer.class, v -> !Is.NA(v) ? v * 2 : Na.of(Integer.class), "lval"));

    Series df = left.join(right)
        .sortBy("lval")
        .apply(Integer.class, v -> !Is.NA(v) ? (int) Math.pow(v, 2) : Na.of(Integer.class))
        .reduce(Integer.class, 0, (a, b) -> a + b);
    System.out.println(df);
    Series sums = left.reduce(Integer.class, 0,
                              (v, acc) -> !Is.NA(v) ? v + acc : Na.of(Integer.class));
    System.out.println(sums);
    System.out.println(right);
    System.out.println(left.join(JoinType.INNER, right));

  }
}

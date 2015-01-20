package org.briljantframework.dataframe;

import java.util.Arrays;

import junit.framework.TestCase;

import org.briljantframework.vector.IntVector;
import org.briljantframework.vector.StringVector;

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
    System.out.println(DataFrames.leftOuterJoin(cats, dogs, Arrays.asList(0)));

    // System.out.println(DataFrames.leftOuterJoin(dogs, cats, Arrays.asList(0)));

    }
}

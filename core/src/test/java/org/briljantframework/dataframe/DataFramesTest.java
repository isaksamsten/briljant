package org.briljantframework.dataframe;

import org.briljantframework.Utils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class DataFramesTest {

  DataFrame iris;

  @Before
  public void setUp() throws Exception {
    iris = Datasets.loadIris();
  }

  @Test
  public void testShuffle() throws Exception {
    Utils.setRandomSeed(123);
    DataFrame shuffle = DataFrames.shuffle(iris);

    System.out.println(shuffle.getRow(0));
    System.out.println(iris.getRow(0));
  }

  @Test
  public void testShuffleConnect4() throws Exception {
    DataFrame connect4 = Datasets.loadConnect4();

    long start = System.currentTimeMillis();
    DataFrame shuffled = DataFrames.shuffle(connect4);
    System.out.println(System.currentTimeMillis() - start);

    System.out.println(shuffled.getRow(100));
    System.out.println(connect4.getRow(100));

    Assert.assertEquals(connect4.rows(), shuffled.rows());



  }
}

package org.briljantframework.dataframe;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DataFramesTest {

  DataFrame iris;

  @Before
  public void setUp() throws Exception {
    iris = Datasets.loadIris();
  }

  @Test
  public void testShuffleConnect4() throws Exception {
    DataFrame connect4 = Datasets.loadConnect4();
    DataFrame shuffled = DataFrames.permuteRows(connect4);
    assertEquals(connect4.rows(), shuffled.rows());
  }
}

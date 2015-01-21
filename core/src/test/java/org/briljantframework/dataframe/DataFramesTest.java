package org.briljantframework.dataframe;

import org.briljantframework.Utils;
import org.briljantframework.dataseries.DataSeriesCollection;
import org.briljantframework.io.MatlabTextInputStream;
import org.briljantframework.matrix.DoubleMatrix;
import org.briljantframework.vector.DoubleVector;
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
    DataFrame shuffle = DataFrames.permuteRows(iris);

    System.out.println(shuffle.getRow(0));
    System.out.println(iris.getRow(0));
  }

  @Test
  public void testShuffleConnect4() throws Exception {
    DataFrame connect4 = Datasets.loadConnect4();

    long start = System.currentTimeMillis();
    DataFrame shuffled = DataFrames.permuteRows(connect4);
    System.out.println(System.currentTimeMillis() - start);

    System.out.println(shuffled.getRow(100));
    System.out.println(connect4.getRow(100));

    Assert.assertEquals(connect4.rows(), shuffled.rows());
  }

  @Test
  public void testLoadMultiChannel() throws Exception {
    DataFrame channel =
        Datasets.load((a, b) -> new DataSeriesCollection.Builder(DoubleVector.TYPE),
            MatlabTextInputStream::new, "multichannel");
    channel.setColumnNames("Seq ID", "channel", "Class");

    System.out.println(channel.dropColumn(0));

    System.out.println(channel.getRow(30));


    // Drop the first 3 columns
//    DoubleMatrix matrix = channel.asMatrix().getView(0, 3, channel.rows(), channel.columns() - 3);
//    DoubleMatrix cls = channel.asMatrix().getColumnView(2);
//    System.out.println(cls);
//    long s = System.currentTimeMillis();
//    DoubleMatrix rowSum = matrix.reduceRows(x -> x.reduce(0, Double::sum, d -> d));
//    System.out.println(System.currentTimeMillis() - s);
//    System.out.println(rowSum);



  }
}

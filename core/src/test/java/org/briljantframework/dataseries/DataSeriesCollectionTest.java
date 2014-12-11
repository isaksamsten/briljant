package org.briljantframework.dataseries;

import static org.junit.Assert.assertEquals;

import java.io.BufferedInputStream;
import java.io.FileInputStream;

import org.briljantframework.io.DataFrameInputStream;
import org.briljantframework.io.MatlabTextInputStream;
import org.briljantframework.vector.DoubleVector;
import org.junit.Test;

public class DataSeriesCollectionTest {

  @Test
  public void testBuilder() throws Exception {

    try (DataFrameInputStream dfis =
        new MatlabTextInputStream(new BufferedInputStream(new FileInputStream(
            "/Users/isak/Desktop/ecgdata.txt")))) {
      DataSeriesCollection.Builder builder = new DataSeriesCollection.Builder(DoubleVector.TYPE);
      builder.read(dfis);
      DataSeriesCollection coll = builder.build();
      System.out.println(coll);

      System.out.println(coll.getRow(77));

    } catch (Exception e) {

    }

    DataSeriesCollection.Builder builder = new DataSeriesCollection.Builder(DoubleVector.TYPE);
    builder.set(0, 0, 10);
    builder.set(9, 9, 30);
    DataSeriesCollection frame = builder.build();
    System.out.println(frame);

    System.out.println(frame.getRow(4));


    assertEquals(1, 1, 1);
  }
}

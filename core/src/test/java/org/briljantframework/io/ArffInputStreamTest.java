package org.briljantframework.io;

import java.io.FileInputStream;

import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.dataframe.MixedDataFrame;
import org.briljantframework.dataseries.DataSeriesCollection;
import org.briljantframework.vector.DoubleVector;
import org.briljantframework.vector.Vector;
import org.junit.Test;

public class ArffInputStreamTest {

  @Test
  public void testNext() throws Exception {

    FileInputStream fis =
        new FileInputStream("/Users/isak-kar/Downloads/dataset3/BeetleFly/BeetleFly_TRAIN.arff");
    ArffInputStream ais = new ArffInputStream(fis);

    DataFrame frame =
        new MixedDataFrame.Builder(ais.readColumnNames(), ais.readColumnTypes()).read(ais).build();
    Vector cls = frame.getColumn(frame.columns() - 1);
    DataFrame x = frame.dropColumn(frame.columns() - 1);

    DataFrame collection = new DataSeriesCollection.Builder(DoubleVector.TYPE).addAll(0, x).build();

    System.out.println(collection);
  }
}

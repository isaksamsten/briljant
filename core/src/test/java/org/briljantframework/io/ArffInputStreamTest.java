package org.briljantframework.io;

import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.dataframe.MixedDataFrame;
import org.junit.Test;

import java.io.FileInputStream;

public class ArffInputStreamTest {

  @Test
  public void testLoadArff() throws Exception {
    ArffInputStream
        in =
        new ArffInputStream(
            new FileInputStream("/Users/isak-kar/Downloads/dataset3/DP_Little/DP_Little.arff"));

    DataFrame.Builder
        builder =
        new MixedDataFrame.Builder(in.readColumnNames(), in.readColumnTypes());
    builder.read(in);

    System.out.println(builder.build());

  }
}
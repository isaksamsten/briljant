package org.briljantframework.io;

import java.io.BufferedInputStream;
import java.io.FileInputStream;

import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.dataframe.DataFrames;
import org.briljantframework.dataframe.MixedDataFrame;
import org.junit.Test;

public class DataFrameInputStreamTest {


  @Test
  public void testReadDataFrame() throws Exception {
    try (DataFrameInputStream dfis =
        new CsvInputStream(new BufferedInputStream(new FileInputStream("connect-4.txt")))) {
      long start = System.currentTimeMillis();
      DataFrame iris = DataFrames.load(MixedDataFrame.Builder::new, dfis);
      System.out.println(System.currentTimeMillis() - start);
      System.out.println(iris);
      start = System.currentTimeMillis();
      iris.newCopyBuilder().removeColumn(1).create();
      System.out.println(System.currentTimeMillis() - start);

      start = System.currentTimeMillis();
      new MixedDataFrame(iris);
      System.out.println(System.currentTimeMillis() - start);

      start = System.currentTimeMillis();
      for (int i = 0; i < iris.rows(); i++) {
        blackbox(iris.getAsString(i, 3));
      }
      System.out.println(System.currentTimeMillis() - start);

      // System.out.println();
    }


  }

  private void blackbox(Object asString) {

  }
}

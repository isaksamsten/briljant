package org.briljantframework.io;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.briljantframework.IntRange;
import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.dataframe.DataFrames;
import org.briljantframework.dataframe.MixedDataFrame;
import org.junit.Test;

public class DataFrameInputStreamTest {


  @Test
  public void testReadDataFrame() throws Exception {
    try (DataFrameInputStream dfis =
        new CsvInputStream(new BufferedInputStream(new FileInputStream("iris.txt")))) {
      long start = System.currentTimeMillis();
      DataFrame iris = DataFrames.load(MixedDataFrame.Builder::new, dfis);


      new CsvOutputStream(new FileOutputStream("iris2.txt")).write(iris);
      System.out.println(DataFrames.load(MixedDataFrame.Builder::new, new CsvInputStream(
          "iris2.txt")));


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

      System.out.println(iris.dropColumns(IntRange.closed(0, 2)));

      // System.out.println();
    }


  }

  private void blackbox(Object asString) {

  }
}

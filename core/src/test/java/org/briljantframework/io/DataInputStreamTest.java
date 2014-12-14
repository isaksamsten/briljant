package org.briljantframework.io;

import java.io.FileOutputStream;

import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.dataframe.DataFrames;
import org.briljantframework.dataframe.Datasets;
import org.briljantframework.dataframe.MixedDataFrame;
import org.briljantframework.vector.Vector;
import org.junit.Test;

public class DataInputStreamTest {


  @Test
  public void testReadDataFrame() throws Exception {
    try (DataInputStream dfis = new DelimitedInputStream("iris.txt")) {
      long start = System.currentTimeMillis();
      DataFrame iris = DataFrames.load(MixedDataFrame.Builder::new, dfis);


      new CsvOutputStream(new FileOutputStream("iris2.txt")).write(iris);
      DataFrame load =
          DataFrames.load(MixedDataFrame.Builder::new, new DelimitedInputStream("iris2.txt"));
      System.out.println(load);


      // System.out.println(System.currentTimeMillis() - start);
      // System.out.println(iris);
      // start = System.currentTimeMillis();
      // iris.newCopyBuilder().removeColumn(1).create();
      // System.out.println(System.currentTimeMillis() - start);
      //
      // start = System.currentTimeMillis();
      // new MixedDataFrame(iris);
      // System.out.println(System.currentTimeMillis() - start);
      //
      // start = System.currentTimeMillis();
      // for (int i = 0; i < iris.rows(); i++) {
      // blackbox(iris.getAsString(i, 3));
      // }
      // System.out.println(System.currentTimeMillis() - start);
      //
      // System.out.println(iris.dropColumns(IntRange.closed(0, 2)));

      print(iris.getColumn(0));
      Vector.Builder builder = iris.getColumn(0).newCopyBuilder();
      builder.swap(0, 2);

      // print(builder.create());

      // System.out.println();
    }
  }

  @Test
  public void testDataSeriesInputStream() throws Exception {
    DataFrame s = Datasets.loadSyntheticControl();
    System.out.println(s);

  }

  private void print(Vector vector) {
    System.out.println(vector.getAsDouble(0));
    System.out.println(vector.getAsDouble(2));
  }

  private void blackbox(Object asString) {

  }
}

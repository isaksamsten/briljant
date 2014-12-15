package org.briljantframework.dataframe;

import org.junit.Test;

public class MatrixDataFrameTest {

  @Test
  public void testBuilder() throws Exception {
    DataFrame.Builder builder = new MatrixDataFrame.ArrayBuilder();
    builder.set(0, 3, 30);
    builder.set(1, 0, 50);
    builder.swapColumns(0, 3);
    builder.swapRows(0, 1);
    System.out.println(builder.build());


    builder = new MatrixDataFrame.HashBuilder();
    builder.set(0, 3, 30);
    builder.set(1, 0, 50);
    builder.set(2, 2, 20);
    builder.set(3, 1, 22);
    builder.swapColumns(0, 3);
    builder.swapRows(0, 1);
    builder.removeColumn(0);
    DataFrame frame = builder.build();
    System.out.println(frame);


    System.out.println(DataFrames.permuteRows(frame));
    // long s = System.currentTimeMillis();
    // for (int i = 0; i < 10; i++) {
    // for (int j = 0; j < 10; j++) {
    // builder.set(i, j, i + j);
    // }
    // }

    // builder.set(9, 9, 10);
    // builder.set(3, 3, 10);
    // builder.set(0, 0, 10);
    // DataFrame frame = builder.build();
    // System.out.println(frame.asMatrix());
    // System.out.println(System.currentTimeMillis() - s);



    // System.out.println(ArrayBuffers.allocations);

    // builder.set(0, 0, 1).set(0, 1, 2).set(0, 2, 3).set(1, 0, 4).set(1, 1, 5).set(1, 2, 6);
    // DataFrame frame = builder.build();
    //
    // System.out.println(frame.asMatrix());



  }
}

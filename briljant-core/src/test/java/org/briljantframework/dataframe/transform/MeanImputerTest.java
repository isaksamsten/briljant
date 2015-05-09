package org.briljantframework.dataframe.transform;

import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.dataframe.MixedDataFrame;
import org.briljantframework.vector.DoubleVector;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class MeanImputerTest {

  @Before
  public void setUp() throws Exception {
  }

  @Test
  public void testFit() throws Exception {
    DataFrame frame = new MixedDataFrame(
        DoubleVector.wrap(1, 2, 3, DoubleVector.NA),
        DoubleVector.wrap(3, 3, 3, DoubleVector.NA),
        DoubleVector.wrap(DoubleVector.NA, 2, 2, DoubleVector.NA)
    );
//    frame.setColumnNames("first", "second", "third");
    MeanImputer imputer = new MeanImputer();
    Transformation t = imputer.fit(frame);
    DataFrame imputed = t.transform(frame);
    System.out.println(imputed);
    Assert.assertEquals(2, imputed.getAsDouble(3, 0), 0.0);
  }
}

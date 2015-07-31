package org.briljantframework.dataframe.transform;

import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.dataframe.HashIndex;
import org.briljantframework.dataframe.MixedDataFrame;
import org.briljantframework.vector.DoubleVector;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MeanImputerTest {

  @Test
  public void testFit() throws Exception {
    DataFrame frame = new MixedDataFrame(
        DoubleVector.wrap(1, 2, 3, DoubleVector.NA),
        DoubleVector.wrap(3, 3, 3, DoubleVector.NA),
        DoubleVector.wrap(DoubleVector.NA, 2, 2, DoubleVector.NA)
    );
    HashIndex columnIndex = HashIndex.from("first", "second", "third");
    HashIndex recordIndex = HashIndex.from("a", "b", "c","d");
    frame.setColumnIndex(columnIndex);
    frame.setRecordIndex(recordIndex);

    MeanImputer imputer = new MeanImputer();
    Transformation t = imputer.fit(frame);
    DataFrame imputed = t.transform(frame);

    assertEquals(columnIndex, imputed.getColumnIndex());
    assertEquals(recordIndex, imputed.getRecordIndex());
    assertEquals(2, imputed.getAsDouble(3, 0), 0.0);
  }
}

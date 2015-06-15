package org.briljantframework.dataseries;

import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.vector.DoubleVector;
import org.briljantframework.vector.Vector;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class DataSeriesCollectionTest {

  @Test
  public void testDropRows() throws Exception {
    DataSeriesCollection.Builder builder = new DataSeriesCollection.Builder(DoubleVector.TYPE);
    builder.addRecord(DoubleVector.newBuilderWithInitialValues(1, 2, 3, 4, 5, 6))
        .addRecord(DoubleVector.newBuilderWithInitialValues(1, 2, 3, 4, 5, 6))
        .addRecord(DoubleVector.newBuilderWithInitialValues(1, 2, 3, 4, 5, 6));

    DataSeriesCollection collection = builder.build();
//    collection.setColumnNames("a", "b", "c");
    DataFrame drop = collection.drop(Arrays.asList(0, 1));

    //TODO(isak): fix failing test.
//    assertEquals("b", drop.getColumnName(0));
    for (Vector row : drop) {
      assertEquals(3, row.getAsDouble(0), 0.0001);
      assertEquals(4, row.getAsDouble(1), 0.0001);
      assertEquals(5, row.getAsDouble(2), 0.0001);
      assertEquals(6, row.getAsDouble(3), 0.0001);
    }
  }
}

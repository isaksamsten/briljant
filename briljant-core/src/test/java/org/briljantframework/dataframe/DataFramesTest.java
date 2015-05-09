package org.briljantframework.dataframe;

import org.briljantframework.io.reslover.Resolver;
import org.briljantframework.io.reslover.Resolvers;
import org.briljantframework.io.reslover.StringDateConverter;
import org.briljantframework.vector.DoubleVector;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;

public class DataFramesTest {

  DataFrame iris;

  @Test
  public void testLoadCsv() throws Exception {
    Resolver<LocalDate> dateResolver = Resolvers.find(LocalDate.class);
    dateResolver.put(String.class, new StringDateConverter("yyyy-MM-dd"));

    DataFrame df = DataFrames.loadCsv(
        "/Users/isak-kar/Downloads/GOOG-NASDAQ_AAPL.csv"
    );
    System.out.println(df.rows());

    df = df.indexOn("Date").sort(SortOrder.DESC);
    System.out.println(df.sortBy("Close", SortOrder.DESC));

    System.out.println(df.get(Double.class, 0, 0));
    System.out.println(df.getAsDouble(1, 1));
    System.out.println(df.getAsDouble(LocalDate.parse("1980-12-12"), "Close"));

  }

  @Test
  public void testName() throws Exception {
    DataFrame df = MixedDataFrame.of(
        "Close", new DoubleVector(1, 2, 3, 4, 5, 6, 7),
        "Open", new DoubleVector(8, 32, 5, 6, 1, 2, 4)
    );

    System.out.println(df);
    System.out.println(df.sortBy("Open", SortOrder.DESC));


  }

  @Before
  public void setUp() throws Exception {
//    iris = Datasets.loadIris();
  }

  @Test
  public void testShuffleConnect4() throws Exception {
    DataFrame connect4 = Datasets.loadConnect4();
    DataFrame shuffled = DataFrames.permuteRows(connect4);
    assertEquals(connect4.rows(), shuffled.rows());
  }
}

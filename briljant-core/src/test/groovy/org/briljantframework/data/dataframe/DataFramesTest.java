/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Isak Karlsson
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.briljantframework.data.dataframe;

import static org.briljantframework.data.dataframe.DataFrame.entry;
import static org.briljantframework.data.dataframe.DataFrames.sort;
import static org.briljantframework.data.dataframe.DataFrames.sortColumns;

import org.briljantframework.data.index.Index;
import org.briljantframework.data.series.IntSeries;
import org.briljantframework.data.series.Series;
import org.briljantframework.data.series.SeriesUtils;
import org.junit.Test;

public class DataFramesTest {

  @Test
  public void testSort_Asc() throws Exception {
    DataFrame df = DataFrame.fromEntries(entry("a", Series.of(10, 20, 30, 50)),
        entry("b", Series.of("A", "B", "C", "D")));

    df.setIndex(Index.of(10, 2, 3, 9));
    System.out.println(sort(df));

  }

  @Test
  public void testSummary() throws Exception {
    DataFrame df = createDataFrame();
    DataFrame summary = DataFrames.summary(df);
    System.out.println(summary);
  }

  protected DataFrame createDataFrame() {
    return DataFrame.of("a", Series.of(1, 2, 3, 4, 5, 6), "b",
        Series.of("a", "b", "b", "b", "e", "f"), "c", Series.of(1.1, 1.2, 1.3, 1.4, 1.5, 1.6));
  }

  @Test
  public void testToString() throws Exception {
    DataFrame df = DataFrame.of(
        "Abcdef", IntSeries.range(1000),
        "Bcdef", IntSeries.range(1000),
        "S", Series.repeat(1, 1000),
        "A", IntSeries.range(1000)
    );
    System.out.println(DataFrames
        .toString(sortColumns(sort(df, (a, b) -> -Integer.compare((int) a, (int) b)),
            (a, b) -> a.toString().compareTo(b.toString())), 9));

    Series of = Series.of(0.3, 0.33333, 0.299, 0.311);
    of.setIndex(Index.of("dsadsadsadsa", "dsadsadsadsadsadsa", "dsaa", "dsa"));
    System.out.println(SeriesUtils.toString(of, 2));

    // Index of = Index.of(0.1, 0.3, 0.001, 0.22, 0.33);
    // Index.Builder index = of.newCopyBuilder();
    // int[] array = {50, 30, 20, 10, 0};
    // index.sortIterationOrder(
    // (a, b) -> Double.compare((Double)of.getKey(a), (Double)of.getKey(b)));
    // System.out.println(index.build().entrySet());

    // Index of2 = index.build();
    // for (int i = 0; i < of2.size(); i++) {
    // System.out.println(of2.getKey(i));
    // }
  }

  @Test
  public void testTable() throws Exception {
    Series a = Series.of(1, 2, 3, 3, 3, 3, 5);
    Series b = Series.of(1, 2, 2, 2, 3, 3, 1);
    System.out.println(DataFrames.table(a, b));

    System.out.println(DataFrames.table(Series.of(1, 1, 1, 2, 2, 2), Series.of(1, 2, 3, 1, 2, 2)));


  }
}

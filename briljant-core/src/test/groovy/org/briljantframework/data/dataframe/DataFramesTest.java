/*
 * The MIT License (MIT)
 * 
 * Copyright (c) 2015 Isak Karlsson
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

import org.briljantframework.data.SortOrder;
import org.briljantframework.data.vector.IntVector;
import org.briljantframework.data.vector.Vector;
import org.junit.Test;

public class DataFramesTest {

  protected DataFrame createDataFrame() {
    return DataFrame.of("a", Vector.of(1, 2, 3, 4, 5, 6), "b",
        Vector.of("a", "b", "b", "b", "e", "f"), "c", Vector.of(1.1, 1.2, 1.3, 1.4, 1.5, 1.6));
  }

  @Test
  public void testSummary() throws Exception {
    DataFrame df = createDataFrame();
    DataFrame summary = DataFrames.summary(df);
    System.out.println(summary);
  }

  @Test
  public void testToString() throws Exception {
    DataFrame df =
        DataFrame.of("Abcdef", IntVector.range(1000), "Bcdef", IntVector.range(1000),
            "S", Vector.singleton(1, 1000), "A", IntVector.range(1000));
    System.out.println(DataFrames.toString(df, 9));

    System.out.println(IntVector.range(2000).sort(SortOrder.ASC));


  }

  @Test
  public void testTable() throws Exception {
    Vector a = Vector.of(1, 2, 3, 3, 3, 3, 5);
    Vector b = Vector.of(1, 2, 2, 2, 3, 3, 1);
    System.out.println(DataFrames.table(a, b));

    System.out.println(DataFrames.table(Vector.of(1, 1, 1, 2, 2, 2), Vector.of(1, 2, 3, 1, 2, 2)));


  }
}

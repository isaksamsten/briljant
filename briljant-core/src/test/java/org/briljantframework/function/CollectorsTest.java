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
package org.briljantframework.function;

import static org.briljantframework.data.Collectors.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.briljantframework.data.Collectors;
import org.briljantframework.data.dataframe.DataFrame;
import org.briljantframework.data.series.Series;
import org.junit.Test;

public class CollectorsTest {

  @Test
  public void testRepeat() throws Exception {
    Series vec = Series.of(1.0, 2.0, 3.0, 4.0, 5.0);
    Series vecX2 = vec.collect(repeat(2));
    assertEquals(vec.size() * 2, vecX2.size());
    assertEquals(Series.of(1.0, 2, 3, 4, 5, 1, 2, 3, 4, 5), vecX2);
  }

  @Test
  public void testValueCounts() throws Exception {
    Series vec = Series.of('a', 'b', 'c', 'd', 'e', 'e');
    Series counts = vec.collect(Character.class, valueCounts());
    assertEquals(2, counts.getInt('e'));
    assertEquals(1, counts.getInt('d'));
    assertTrue(counts.getIndex().containsAll(Arrays.asList('a', 'b', 'c', 'd', 'e')));
  }

  @Test
  public void testFactorize() throws Exception {
    Series v = Series.of("a", "b", "c", "c", "d", "d", "a");
    Series actual = v.collect(Collectors.factorize());
    assertEquals(Series.of(0, 1, 2, 2, 3, 3, 0), actual);
  }

  @Test
  public void testFillNa() throws Exception {
    Series a = Series.of("1", null, "3", "4");
    Series filled = a.collect(fillNa("2"));
    assertEquals(Series.of("1", "2", "3", "4"), filled);
  }

  @Test
  public void testCount() throws Exception {
    assertEquals(5, (int) Series.of("1", null, null, "4", "5", "6", "7").collect(count()));
  }

  @Test
  public void testMedian() throws Exception {
    assertEquals(20.0,
        Series.copyOf(new double[] {10, 20, 30, 50, 10}).collect(Double.class, median()), 0);
    assertEquals(15.0,
        Series.copyOf(new double[] {10, 10, 20, 30, 50, 0}).collect(Double.class, median()), 0);

  }

  @Test
  public void testToDataFrame() throws Exception {
    DataFrame df = Arrays.asList(Series.of(1, 2, 3), Series.of(1, 2, 3), Series.of(1, 2, 3))
        .stream().collect(Collectors.toDataFrame());
    System.out.println(df);

    assertEquals(2, df.getInt(1, 1));
  }
}

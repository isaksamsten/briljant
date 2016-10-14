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
package org.briljantframework.data.series;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.math3.complex.Complex;
import org.briljantframework.data.Logical;
import org.junit.Before;
import org.junit.Test;

public class VectorsTest {

  private Series vec6;
  private Series vec8;

  @Test
  public void testIndexSort() throws Exception {
    Series v = DoubleSeries.of(10, 23, 5, 31, 0);
    int[] order = SeriesUtils.indexSort(v);
    System.out.println(Arrays.toString(order));
    for (int i : order) {
      System.out.println(v.values().getDouble(i));
    }

  }

  @Before
  public void setUp() throws Exception {
    vec6 = DoubleSeries.of(1.0, 2, 3, 4, 5, 6);
    vec8 = Series.of("a", "sb", "cds", "qdsa", "fdasdsadsa", "dd", "r", "a");
  }

  @Test
  public void testInferType() throws Exception {
    assertEquals(Types.DOUBLE, Types.from(Double.class));
    assertEquals(Types.INT, Types.from(Integer.class));
    assertEquals(Types.LOGICAL, Types.from(Boolean.class));
    assertEquals(Types.LOGICAL, Types.from(Logical.class));
    assertEquals(Types.COMPLEX, Types.from(Complex.class));
    assertEquals(Types.STRING, Types.from(String.class));
    assertEquals(Types.DOUBLE, Types.from(Double.TYPE));
    assertEquals(Types.INT, Types.from(Integer.TYPE));
    assertEquals(Types.OBJECT, Types.from(null));
  }

  @Test
  public void testMode() throws Exception {
    // Series v = DoubleSeries.of("a", "b", "c", "d", "e", "f", "a");
    // assertEquals("a", Vec.mode(v));
  }

  @Test
  public void testSplitExact() throws Exception {
    Collection<Series> chunks = SeriesUtils.split(vec6, 3);
    List<Series> listChunks = new ArrayList<>(chunks);

    assertEquals(3, chunks.size());
    assertEquals(DoubleSeries.of(1, 2), listChunks.get(0));
    assertEquals(DoubleSeries.of(3, 4), listChunks.get(1));
    assertEquals(DoubleSeries.of(5, 6), listChunks.get(2));
  }

  @Test
  public void testSplitSingleton() throws Exception {
    Collection<Series> chunks = SeriesUtils.split(vec6, 6);
    List<Series> listChunks = new ArrayList<>(chunks);

    assertEquals(1, chunks.size());
    assertEquals(DoubleSeries.of(1, 2, 3, 4, 5, 6), listChunks.get(0));
  }

  @Test
  public void testSplitUneven() throws Exception {
    Collection<Series> chunks = SeriesUtils.split(vec6, 4);
    List<Series> listChunks = new ArrayList<>(chunks);

    assertEquals(4, chunks.size());
    assertEquals(DoubleSeries.of(1, 2), listChunks.get(0));
    assertEquals(DoubleSeries.of(3, 4), listChunks.get(1));
    assertEquals(Series.of(5.0), listChunks.get(2));
    assertEquals(Series.of(6.0), listChunks.get(3));
  }
}

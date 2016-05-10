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
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.briljantframework.array.IntArray;
import org.briljantframework.data.Is;
import org.briljantframework.data.Na;
import org.briljantframework.data.index.HashIndex;
import org.briljantframework.data.reader.DataEntry;
import org.briljantframework.data.reader.StringDataEntry;
import org.junit.Test;

public abstract class VectorBuilderTest {

  @Test
  public void testSetNAWithIndex() throws Exception {
    Series.Builder builder = getBuilder();
    builder.loc().setNA(0);
    builder.addNA();
    builder.loc().setNA(3);

    Series series = builder.build();
    assertEquals(4, series.size());
    for (int i = 0; i < series.size(); i++) {
      assertTrue(i + " double is NA", Is.NA(series.loc().getDouble(i)));
      assertTrue(i + " int is NA", Is.NA(series.loc().getInt(i)));
      assertTrue(i + " Object is NA", Is.NA(series.loc().get(Object.class, i)));

      Object key = i;
      assertTrue(i + " double is NA", Is.NA(series.getDouble(key)));
      assertTrue(i + " int is NA", Is.NA(series.getInt(key)));
      assertTrue(i + " Object is NA", Is.NA(series.get(Object.class, key)));
    }
  }

  abstract Series.Builder getBuilder();

  @Test
  public void testSetNAWithKey() throws Exception {
    Series.Builder builder = getBuilder();
    builder.setNA("a");
    builder.setNA("b");
    builder.setNA("c");
    builder.addNA();

    Series series = builder.build();
    assertEquals(4, series.size());
    for (Object s : Arrays.asList("a", "b", "c", 3)) {
      assertTrue(s + " double is NA", Is.NA(series.getDouble(s)));
      assertTrue(s + " int is NA", Is.NA(series.getInt(s)));
      assertTrue(s + " Object is NA", Is.NA(series.get(Object.class, s)));
    }
  }

  @Test
  public void testAddNA() throws Exception {
    final int size = 10;
    Series.Builder builder = getBuilder();
    for (int i = 0; i < size; i++) {
      builder.addNA();
    }

    Series series = builder.build();
    assertEquals(size, series.size());
    for (int i = 0; i < size; i++) {
      assertTrue(i + " double is NA", Is.NA(series.loc().getDouble(i)));
      assertTrue(i + " int is NA", Is.NA(series.loc().getInt(i)));
      assertTrue(i + " Object is NA", Is.NA(series.loc().get(Object.class, i)));

      Object key = i;
      assertTrue(i + " double is NA", Is.NA(series.getDouble(key)));
      assertTrue(i + " int is NA", Is.NA(series.getInt(key)));
      assertTrue(i + " Object is NA", Is.NA(series.get(Object.class, key)));
    }
  }

  @Test
  public void testAdd() throws Exception {
    List<Integer> expected = Arrays.asList(Na.INT, Na.INT, 10, Na.INT, 1, 2);

    Series.Builder builder = getBuilder();
    expected.forEach(builder::add);

    Series series = builder.build();
    assertEquals(expected.size(), series.size());

    for (int i = 0; i < expected.size(); i++) {
      Object key = i;
      Integer expectedValue = expected.get(i);
      assertEquals(expectedValue, series.loc().get(Integer.class, i));
      assertEquals(expectedValue, series.get(Integer.class, key));
    }
  }

  @Test
  public void testAdd1() throws Exception {
    Series expected = Series.of(1, 2, 3, 4);
    Series.Builder builder = getBuilder();
    for (int i = 0; i < expected.size(); i++) {
      builder.addFromLocation(expected, i);
    }

    Series series = builder.build();
    assertEquals(expected.size(), series.size());
    for (int i = 0; i < expected.size(); i++) {
      Object key = i;
      Integer expectedValue = expected.loc().get(Integer.class, i);
      assertEquals(expectedValue, series.loc().get(Integer.class, i));
      assertEquals(expectedValue, series.get(Integer.class, key));
    }
  }

  @Test
  public void testSet() throws Exception {
    Series.Builder builder = getBuilder();

    builder.loc().set(0, Integer.valueOf(10));
    builder.loc().set(1, Double.valueOf(3));
    builder.loc().set(3, Integer.valueOf(2));

    List<Integer> expected = Arrays.asList(10, 3, Na.INT, 2);

    Series series = builder.build();
    assertEquals(4, series.size());
    for (int i = 0; i < series.size(); i++) {
      Object key = i;
      int expectedValue = expected.get(i);
      assertEquals(expectedValue, series.loc().getInt(i));
      assertEquals(expectedValue, series.getInt(key));
    }
  }

  @Test
  public void testSetWithKey() throws Exception {
    List<Object> keys = Arrays.asList(0, "a", "10", 100, "c", "d", null);
    Series.Builder builder = getBuilder();
    for (int i = 0; i < keys.size(); i++) {
      builder.set(keys.get(i), i);
    }

    Series series = builder.build();
    for (int i = 0; i < keys.size(); i++) {
      assertEquals(i, series.getInt(keys.get(i)));
      assertEquals(i, series.loc().getInt(i));
    }

  }

  @Test
  public void testSetFromVectorFromIndex() throws Exception {
    Series expected = Series.of(1, 2, 3, 4, 5);
    Series.Builder builder = getBuilder();
    for (int i = 0; i < expected.size(); i++) {
      builder.loc().setFrom(i, expected, i);
    }

    Series series = builder.build();
    for (int i = 0; i < expected.size(); i++) {
      assertEquals(expected.loc().get(Integer.class, i), series.loc().get(Integer.class, i));
    }

  }

  @Test
  public void testSetFromVectorFromKey() throws Exception {
    List<Object> index = Arrays.asList("a", "b", "c");
    Series expected = Series.of(10, 20, 30);
    expected.setIndex(HashIndex.of(index));

    Series.Builder builder = getBuilder();
    for (int i = 0; i < index.size(); i++) {
      builder.loc().setFromKey(i, expected, index.get(i));
    }

    Series actual = builder.build();
    for (int i = 0; i < index.size(); i++) {
      Object key = index.get(i);
      assertEquals(expected.get(Integer.class, key), actual.loc().get(Integer.class, i));
    }
  }

  @Test
  public void testSetFromVectorWithKeyFromIndex() throws Exception {
    List<Object> index = Arrays.asList("a", "b", "c");
    Series expected = Series.of(10, 20, 30);
    expected.setIndex(HashIndex.of(index));

    Series.Builder builder = getBuilder();
    for (int i = 0; i < index.size(); i++) {
      Object key = index.get(i);
      builder.setFromLocation(key, expected, i);
    }

    Series actual = builder.build();
    for (int i = 0; i < index.size(); i++) {
      Object key = index.get(i);
      assertEquals(expected.loc().get(Integer.class, i), actual.get(Integer.class, key));
    }
  }

  @Test
  public void testSetFromVectorWithKeyFromKey() throws Exception {
    List<Object> index = Arrays.asList("a", "b", "c");
    Series expected = Series.of(10, 20, 30);
    expected.setIndex(HashIndex.of(index));

    Series.Builder builder = getBuilder();
    for (Object key : index) {
      builder.setFrom(key, expected, key);
    }

    Series actual = builder.build();
    for (Object key : index) {
      assertEquals(expected.get(Integer.class, key), actual.get(Integer.class, key));
    }
  }

  @Test
  public void testOverwriteObjectLocationIndex() throws Exception {
    Series.Builder builder = getBuilder();
    builder.loc().setInt(0, 1);
    builder.loc().setInt(3, 10);
    builder.loc().setInt(3, 100);

    Series expected = Series.of(1, null, null, 100);
    Series actual = builder.build();
    assertEquals(expected.asList(Integer.class), actual.asList(Integer.class));
  }

  @Test
  public void testGetSubVectorWithCorrectKeysUsingLocationIndexes() throws Exception {
    Series.Builder builder = getBuilder();
    List<Object> keys = Arrays.asList(22, 300, 2, 10, 1000);
    for (Object key : keys) {
      builder.set(key, key);
    }
    Series series = builder.build();
    Series selected = series.loc().get(IntArray.of(0, 1, 4));

    assertEquals(22, selected.getInt(22));
    assertEquals(300, selected.getInt(300));
    assertEquals(1000, selected.getInt(1000));
  }

  @Test
  public void testGetSubVectorWithKeysUsingBitVector() throws Exception {
    Series select = Series.of(0, 1, 1, 0);
    select.setIndex(HashIndex.of("a", "b", "c", "d"));
    List<Object> keys = Arrays.asList("a", "b", "c", "d");

    // Series.Builder builder = getBuilder();
    // for (int i = 0; i < select.size(); i++) {
    // builder.set(keys.get(i), i * 10);
    // }

    // Series actual = builder.build().get(select);
    //
    // assertEquals(10, actual.getAsInt("b"));
    // assertEquals(20, actual.getAsInt("c"));
  }

  @Test
  public void testBuildVectorUsingDataEntries() throws Exception {
    Series.Builder builder = getBuilder();
    DataEntry entry = new StringDataEntry("1", "2", "4", "10");
    while (entry.hasNext()) {
      builder.read(entry);
    }

    Series v = builder.build();
    if (v instanceof ObjectSeries) {
      assertEquals(Arrays.asList("1", "2", "4", "10"), v.asList(String.class));
    } else {
      assertEquals(Arrays.asList(1, 2, 4, 10), v.asList(Integer.class));
    }
  }

  // @Test
  // public void testAddAll1() throws Exception {
  // double[] array = new double[1000000];
  // Arrays.fill(array, 20);
  //
  // List<Double> list = new ArrayList<>();
  // long start = System.nanoTime();
  // for (int j = 0; j < 100; j++) {
  // for (double i : array) {
  // list.plus(i);
  // }
  // }
  //
  // double end = (System.nanoTime() - start) / 1e6 / 100;
  // System.out.println(list.size() + " " + end);
  // }

  // @Test
  // public void testAddAll2() throws Exception {
  // double[] array = new double[1000000];
  // Arrays.fill(array, 20);
  // Series.Builder builder = getBuilder();
  // long start = System.nanoTime();
  // for (int j = 0; j < 100; j++) {
  // for (double i : array) {
  // builder.addNA();
  // }
  // }
  // double end = (System.nanoTime() - start) / 1e6 / 100;
  // Series v = builder.build();
  // System.out.println(v.size() + " " + end);
  // }

  @Test
  public void testAddAll3() throws Exception {

  }

  @Test
  public void testRemove() throws Exception {

  }

  @Test
  public void testRemove1() throws Exception {

  }
}

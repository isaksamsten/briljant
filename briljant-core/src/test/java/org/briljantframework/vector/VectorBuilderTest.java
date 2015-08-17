/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Isak Karlsson
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.briljantframework.vector;

import org.briljantframework.dataframe.HashIndex;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public abstract class VectorBuilderTest {

  abstract Vector.Builder getBuilder();

  @Test
  public void testSetNAWithIndex() throws Exception {
    Vector.Builder builder = getBuilder();
    builder.setNA(0);
    builder.addNA();
    builder.setNA(3);

    Vector vector = builder.build();
    assertEquals(4, vector.size());
    for (int i = 0; i < vector.size(); i++) {
      assertTrue(i + " double is NA", Is.NA(vector.getAsDouble(i)));
      assertTrue(i + " int is NA", Is.NA(vector.getAsInt(i)));
      assertTrue(i + " Object is NA", Is.NA(vector.get(Object.class, i)));

      Object key = i;
      assertTrue(i + " double is NA", Is.NA(vector.getAsDouble(key)));
      assertTrue(i + " int is NA", Is.NA(vector.getAsInt(key)));
      assertTrue(i + " Object is NA", Is.NA(vector.get(Object.class, key)));
    }
  }

  @Test
  public void testSetNAWithKey() throws Exception {
    Vector.Builder builder = getBuilder();
    builder.setNA("a");
    builder.setNA("b");
    builder.setNA("c");
    builder.addNA();

    Vector vector = builder.build();
    assertEquals(4, vector.size());
    for (Object s : Arrays.asList("a", "b", "c", 3)) {
      assertTrue(s + " double is NA", Is.NA(vector.getAsDouble(s)));
      assertTrue(s + " int is NA", Is.NA(vector.getAsInt(s)));
      assertTrue(s + " Object is NA", Is.NA(vector.get(Object.class, s)));
    }
  }

  @Test
  public void testAddNA() throws Exception {
    final int size = 10;
    Vector.Builder builder = getBuilder();
    for (int i = 0; i < size; i++) {
      builder.addNA();
    }

    Vector vector = builder.build();
    assertEquals(size, vector.size());
    for (int i = 0; i < size; i++) {
      assertTrue(i + " double is NA", Is.NA(vector.getAsDouble(i)));
      assertTrue(i + " int is NA", Is.NA(vector.getAsInt(i)));
      assertTrue(i + " Object is NA", Is.NA(vector.get(Object.class, i)));

      Object key = i;
      assertTrue(i + " double is NA", Is.NA(vector.getAsDouble(key)));
      assertTrue(i + " int is NA", Is.NA(vector.getAsInt(key)));
      assertTrue(i + " Object is NA", Is.NA(vector.get(Object.class, key)));
    }
  }

  @Test
  public void testAdd() throws Exception {
    List<Integer> expected = Arrays.asList(Na.INT, Na.INT, 10, Na.INT, 1, 2);

    Vector.Builder builder = getBuilder();
    expected.forEach(builder::add);

    Vector vector = builder.build();
    assertEquals(expected.size(), vector.size());

    for (int i = 0; i < expected.size(); i++) {
      Object key = i;
      Integer expectedValue = expected.get(i);
      assertEquals(expectedValue, vector.get(Integer.class, i));
      assertEquals(expectedValue, vector.get(Integer.class, key));
    }
  }

  @Test
  public void testAdd1() throws Exception {
    Vector expected = Vector.of(1, 2, 3, 4);
    Vector.Builder builder = getBuilder();
    for (int i = 0; i < expected.size(); i++) {
      builder.add(expected, i);
    }

    Vector vector = builder.build();
    assertEquals(expected.size(), vector.size());
    for (int i = 0; i < expected.size(); i++) {
      Object key = i;
      Integer expectedValue = expected.get(Integer.class, i);
      assertEquals(expectedValue, vector.get(Integer.class, i));
      assertEquals(expectedValue, vector.get(Integer.class, key));
    }
  }

  @Test
  public void testSet() throws Exception {
    Vector.Builder builder = getBuilder();

    builder.set(0, Integer.valueOf(10));
    builder.set(1, Double.valueOf(3));
    builder.set(3, Integer.valueOf(2));

    List<Integer> expected = Arrays.asList(10, 3, Na.INT, 2);

    Vector vector = builder.build();
    assertEquals(4, vector.size());
    for (int i = 0; i < vector.size(); i++) {
      Object key = i;
      int expectedValue = expected.get(i);
      assertEquals(expectedValue, vector.getAsInt(i));
      assertEquals(expectedValue, vector.getAsInt(key));
    }
  }

  @Test
  public void testSetWithKey() throws Exception {
    List<Object> keys = Arrays.asList(0, "a", "10", 100, "c", "d", null);
    Vector.Builder builder = getBuilder();
    for (int i = 0; i < keys.size(); i++) {
      builder.set(keys.get(i), i);
    }

    Vector vector = builder.build();
    for (int i = 0; i < keys.size(); i++) {
      assertEquals(i, vector.getAsInt(keys.get(i)));
      assertEquals(i, vector.getAsInt(i));
    }

  }

  @Test
  public void testSetFromVectorFromIndex() throws Exception {
    Vector expected = Vector.of(1, 2, 3, 4, 5);
    Vector.Builder builder = getBuilder();
    for (int i = 0; i < expected.size(); i++) {
      builder.set(i, expected, i);
    }

    Vector vector = builder.build();
    for (int i = 0; i < expected.size(); i++) {
      assertEquals(expected.get(Integer.class, i), vector.get(Integer.class, i));
    }

  }

  @Test
  public void testSetFromVectorFromKey() throws Exception {
    List<Object> index = Arrays.asList("a", "b", "c");
    Vector expected = Vector.of(10, 20, 30);
    expected.setIndex(HashIndex.from(index));

    Vector.Builder builder = getBuilder();
    for (int i = 0; i < index.size(); i++) {
      builder.set(i, expected, index.get(i));
    }

    Vector actual = builder.build();
    for (int i = 0; i < index.size(); i++) {
      Object key = index.get(i);
      assertEquals(expected.get(Integer.class, key), actual.get(Integer.class, i));
    }
  }

  @Test
  public void testSetFromVectorWithKeyFromIndex() throws Exception {
    List<Object> index = Arrays.asList("a", "b", "c");
    Vector expected = Vector.of(10, 20, 30);
    expected.setIndex(HashIndex.from(index));

    Vector.Builder builder = getBuilder();
    for (int i = 0; i < index.size(); i++) {
      Object key = index.get(i);
      builder.set(key, expected, i);
    }

    Vector actual = builder.build();
    for (int i = 0; i < index.size(); i++) {
      Object key = index.get(i);
      assertEquals(expected.get(Integer.class, i), actual.get(Integer.class, key));
    }
  }

  @Test
  public void testSetFromVectorWithKeyFromKey() throws Exception {
    List<Object> index = Arrays.asList("a", "b", "c");
    Vector expected = Vector.of(10, 20, 30);
    expected.setIndex(HashIndex.from(index));

    Vector.Builder builder = getBuilder();
    for (int i = 0; i < index.size(); i++) {
      Object key = index.get(i);
      builder.set(key, expected, key);
    }

    Vector actual = builder.build();
    for (int i = 0; i < index.size(); i++) {
      Object key = index.get(i);
      assertEquals(expected.get(Integer.class, key), actual.get(Integer.class, key));
    }
  }

  @Test
  public void testAddAll() throws Exception {

  }

  @Test
  public void testAddAll1() throws Exception {

  }

  @Test
  public void testAddAll2() throws Exception {

  }

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
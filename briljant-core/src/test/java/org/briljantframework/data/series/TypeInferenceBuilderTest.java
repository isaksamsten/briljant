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

import java.util.Arrays;
import java.util.Collections;

import org.briljantframework.data.Na;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Isak Karlsson <isak-kar@dsv.su.se>
 */
public class TypeInferenceBuilderTest {

  Series.Builder builder;

  @Before
  public void setUp() throws Exception {
    builder = new TypeInferenceBuilder();
  }

  @Test
  public void testSetNA_NoneIntegerKey() throws Exception {
    builder.setNA("key");
    builder.add(1);
    Series series = builder.build();
    assertEquals(Type.OBJECT, series.getType());
    assertEquals(2, series.size());
  }

  @Test
  public void testAddNA() throws Exception {
    builder.addNA().add(1);
    Series series = builder.build();
    assertEquals(Type.INT, series.getType());
    assertEquals(2, series.size());
    assertEquals(Arrays.asList(Na.INT, 1), series.asList(Integer.class));
  }

  @Test
  public void testAdd_int() throws Exception {
    builder.add(1);
    Series series = builder.build();
    assertEquals(Type.INT, series.getType());
    assertEquals(1, series.size());
    assertEquals(Collections.singletonList(1), series.asList(Integer.class));
  }

  @Test
  public void testAdd_double() throws Exception {
    builder.add(1.0);
    Series series = builder.build();
    assertEquals(Type.DOUBLE, series.getType());
    assertEquals(1, series.size());
    assertEquals(Collections.singletonList(1.0), series.asList(Double.class));
  }

  @Test
  public void testAdd_ReferenceType() throws Exception {
    builder.add("hello");
    Series series = builder.build();
    assertEquals(Type.of(String.class), series.getType());
    assertEquals(1, series.size());
    assertEquals(Collections.singletonList("hello"), series.asList(String.class));
  }

  @Test
  public void testAdd1() throws Exception {

  }

  @Test
  public void testSet() throws Exception {

  }

  @Test
  public void testSet1() throws Exception {

  }

  @Test
  public void testSet2() throws Exception {

  }

  @Test
  public void testAdd2() throws Exception {

  }

  @Test
  public void testAddFromVector() throws Exception {

  }

  @Test
  public void testAdd4() throws Exception {

  }

  @Test
  public void testAddAll() throws Exception {

  }

  @Test
  public void testRemove() throws Exception {

  }

  @Test
  public void testRead() throws Exception {

  }

  @Test
  public void testReadAll() throws Exception {

  }

  @Test
  public void testSize_addNA() throws Exception {

  }

  @Test
  public void testSize_setNA() throws Exception {

  }

  @Test
  public void testSize_setLocNA() throws Exception {

  }
}

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

package org.briljantframework.data.index;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public abstract class IndexBuilderTest {

  abstract Index.Builder getBuilder();

  @Test
  public void testContains() throws Exception {
    Index.Builder builder = getBuilder();
    builder.add(2231321);
    builder.add(23);
    builder.add(323);

    assertTrue(builder.contains(23));
    assertFalse(builder.contains(52543));
  }

  @Test
  public void testGetLocation() throws Exception {
    Index.Builder builder = getBuilder();
    builder.add(0);
    builder.add(1);
    builder.add(2);

    assertEquals(0, builder.getLocation(0));
    assertEquals(1, builder.getLocation(1));
    assertEquals(2, builder.getLocation(2));
  }

  @Test
  public void testGetLocation_AfterRemove() throws Exception {
    Index.Builder builder = getBuilder();
    builder.add("a");
    builder.add("b");
    builder.add("c");
    builder.add("d");
    builder.remove(2);
    assertEquals(0, builder.getLocation("a"));
    assertEquals(2, builder.getLocation("d"));
  }

  @Test
  public void testGetKey() throws Exception {

  }

  @Test
  public void testAdd() throws Exception {

  }

  @Test
  public void testAdd1() throws Exception {

  }

  @Test
  public void testSort() throws Exception {

  }

  @Test
  public void testSort1() throws Exception {

  }

  @Test
  public void testExtend() throws Exception {

  }

  @Test
  public void testBuild() throws Exception {

  }

  @Test
  public void testSize() throws Exception {

  }

  @Test
  public void testRemove() throws Exception {

  }
}

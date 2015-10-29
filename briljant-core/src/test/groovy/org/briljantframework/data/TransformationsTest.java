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

package org.briljantframework.data;

import org.briljantframework.data.vector.Vector;
import org.junit.Assert;
import org.junit.Test;

public class TransformationsTest {

  @Test
  public void testLessThan() throws Exception {
    Vector a = Vector.of(1, 2, 3, 4, 4);
    Assert.assertEquals(Vector.of(true, false, false, false, false),
        a.map(Integer.class, Transformations.lessThan(2)));
  }

  @Test
  public void testGreaterThan() throws Exception {
    Vector a = Vector.of(1, 2, 3, 4, 4);
    Assert.assertEquals(Vector.of(false, false, true, true, true),
        a.map(Integer.class, Transformations.greaterThan(2)));
  }

  @Test
  public void testLessThan1() throws Exception {

  }

  @Test
  public void testGreaterThan1() throws Exception {

  }

  @Test
  public void testEqual() throws Exception {

  }

  @Test
  public void testEqual1() throws Exception {

  }

  @Test
  public void testReplaceNA() throws Exception {

  }

  @Test
  public void testClip() throws Exception {

  }
}

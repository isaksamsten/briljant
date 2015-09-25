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

package org.briljantframework.data.vector;

import org.apache.commons.math3.complex.Complex;
import org.briljantframework.data.Logical;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class VectorsTest {

  private Vector vec6;
  private Vector vec8;

  @Test
  public void testIndexSort() throws Exception {
    Vector v = Vector.of(10, 23, 5, 31, 0);
    int[] order = Vectors.indexSort(v);
    System.out.println(Arrays.toString(order));
    for (int i : order) {
      System.out.println(v.loc().getAsDouble(i));
    }

  }

  @Before
  public void setUp() throws Exception {
    vec6 = Vector.of(1.0, 2, 3, 4, 5, 6);
    vec8 = Vector.of("a", "sb", "cds", "qdsa", "fdasdsadsa", "dd", "r", "a");
  }

  @Test
  public void testInferType() throws Exception {
    assertEquals(VectorType.DOUBLE, VectorType.of(Double.class));
    assertEquals(VectorType.INT, VectorType.of(Integer.class));
    assertEquals(VectorType.LOGICAL, VectorType.of(Boolean.class));
    assertEquals(VectorType.LOGICAL, VectorType.of(Logical.class));
    assertEquals(VectorType.COMPLEX, VectorType.of(Complex.class));
    assertEquals(VectorType.STRING, VectorType.of(String.class));
    assertEquals(VectorType.DOUBLE, VectorType.of(Double.TYPE));
    assertEquals(VectorType.INT, VectorType.of(Integer.TYPE));
    assertEquals(VectorType.OBJECT, VectorType.of(null));
  }

  @Test
  public void testMode() throws Exception {
//    Vector v = Vector.of("a", "b", "c", "d", "e", "f", "a");
//    assertEquals("a", Vec.mode(v));
  }

  @Test
  public void testSplitExact() throws Exception {
    Collection<Vector> chunks = Vectors.split(vec6, 3);
    List<Vector> listChunks = new ArrayList<>(chunks);

    assertEquals(3, chunks.size());
    assertEquals(Vector.of(1, 2), listChunks.get(0));
    assertEquals(Vector.of(3, 4), listChunks.get(1));
    assertEquals(Vector.of(5, 6), listChunks.get(2));
  }

  @Test
  public void testSplitSingleton() throws Exception {
    Collection<Vector> chunks = Vectors.split(vec6, 6);
    List<Vector> listChunks = new ArrayList<>(chunks);

    assertEquals(1, chunks.size());
    assertEquals(Vector.of(1, 2, 3, 4, 5, 6), listChunks.get(0));
  }

  @Test
  public void testSplitUneven() throws Exception {
    Collection<Vector> chunks = Vectors.split(vec6, 4);
    List<Vector> listChunks = new ArrayList<>(chunks);

    assertEquals(4, chunks.size());
    assertEquals(Vector.of(1, 2), listChunks.get(0));
    assertEquals(Vector.of(3, 4), listChunks.get(1));
    assertEquals(Vector.singleton(5), listChunks.get(2));
    assertEquals(Vector.singleton(6), listChunks.get(3));
  }
}

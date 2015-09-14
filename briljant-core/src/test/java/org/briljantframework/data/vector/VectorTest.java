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

import org.apache.commons.math3.random.UniformRandomGenerator;
import org.apache.commons.math3.random.Well1024a;
import org.briljantframework.data.SortOrder;
import org.junit.Assert;
import org.junit.Test;

public class VectorTest {

  @Test
  public void testSort() throws Exception {
    Vector.Builder builder = Vector.Builder.of(Double.class);
    UniformRandomGenerator gen = new UniformRandomGenerator(new Well1024a());
    for (int i = 0; i < 1000_000; i++) {
      builder.add(gen.nextNormalizedDouble());
    }
    Vector vector = builder.build();
    System.out.println(vector.sort(SortOrder.DESC));
  }

  @Test
  public void testHead() throws Exception {
    Vector a = new TypeInferenceVectorBuilder()
        .set("a", 10)
        .set("b", 100)
        .set("c", 1)
        .set("d", 11)
        .build();

//    Vector head = a.head(2);
    System.out.println(a);

    System.out.println(a.sort(SortOrder.DESC));
  }

  @Test
  public void testTestSort() throws Exception {
    Vector a = new TypeInferenceVectorBuilder()
        .set(40, 3)
        .set(30, 2)
        .set(20, 4)
        .set(10, 1)
        .build();

    Vector v = a.sort(SortOrder.DESC);
    System.out.println(v.asList(Object.class));
    System.out.println(v);
//    for (int i = 0; i < v.size(); i++) {
//      Assert.assertEquals(i + 1, v.loc().getAsInt(i));
//    }
  }
}

/**
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

import org.briljantframework.data.SortOrder;
import org.junit.Assert;
import org.junit.Test;

public abstract class VectorTest {

  protected abstract Vector.Builder getBuilder();

  @Test
  public void testSet_BooleanArray() throws Exception {
    Vector a = getBuilder().addAll(1, 2, 3, 4, 5).build();
    Vector expected = getBuilder().addAll(1, 2, 320, 320, 320).build();
    Vector actual = a.set(a.where(Double.class, v -> v > 2), 320);

    System.out.println(IntVector.range(2000).sort(SortOrder.ASC));
    Assert.assertEquals(expected, actual);
  }

  @Test
  public void testHead() throws Exception {
    Vector a =
        new TypeInferenceVectorBuilder().set("a", 10).set("b", 100).set("c", 1).set("d", 11)
            .build();

    // Vector head = a.head(2);
    System.out.println(a);

    Vector sort = a.sort(SortOrder.ASC);
    System.out.println(sort);
//    System.out.println(sort.loc().get(0));
//    System.out.println(a.sort(Boolean.class));
  }

  @Test
  public void testTestSort() throws Exception {
    Vector a = new TypeInferenceVectorBuilder().set(40, 3).set(30, 2).set(20, 4).set(10, 1).build();

    Vector v = a.sort(SortOrder.DESC);
    System.out.println(v.toList(Object.class));
    System.out.println(v);
    // for (int i = 0; i < v.size(); i++) {
    // Assert.assertEquals(i + 1, v.loc().getAsInt(i));
    // }
  }
}

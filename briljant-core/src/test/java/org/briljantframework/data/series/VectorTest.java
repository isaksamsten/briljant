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

import org.briljantframework.array.Arrays;
import org.briljantframework.data.SortOrder;
import org.briljantframework.data.index.Index;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public abstract class VectorTest {

  @Test
  public void testArrayMethods() throws Exception {
    Series s = Series.of("dsadsa", 20.0, 30.0, 40.0);
    s.setIndex(Index.of("a", "b", "c", "d"));
    System.out.println(s.getIndex());

    s = s.reindex(Index.of("AAA", "BBB", "CCC", "DDD"));

    Series b = s.reshape(2, 2);
    System.out.println(b.getIndex());

    System.out.println(b);

    for (Object key : s.getIndex()) {
      System.out.println(key + " " + b.get(key) + b.get(key).getClass());
    }

    System.out.println(b.asArray());

    List<Series> vsplit = Arrays.vsplit(b, 2);
    System.out.println(vsplit.get(0));
  }

  @Test
  public void testSet_BooleanArray() throws Exception {
    Series a = getBuilder().addAll(Series.of(1, 2, 3, 4, 5)).build();
    Series expected = getBuilder().addAll(Series.of(1, 2, 320, 320, 320)).build();
    a.set(a.where(Double.class, v -> v > 2), 320);

    System.out.println(IntSeries.range(2000).sort(SortOrder.ASC));
    Assert.assertEquals(expected, a);
  }

  protected abstract Series.Builder getBuilder();

  @Test
  public void testHead() throws Exception {
    Series a =
        new TypeInferenceBuilder().set("a", 10).set("b", 100).set("c", 1).set("d", 11).build();

    // Series head = a.head(2);
    System.out.println(a);

    Series sort = a.sort(SortOrder.ASC);
    System.out.println(sort);
    // System.out.println(sort.loc().get(0));
    // System.out.println(a.sort(Boolean.class));
  }

  @Test
  public void testTestSort() throws Exception {
    Series a = new TypeInferenceBuilder().set(40, 3).set(30, 2).set(20, 4).set(10, 1).build();

    Series v = a.sort(SortOrder.DESC);
    System.out.println(v.asList(Object.class));
    System.out.println(v);
    // for (int i = 0; i < v.size(); i++) {
    // Assert.assertEquals(i + 1, v.loc().getAsInt(i));
    // }
  }
}

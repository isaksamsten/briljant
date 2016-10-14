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

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.briljantframework.data.SortOrder;
import org.briljantframework.data.index.Index;
import org.junit.Ignore;
import org.junit.Test;

public abstract class SeriesTest {

  protected abstract Series.Builder getBuilder();

  @Test
  public void testDrop() throws Exception {
    Series expected = getBuilder().addAll(java.util.Arrays.asList(1, 2, 3, 4)).build();
    expected = expected.reindex(Index.of(0, 1, 3, 4));
    Series x = getBuilder().addAll(java.util.Arrays.asList(1, 2, 3, 3, 4)).build();

    Series actual = x.drop(2);
    assertEquals(expected, actual);
  }

  @Test(expected = NoSuchElementException.class)
  public void testDropNonExistentKeyShouldThrowNoSuchElementException() throws Exception {
    getBuilder().set("A", 10).set("B", 20).build().drop("C");
  }

  @Test
  @Ignore
  public void testAdd() throws Exception {
    Series expected = getBuilder().set("A", 10).set("B", 20).set(2, 30).build();
    Series actual = getBuilder().set("A", 10).set("B", 20).build();
    actual.values().add(30);
    assertEquals(expected, actual);
  }

  @Test
  public void testSet_BooleanArray() throws Exception {
    Series a = getBuilder().setAll(Series.of(1, 2, 3, 4, 5)).build();
    Series expected = getBuilder().setAll(Series.of(1, 2, 320, 320, 320)).build();
    a.set(a.where(Double.class, v -> v > 2), 320);
    assertEquals(expected, a);
  }

  @Test
  public void testHead() throws Exception {
    Series a = getBuilder().set("a", 10).set("b", 100).set("c", 1).set("d", 11).build();
    Series sort = a.sort(SortOrder.ASC);
    Iterator<Object> index = sort.index().iterator();
    List<String> sorted = java.util.Arrays.asList("c", "a", "d", "b");
    for (int i = 0; i < sort.size(); i++) {
      assertEquals(sorted.get(i), index.next());
    }
  }
}

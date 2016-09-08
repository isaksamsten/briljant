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

import static org.junit.Assert.*;

import java.util.Arrays;

import org.briljantframework.array.AbstractBaseArrayTest;
import org.briljantframework.array.ArrayTest;
import org.briljantframework.data.index.Index;
import org.junit.Test;

/**
 * Created by isak on 5/4/16.
 */
public class ObjectSeriesTest extends AbstractBaseArrayTest<Series> {

  protected Series seriesOf(Object... values) {
    Series.Builder builder = new ObjectSeries.Builder(Object.class);
    builder.addAll(Arrays.asList(values));
    return builder.build();
  }

  @Test
  public void testGetAll() throws Exception {
    Series x = seriesOf("A", "B", "C", "D");
    Series y = x.getAll(Arrays.asList(1, 2));
    assertEquals(seriesOf("B", "C").reindex(Index.of(1, 2)), y);
  }

  @Test
  public void testDropAll() throws Exception {
    Series x = seriesOf(1, 2, 3, 4, 5);
    Series y = x.dropAll(Index.of(2, 3));
    assertEquals(seriesOf(1, 2, 5).reindex(Index.of(0, 1, 4)), y);
  }
  
  @Override
  protected ArrayTest<Series> createSetSingleIndexTest() {
    return null;
//    return new ArrayTest<Series>() {
//      @Override
//      public List<Series> getTestData() {
//        Series a = seriesOf(1, 2, 3, 4);
//        Series b = seriesOf(5, 6, 7, 8);
//        //@formatter:off
//        return Arrays.asList(a, b,
//            a.reshape(2,2),
//            b.reshape(2,2),
//
//            a.reshape(2,1,2),
//            b.reshape(2,1,2)
//        );
//        //@formatter:on
//      }
//
//      @Override
//      public void test(List<Series> actual) {
//        assertEquals(5, actual.get(0).get(1));
//        assertEquals(5, actual.get(1).loc().get(0, 1));
//        assertEquals(5, actual.get(2).loc().get(0, 0, 1));
//      }
//    };
  }

  @Override
  protected ArrayTest<Series> createSetMatrixIndexTest() {
    return null;
//    return new ArrayTest<Series>() {
//      @Override
//      public List<Series> getTestData() {
//        //@formatter:off
//        return java.util.Arrays.asList(
//            seriesOf(1, 2, 3, 4),
//
//            seriesOf(1, 2, 3, 4, 5, 6, 7, 8).reshape(2,4),
//            seriesOf("a", "a", "a", "a", "a", "a", "a", "a").reshape(2, 4),
//
//            new ObjectSeries(Types.OBJECT, 2, 1, 2)
//        );
//        //@formatter:on
//      }
//
//      @Override
//      public void test(List<Series> actual) {
//        for (int i = 0; i < actual.get(0).size(); i++) {
//          assertEquals("a", actual.get(0).loc().get(i));
//        }
//      }
//    };
  }

  @Override
  protected ArrayTest<Series> createAssignTest() {
    return null;
//    return new ArrayTest<Series>() {
//      @Override
//      public List<Series> getTestData() {
//        //@formatter:off
//        return Arrays.asList(
//            new ObjectSeries(Types.OBJECT, 3,3,3),
//            seriesOf(10),
//
//            new ObjectSeries(Types.OBJECT, 1,3,4),
//            seriesOf(1,2,3,4)
//        );
//        //@formatter:on
//      }
//
//      @Override
//      public void test(List<Series> actual) {
//        Series first = actual.get(0);
//        assertArrayEquals(new int[] {3, 3, 3}, first.getShape());
//        for (int i = 0; i < first.size(); i++) {
//          assertEquals(10, first.get(10));
//        }
//
//        Series second = actual.get(1);
//        assertArrayEquals(new int[] {1, 3, 4}, second.getShape());
//        for (int i = 0; i < second.vectors(2); i++) {
//          Series vector = second.getVector(2, i);
//          assertEquals(vector.loc(), seriesOf(1, 2, 3, 4).loc());
//        }
//      }
//    };
  }

  @Override protected ArrayTest<Series> createForEachTest() {
    return null;
  }
}

package org.briljantframework.data.series;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

/**
 * Created by isak on 5/4/16.
 */
public class ObjectSeriesTest extends org.briljantframework.array.AbstractBaseArrayTest<Series> {

  protected Series seriesOf(Object... values) {
    Series.Builder builder = new ObjectSeries.Builder(Object.class);
    builder.addAll(Arrays.asList(values));
    return builder.build();
  }

  @Override
  protected ArrayTest<Series> createSetSingleIndexTest() {
    return new ArrayTest<Series>() {
      @Override
      public List<Series> create() {
        Series a = seriesOf(1, 2, 3, 4);
        Series b = seriesOf(5, 6, 7, 8);
        //@formatter:off
        return Arrays.asList(a, b,
            a.reshape(2,2),
            b.reshape(2,2),

            a.reshape(2,1,2),
            b.reshape(2,1,2)
        );
        //@formatter:on
      }

      @Override
      public void test(List<Series> actual) {
        assertEquals(5, actual.get(0).get(1));
        assertEquals(5, actual.get(1).loc().get(0, 1));
        assertEquals(5, actual.get(2).loc().get(0, 0, 1));
      }
    };
  }

  @Override
  protected ArrayTest<Series> createSetMatrixIndexTest() {
    return new ArrayTest<Series>() {
      @Override
      public List<Series> create() {
        //@formatter:off
        return java.util.Arrays.asList(
            seriesOf(1,2,3,4),

            seriesOf(1,2,3,4,5,6,7,8).reshape(2,4),
            seriesOf("a","a","a","a","a","a","a","a").reshape(2, 4),

            new ObjectSeries(Type.OBJECT, 2,1,2)
        );
        //@formatter:on
      }

      @Override
      public void test(List<Series> actual) {
        for (int i = 0; i < actual.get(0).size(); i++) {
          assertEquals("a", actual.get(0).loc().get(i));
        }
      }
    };
  }

  @Override
  protected ArrayTest<Series> createAssignTest() {
    return new ArrayTest<Series>() {
      @Override
      public List<Series> create() {
        //@formatter:off
        return Arrays.asList(
            new ObjectSeries(Type.OBJECT, 3,3,3),
            seriesOf(10),

            new ObjectSeries(Type.OBJECT, 1,3,4),
            seriesOf(1,2,3,4)
        );
        //@formatter:on
      }

      @Override
      public void test(List<Series> actual) {
        Series first = actual.get(0);
        assertArrayEquals(new int[] {3, 3, 3}, first.getShape());
        for (int i = 0; i < first.size(); i++) {
          assertEquals(10, first.get(10));
        }

        Series second = actual.get(1);
        assertArrayEquals(new int[] {1, 3, 4}, second.getShape());
        for (int i = 0; i < second.vectors(2); i++) {
          Series vector = second.getVector(2, i);
          assertEquals(vector.loc(), seriesOf(1, 2, 3, 4).loc());
        }
      }
    };
  }
}

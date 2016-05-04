package org.briljantframework.array;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.briljantframework.array.api.ArrayFactory;

/**
 * Created by isak on 5/3/16.
 */
public abstract class IntArrayTest extends AbstractBaseArrayTest<IntArray> {

  protected abstract ArrayFactory getArrayFactory();

  @Override
  protected ArrayTest<IntArray> createSetSingleIndexTest() {
    return new ArrayTest<IntArray>() {
      @Override
      public List<IntArray> create() {
        // @formatter:off
        return java.util.Arrays.asList(
            getArrayFactory().newIntVector(1,2,3,4),
            getArrayFactory().newIntVector(5,6,7,8),

            getArrayFactory().newIntVector(1,3,2,4).reshape(2,2),
            getArrayFactory().newIntVector(5,7,6,8).reshape(2,2),

            getArrayFactory().newIntVector(1,3,2,4).reshape(2,1,2),
            getArrayFactory().newIntVector(5,7,6,8).reshape(2,1,2)
        );
        // @formatter:on
      }

      @Override
      public void test(List<IntArray> actual) {
        assertEquals(5, actual.get(0).get(1));
        assertEquals(5, actual.get(1).get(0, 1));
        assertEquals(5, actual.get(2).get(0, 0, 1));
      }
    };

  }

  @Override
  protected ArrayTest<IntArray> createSetMatrixIndexTest() {
    return new ArrayTest<IntArray>() {
      @Override
      public List<IntArray> create() {
        //@formatter:off
        return java.util.Arrays.asList(
            getArrayFactory().newIntVector(1,2,3,4),

            getArrayFactory().newIntVector(1,2,3,4,5,6,7,8).reshape(2,4),
            getArrayFactory().newIntArray(2,4),

            getArrayFactory().newIntArray(2,1,2)
        );
        //@formatter:on
      }

      @Override
      public void test(List<IntArray> actual) {
        for (int i = 0; i < actual.get(0).size(); i++) {
          assertEquals(0, actual.get(0).get(i));
        }
      }
    };
  }

  @Override
  protected ArrayTest<IntArray> createAssignTest() {
    return new ArrayTest<IntArray>() {
      @Override
      public List<IntArray> create() {
        //@formatter:off
        return Arrays.asList(
            getArrayFactory().newIntArray(3,3,3),
            getArrayFactory().newIntVector(10),

            getArrayFactory().newIntArray(1,3,4),
            getArrayFactory().newIntVector(1,2,3,4)
        );
        //@formatter:on
      }

      @Override
      public void test(List<IntArray> actual) {
        IntArray first = actual.get(0);
        assertArrayEquals(new int[] {3, 3, 3}, first.getShape());
        for (int i = 0; i < first.size(); i++) {
          assertEquals(10, first.get(10));
        }

        IntArray second = actual.get(1);
        assertArrayEquals(new int[] {1, 3, 4}, second.getShape());
        for (int i = 0; i < second.vectors(2); i++) {
          IntArray vector = second.getVector(2, i);
          assertEquals(vector, getArrayFactory().newIntVector(1, 2, 3, 4));
        }
      }
    };
  }

}

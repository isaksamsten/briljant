package org.briljantframework;

import static org.junit.Assert.assertEquals;

import org.briljantframework.array.Arrays;
import org.briljantframework.array.ComplexArray;
import org.briljantframework.array.DoubleArray;
import org.briljantframework.array.IntArray;
import org.junit.Test;

/**
 * @author Isak Karlsson <isak-kar@dsv.su.se>
 */
public class ArraysTest {

  @Test
  public void testBisectLeft() throws Exception {
    IntArray a = IntArray.of(1, 2, 9, 10, 12);
    System.out.println(Arrays.bisectLeft(a, 12));
  }

  @Test
  public void testOrder() throws Exception {
    DoubleArray array = DoubleArray.of(2, 3, 1, 9, 1);
    assertEquals(IntArray.of(2, 4, 0, 1, 3), Arrays.order(array));
  }

  @Test
  public void testOrderDimension() throws Exception {
    DoubleArray array = DoubleArray.of(1, 9, 1, 9, 2, 4).reshape(3, 2);
    assertEquals(IntArray.of(0, 2, 1, 1, 2, 0).reshape(3, 2), Arrays.order(0, array));
  }

  @Test
  public void testConcatenate() throws Exception {
    IntArray x = Arrays.range(2 * 2 * 3).reshape(2, 2, 3);

    IntArray concat_0 = Arrays.concatenate(java.util.Arrays.asList(x, x, x), 0);
    IntArray concat_1 = Arrays.concatenate(java.util.Arrays.asList(x, x, x), 1);
    IntArray concat_2 = Arrays.concatenate(java.util.Arrays.asList(x, x, x), 2);

    IntArray expected_0 = IntArray.of(0, 1, 0, 1, 0, 1, 2, 3, 2, 3, 2, 3, 4, 5, 4, 5, 4, 5, 6, 7, 6,
        7, 6, 7, 8, 9, 8, 9, 8, 9, 10, 11, 10, 11, 10, 11);
    IntArray expected_1 = IntArray.of(0, 1, 2, 3, 0, 1, 2, 3, 0, 1, 2, 3, 4, 5, 6, 7, 4, 5, 6, 7, 4,
        5, 6, 7, 8, 9, 10, 11, 8, 9, 10, 11, 8, 9, 10, 11);
    IntArray expected_2 = IntArray.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 0, 1, 2, 3, 4, 5, 6, 7,
        8, 9, 10, 11, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11);

    assertEquals(expected_0.reshape(6, 2, 3), concat_0);
    assertEquals(expected_1.reshape(2, 6, 3), concat_1);
    assertEquals(expected_2.reshape(2, 2, 9), concat_2);
  }

  @Test
  public void testSplit() throws Exception {
    IntArray x = Arrays.range(2 * 2 * 3).reshape(2, 2, 3);
    assertEquals(x, Arrays.concatenate(Arrays.split(x, 2, 0), 0));
    assertEquals(x, Arrays.concatenate(Arrays.split(x, 2, 1), 1));
    assertEquals(x, Arrays.concatenate(Arrays.split(x, 3, 2), 2));
  }

  @Test
  public void testWhere() throws Exception {
    DoubleArray c = DoubleArray.of(1, 0, 1, 2, 1);
    ComplexArray x = ComplexArray.of(1, 1, 1, 1, 1);
    ComplexArray y = ComplexArray.of(-1, 2, 3, -10, 3);
    assertEquals(Arrays.where(c.gte(2), x, y), ComplexArray.of(-1, 2, 3, 1, 3));
  }
}

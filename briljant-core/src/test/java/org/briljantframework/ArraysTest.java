package org.briljantframework;

import org.briljantframework.array.Arrays;
import org.briljantframework.array.DoubleArray;
import org.junit.Test;

/**
 * @author Isak Karlsson <isak-kar@dsv.su.se>
 */
public class ArraysTest {

  @Test
  public void testOrder() throws Exception {
    DoubleArray array = Arrays.of(new double[]{2, 3, 1, 9, 1});
    System.out.println(Arrays.order(array));

  }

  @Test
  public void testOrderDimension() throws Exception {
    DoubleArray array = Arrays.of(new double[]{1, 9, 1, 9, 2, 4}).reshape(3, 2);
    System.out.println(array);
    System.out.println(Arrays.order(0, array));
  }
}

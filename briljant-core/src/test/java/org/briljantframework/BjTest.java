package org.briljantframework;

import org.briljantframework.array.DoubleArray;
import org.junit.Test;

/**
 * @author Isak Karlsson <isak-kar@dsv.su.se>
 */
public class BjTest {

  @Test
  public void testOrder() throws Exception {
    DoubleArray array = Bj.array(new double[] {2, 3, 1, 9, 1});
    System.out.println(Bj.order(array));

  }

  @Test
  public void testOrderDimension() throws Exception {
    DoubleArray array = Bj.array(new double[]{1, 9, 1, 9, 2, 4}).reshape(3, 2);
    System.out.println(array);
    System.out.println(Bj.order(0, array));
  }
}

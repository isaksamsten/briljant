package org.briljantframework.matrix.time;

import static org.junit.Assert.assertEquals;

import org.briljantframework.matrix.RealArrayMatrix;
import org.briljantframework.matrix.RealMatrix;
import org.briljantframework.matrix.distance.Distance;
import org.junit.Test;

public class DynamicTimeWarpingTest {

  @Test
  public void testDistance() throws Exception {
    RealMatrix a = RealArrayMatrix.of(1, 16, 0, 0, 0, 0, 1, 1, 2, 2, 3, 2, 1, 1, 0, 0, 0, 0);
    RealMatrix b = RealArrayMatrix.of(1, 16, 0, 1, 1, 1, 2, 2, 3, 3, 3, 3, 2, 2, 1, 1, 0, 0);

    Distance dwt = DynamicTimeWarping.unconstraint(Distance.MANHATTAN);
    assertEquals(3.0, dwt.distance(a, b), 0);

    dwt = DynamicTimeWarping.withDistance(Distance.MANHATTAN).withConstraint(10).create();
    assertEquals(3.0, dwt.distance(a, b), 0);

    assertEquals(12.0, Distance.MANHATTAN.distance(a, b), 0);
    assertEquals(true, dwt.distance(a, b) < Distance.MANHATTAN.distance(a, b));
  }
}

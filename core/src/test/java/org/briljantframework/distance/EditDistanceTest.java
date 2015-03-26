package org.briljantframework.distance;

import org.briljantframework.vector.IntVector;
import org.briljantframework.vector.Vector;
import org.junit.Test;

public class EditDistanceTest {

  @Test
  public void testCompute() throws Exception {
    Vector a = new IntVector(1, 2, 3);
    Vector b = new IntVector(2, 1, 2);
    Distance edit = new EditDistance();
    System.out.println(edit.compute(a, b));

  }
}
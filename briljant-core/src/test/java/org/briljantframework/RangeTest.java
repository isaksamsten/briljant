package org.briljantframework;

import org.briljantframework.matrix.Range;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RangeTest {

  @Test
  public void testRange() throws Exception {
    Range r = Briljant.range(10);
    for (int i = 0; i < r.size(); i++) {
      assertEquals(i, r.get(i));
    }
  }
}

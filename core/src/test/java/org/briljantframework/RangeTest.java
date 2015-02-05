package org.briljantframework;

import static org.junit.Assert.assertEquals;

import org.briljantframework.matrix.Range;
import org.junit.Test;

public class RangeTest {

  @Test
  public void testRange() throws Exception {
    Range r = Range.range(10);
    for (int i = 0; i < r.size(); i++) {
      assertEquals(i, r.get(i));
    }
  }
}

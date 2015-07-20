package org.briljantframework;

import org.briljantframework.array.Range;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RangeTest {

  @Test
  public void testRange() throws Exception {
    Range r = Bj.range(10);
    for (int i = 0; i < r.size(); i++) {
      assertEquals(i, r.get(i));
    }
  }
}

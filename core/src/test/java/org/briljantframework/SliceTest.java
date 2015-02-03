package org.briljantframework;

import static org.junit.Assert.assertEquals;

import org.briljantframework.matrix.Slice;
import org.junit.Test;

public class SliceTest {

  @Test
  public void testRange() throws Exception {
    Slice r = Slice.slice(10);
    for (int i = 0; i < r.size(); i++) {
      assertEquals(i, r.get(i));
    }
  }
}

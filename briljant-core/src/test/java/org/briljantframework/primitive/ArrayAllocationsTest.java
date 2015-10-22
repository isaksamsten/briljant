package org.briljantframework.primitive;

import static org.junit.Assert.assertArrayEquals;

import org.junit.Test;

/**
 * @author Isak Karlsson <isak-kar@dsv.su.se>
 */
public class ArrayAllocationsTest {

  @Test
  public void testPrepend() throws Exception {
    assertArrayEquals(new Object[]{1, 2, 3}, ArrayAllocations.prepend(1, new Integer[]{2, 3}));
  }
}

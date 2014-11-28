package org.briljantframework.matrix;

import org.briljantframework.matrix.slice.Index;
import org.junit.Assert;
import org.junit.Test;

public class IndexTest {
  @Test
  public void testToString() throws Exception {
    Index index = Index.of(1, 2, 3, 4, 4, 5, 6, 6, 7, 7, 7, 8, 8, 8, 9, 9);
    Assert.assertEquals("Index(2,3,4,4,4,5,6,6,6,6,6,...)", index.toString());
  }
}

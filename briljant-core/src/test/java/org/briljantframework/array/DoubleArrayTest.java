package org.briljantframework.array;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Isak Karlsson
 */
public class DoubleArrayTest {

  @Test
  public void testRange() throws Exception {
    DoubleArray actual = DoubleArray.range(0, 1, 0.1);
    DoubleArray expected = DoubleArray.of(0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9);
    assertEquals(expected, actual);
  }
}
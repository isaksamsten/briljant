package org.briljantframework.array;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by isak on 11/17/15.
 */
public class DoubleArrayTest {

  @Test
  public void testRange() throws Exception {
    DoubleArray r = DoubleArray.range(0, 10, 0.1);
    System.out.println(r);
  }
}
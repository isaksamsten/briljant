package org.briljantframework.array.random;

import org.briljantframework.Bj;
import org.briljantframework.array.IntArray;
import org.briljantframework.array.ArrayAssert;
import org.junit.Test;

import java.util.Random;

public class SampleTest {

  @Test
  public void testSampleWithoutReplacement() throws Exception {
    IntArray sample = Sample.withoutReplacement(10, 5, new Random(123));
    ArrayAssert.assertMatrixEquals(sample, Bj.array(new int[]{0, 1, 5, 3, 4}));
  }
}
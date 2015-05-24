package org.briljantframework.matrix.random;

import org.briljantframework.Bj;
import org.briljantframework.matrix.IntMatrix;
import org.briljantframework.matrix.MatrixAssert;
import org.junit.Test;

import java.util.Random;

public class SampleTest {

  @Test
  public void testSampleWithoutReplacement() throws Exception {
    IntMatrix sample = Sample.withoutReplacement(10, 5, new Random(123));
    MatrixAssert.assertMatrixEquals(sample, Bj.matrix(new int[]{0, 1, 5, 3, 4}));
  }
}
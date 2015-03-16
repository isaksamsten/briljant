package org.briljantframework.dataframe.transform;

import org.briljantframework.matrix.DefaultDoubleMatrix;
import org.briljantframework.matrix.DoubleMatrix;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class InverseTransformerTest {

  DefaultDoubleMatrix matrix;

  @Before
  public void setUp() throws Exception {
    matrix = DefaultDoubleMatrix.of(4, 4, 0, 2, 0, 1, 2, 2, 3, 2, 4, -3, 0, 1., 6, 1, -6, -5);
  }

  @Test
  public void testTransform() throws Exception {
    InverseTransformation transformer = new InverseTransformation();
    DoubleMatrix inverse = transformer.transform(matrix);
    assertEquals(-0.02564102564102574, inverse.get(0, 0), 0.01);
  }
}

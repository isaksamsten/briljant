package org.briljantframework.dataframe.transform;

import static org.junit.Assert.assertEquals;

import org.briljantframework.matrix.ArrayDoubleMatrix;
import org.junit.Before;
import org.junit.Test;

public class InverseTransformerTest {

  ArrayDoubleMatrix matrix;

  @Before
  public void setUp() throws Exception {
    matrix = ArrayDoubleMatrix.of(4, 4, 0, 2, 0, 1, 2, 2, 3, 2, 4, -3, 0, 1., 6, 1, -6, -5);
  }

  @Test
  public void testTransform() throws Exception {
    InverseTransformation transformer = new InverseTransformation();
    ArrayDoubleMatrix inverse = transformer.transform(matrix);
    assertEquals(-0.02564102564102574, inverse.get(0, 0), 0.01);
  }
}

package org.briljantframework.matrix;

import static org.junit.Assert.assertEquals;

import org.briljantframework.vector.DoubleVector;
import org.briljantframework.vector.Vector;
import org.junit.Before;
import org.junit.Test;

public class VectorDoubleMatrixTest {

  VectorDoubleMatrix matrix = null;
  DoubleMatrix mmul = Matrices.parseMatrix("1,2;1,2;1,2");


  @Before
  public void setUp() throws Exception {
    Vector vec = DoubleVector.newBuilderWithInitialValues(1, 2, 3, 4, 5, 6, 7, 8, 9).build();
    matrix = new VectorDoubleMatrix(3, 3, vec);
  }

  @Test
  public void testGet() throws Exception {
    assertEquals(2, matrix.get(1, 0), 0);
    assertEquals(Matrices.parseMatrix("1,2;1,2;1,2"), mmul);
    System.out.println(matrix.mmul(mmul));
  }
}

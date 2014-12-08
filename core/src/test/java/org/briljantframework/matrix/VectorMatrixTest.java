package org.briljantframework.matrix;

import static org.junit.Assert.assertEquals;

import org.briljantframework.vector.RealVector;
import org.briljantframework.vector.Vector;
import org.junit.Before;
import org.junit.Test;

public class VectorMatrixTest {

  VectorMatrix matrix = null;
  Matrix mmul = Matrices.parseMatrix("1,2;1,2;1,2");


  @Before
  public void setUp() throws Exception {
    Vector vec = RealVector.newBuilderWithInitialValues(1, 2, 3, 4, 5, 6, 7, 8, 9).build();
    matrix = new VectorMatrix(vec, 3, 3);
  }

  @Test
  public void testGet() throws Exception {
    assertEquals(2, matrix.get(1, 0), 0);
    assertEquals(Matrices.parseMatrix("1,2;1,2;1,2"), mmul);
    System.out.println(matrix.mmul(mmul));
  }
}

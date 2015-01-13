package org.briljantframework.vector;

import static org.junit.Assert.assertEquals;

import org.briljantframework.matrix.DoubleMatrix;
import org.briljantframework.matrix.Doubles;
import org.junit.Before;
import org.junit.Test;

public class VectorDoubleMatrixAdapterTest {

  VectorDoubleMatrixAdapter matrix = null;
  DoubleMatrix mmul = Doubles.parseMatrix("1,2;1,2;1,2");


  @Before
  public void setUp() throws Exception {
    Vector vec = DoubleVector.newBuilderWithInitialValues(1, 2, 3, 4, 5, 6, 7, 8, 9).build();
    matrix = new VectorDoubleMatrixAdapter(3, 3, vec);
  }

  @Test
  public void testGet() throws Exception {
    assertEquals(2, matrix.get(1, 0), 0);
    assertEquals(Doubles.parseMatrix("1,2;1,2;1,2"), mmul);
    System.out.println(matrix.mmul(mmul));
  }
}

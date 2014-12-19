package org.briljantframework.linalg;

import static org.briljantframework.matrix.Matrices.parseMatrix;
import static org.briljantframework.matrix.Matrices.sort;
import static org.junit.Assert.assertEquals;

import org.briljantframework.matrix.ArrayMatrix;
import org.briljantframework.matrix.Axis;
import org.briljantframework.matrix.Matrices;
import org.briljantframework.matrix.Matrix;
import org.junit.Before;
import org.junit.Test;


public class MatricesTest {

  @Before
  public void setUp() throws Exception {

  }

  @Test
  public void testSumRows() throws Exception {
    Matrix m = parseMatrix("1,1,1,1;" + "1,1,1,1;" + "1,1,1,1");
    Matrix rowSum = Matrices.sum(m, Axis.ROW);
    Matrix columnSum = Matrices.sum(m, Axis.COLUMN);

    assertEquals(4, rowSum.size());
    assertEquals(3, columnSum.size());

    assertEquals(ArrayMatrix.rowVector(3, 3, 3, 3), rowSum);
    assertEquals(ArrayMatrix.columnVector(4, 4, 4), columnSum);
  }

  @Test
  public void testAxB() throws Exception {
    Matrix a = ArrayMatrix.of(2, 3, 1, 2, 3, 1, 2, 3);
    Matrix b = ArrayMatrix.of(3, 2, 2, 2, 1, 1, 3, 3);
    Matrix result = ArrayMatrix.of(2, 2, 13, 13, 13, 13);
    assertEquals(result, a.mmul(b));

    Matrix x = parseMatrix("32,12,3,4;" + "12,3,41,122");

    System.out.println(sort(x, Axis.ROW));
  }
}

package org.briljantframework.matrix;

import org.junit.Test;

public class HashMatrixTest {

  @Test
  public void testAdd() throws Exception {
    int rows = 100;
    int columns = 5000;
    Matrix matrix = new ArrayMatrix(rows, columns);
    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < columns; j++) {
        matrix.put(i, j, i + j);
      }
    }
    Matrix tmatrix = matrix.transpose();
    tmatrix = tmatrix.getView(0, 0, columns, rows);
    long s = System.currentTimeMillis();
    matrix.mmul(tmatrix);
    System.out.println(System.currentTimeMillis() - s);
    // System.out.println(matrix.getView(0, 1, 2, 2));

    // System.out.println(matrix.mmul(matrix.transpose()).assign(matrix.getView(0, 1, 2, 2),
    // Math::sqrt));
    //
    //
    // assertEquals(2, matrix.get(1, 1), 0.1);
  }
}

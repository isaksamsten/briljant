package org.briljantframework.matrix;

import org.briljantframework.DoubleArray;
import org.junit.Test;

public class HashMatrixTest {

  @Test
  public void testAdd() throws Exception {
    int rows = 5;
    int columns = 5;
    Matrix matrix = new HashMatrix(rows, columns);
    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < columns; j++) {
        matrix.put(i, j, i + j);
      }
    }

    DoubleArray a = DoubleArray.wrap(1, 2, 3, 4, 5);
    System.out.println(matrix.assign(a, (c, d) -> c + d, Axis.COLUMN));

    // Matrix view = matrix.getView(0, 0, 4, 4);
    //
    // Matrix tmatrix = matrix.transpose();
    // tmatrix = tmatrix.getView(0, 0, columns, rows);
    // long s = System.currentTimeMillis();
    // matrix.mmul(tmatrix);
    // System.out.println(System.currentTimeMillis() - s);
    // System.out.println(matrix.getView(0, 1, 2, 2));

    // System.out.println(matrix.mmul(matrix.transpose()).assign(matrix.getView(0, 1, 2, 2),
    // Math::sqrt));
    //
    //
    // assertEquals(2, matrix.get(1, 1), 0.1);
  }
}

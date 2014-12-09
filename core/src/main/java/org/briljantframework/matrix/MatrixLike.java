package org.briljantframework.matrix;

/**
 * Created by isak on 09/12/14.
 */
public interface MatrixLike {

  /**
   * Flattens the traversal of the matrix in column-major order. The matrix is traversed in
   * column-major order. For example, given the following matrix
   * <p>
   *
   * <pre>
   *     1 2 3
   *     4 5 6
   * </pre>
   * <p>
   * this code
   * <p>
   *
   * <pre>
   * for (int i = 0; i &lt; x.size(); i++) {
   *   System.out.print(x.get(i));
   * }
   * </pre>
   * <p>
   * prints
   * <p>
   *
   * <pre>
   * 142536
   * </pre>
   *
   * @param index the index
   * @return the double
   */
  double get(int index);

  /**
   * Returns the linearized size of this matrix.
   * <p>
   *
   * <pre>
   * {@code rows() * columns() == @code size()}
   * </pre>
   *
   * @return the int
   */
  int size();

  /**
   * The number of rows.
   *
   * @return number or rows
   */
  int rows();

  /**
   * The number of columns.
   *
   * @return number of columns
   */
  int columns();

  /**
   * Get value at row i and column j
   *
   * @param i row
   * @param j column
   * @return value double
   */
  double get(int i, int j);
}

package org.briljantframework.matrix;

import org.briljantframework.complex.Complex;

/**
 * Created by Isak Karlsson on 09/01/15.
 */
public interface AnyMatrix {

  AnyMatrix reshape(int rows, int cols);

  /**
   * Get value at row {@code i} and column {@code j}
   *
   * @param i row
   * @param j column
   * @return value int
   */
  Complex getAsComplex(int i, int j);

  /**
   * Flattens the traversal of the matrix in column-major order. The matrix is traversed in
   * column-major order. For example, given the following matrix
   * <p>
   * <p>
   *
   * <pre>
   *     1 2 3
   *     4 5 6
   * </pre>
   * <p>
   * this code
   * <p>
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
   * <p>
   *
   * <pre>
   * 142536
   * </pre>
   *
   * @param index the index
   * @return the value index
   */
  Complex getAsComplex(int index);

  /**
   * Set value at row {@code i} and column {@code j} to value
   *
   * @param i row
   * @param j column
   * @param value value
   */
  void put(int i, int j, Complex value);

  /**
   * Puts {@code value} at the linearized position {@code index}. Column major order is strictly
   * enforced.
   *
   * @param index the index
   * @param value the value
   * @see #getAsComplex(int)
   */
  void put(int index, Complex value);

  /**
   * Get value at row {@code i} and column {@code j}
   *
   * @param i row
   * @param j column
   * @return value int
   */
  double getAsDouble(int i, int j);

  /**
   * Flattens the traversal of the matrix in column-major order. The matrix is traversed in
   * column-major order. For example, given the following matrix
   * <p>
   * <p>
   *
   * <pre>
   *     1 2 3
   *     4 5 6
   * </pre>
   * <p>
   * this code
   * <p>
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
   * <p>
   *
   * <pre>
   * 142536
   * </pre>
   *
   * @param index the index
   * @return the value index
   */
  double getAsDouble(int index);

  /**
   * Set value at row {@code i} and column {@code j} to value
   *
   * @param i row
   * @param j column
   * @param value value
   */
  void put(int i, int j, double value);

  /**
   * Puts {@code value} at the linearized position {@code index}. Column major order is strictly
   * enforced.
   *
   * @param index the index
   * @param value the value
   * @see #getAsInt(int)
   */
  void put(int index, double value);

  /**
   * Get value at row {@code i} and column {@code j}
   *
   * @param i row
   * @param j column
   * @return value int
   */
  int getAsInt(int i, int j);

  /**
   * Flattens the traversal of the matrix in column-major order. The matrix is traversed in
   * column-major order. For example, given the following matrix
   * <p>
   * <p>
   * 
   * <pre>
   *     1 2 3
   *     4 5 6
   * </pre>
   * <p>
   * this code
   * <p>
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
   * <p>
   * 
   * <pre>
   * 142536
   * </pre>
   *
   * @param index the index
   * @return the value index
   */
  int getAsInt(int index);

  /**
   * Set value at row {@code i} and column {@code j} to value
   *
   * @param i row
   * @param j column
   * @param value value
   */
  void put(int i, int j, int value);

  /**
   * Puts {@code value} at the linearized position {@code index}. Column major order is strictly
   * enforced.
   *
   * @param index the index
   * @param value the value
   * @see #getAsInt(int)
   */
  void put(int index, int value);

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
   * Returns the linearized size of this matrix. If {@code rows()} or {@code columns()} return 1,
   * then {@code size()} is intuitive. However, if not size is {@code rows() * columns()} and the
   * end when iterating using {@code getAs...(int)}. To avoid cache misses,
   * {@code for(int i = 0; i < m.size(); i++) m.put(i, m.getAsDouble(i) * 2)} should be preferred to
   *
   * <pre>
   *     for(int i = 0; i < m.rows(); i++)
   *       for(int j = 0; j < m.columns(); j++)
   *          m.put(i, j, m.get(i, j) * 2
   * </pre>
   *
   * @return the size
   */
  int size();

  /**
   * Is square.
   *
   * @return true if rows() == columns()
   */
  default boolean isSquare() {
    return rows() == columns();
  }

  /**
   * The shape of the current matrix.
   *
   * @return the shape
   */
  default Shape getShape() {
    return Shape.of(rows(), columns());
  }

  /**
   * @param other the other
   * @return the boolean
   */
  default boolean hasEqualShape(AnyMatrix other) {
    return rows() == other.rows() && columns() == other.columns();
  }

  DoubleMatrix asDoubleMatrix();

  IntMatrix asIntMatrix();

  ComplexMatrix asComplexMatrix();

  /**
   * Return a boolean matrix with element {@code i, j} set to true if
   * {@code get(i, j) < other.get(i, j)}.
   *
   * @param other the matrix
   * @return a boolean matrix
   */
  BooleanMatrix lessThan(AnyMatrix other);

  /**
   * Return a boolean matrix with element {@code i, j} set to true if {@code get(i, j) < value}.
   *
   * @param value the matrix
   * @return a boolean matrix
   */
  BooleanMatrix lessThan(Number value);

  /**
   * Return a boolean matrix with element {@code i, j} set to true if
   * {@code get(i, j) <= other.get(i, j)}.
   *
   * @param other the matrix
   * @return a boolean matrix
   */
  BooleanMatrix lessThanEqual(AnyMatrix other);

  /**
   * Return a boolean matrix with element {@code i, j} set to true if {@code get(i, j) <= value}.
   *
   * @param value the matrix
   * @return a boolean matrix
   */
  BooleanMatrix lessThanEqual(Number value);

  /**
   * Return a boolean matrix with element {@code i, j} set to true if
   * {@code get(i, j) > other.get(i, j)}.
   *
   * @param other the matrix
   * @return a boolean matrix
   */
  BooleanMatrix greaterThan(AnyMatrix other);

  /**
   * Return a boolean matrix with element {@code i, j} set to true if {@code get(i, j) > value}.
   *
   * @param value the matrix
   * @return a boolean matrix
   */
  BooleanMatrix greaterThan(Number value);

  /**
   * Return a boolean matrix with element {@code i, j} set to true if
   * {@code get(i, j) >= other.get(i, j)}.
   *
   * @param other the matrix
   * @return a boolean matrix
   */
  BooleanMatrix greaterThanEquals(AnyMatrix other);

  /**
   * Return a boolean matrix with element {@code i, j} set to true if {@code get(i, j) >= value}.
   *
   * @param value the matrix
   * @return a boolean matrix
   */
  BooleanMatrix greaterThanEquals(Number value);

  /**
   * Return a boolean matrix with element {@code i, j} set to true if
   * {@code get(i, j) == other.get(i, j)}.
   *
   * @param other the matrix
   * @return a boolean matrix
   */
  BooleanMatrix equalsTo(AnyMatrix other);

  /**
   * Return a boolean matrix with element {@code i, j} set to true if {@code get(i, j) == value}.
   *
   * @param value the matrix
   * @return a boolean matrix
   */
  BooleanMatrix equalsTo(Number value);

}

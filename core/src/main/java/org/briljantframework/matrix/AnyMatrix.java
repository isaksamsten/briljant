package org.briljantframework.matrix;

import org.briljantframework.complex.Complex;

/**
 * @author Isak Karlsson
 */
public interface AnyMatrix {

  /**
   * If {@code getType()} equals
   * <ul>
   * <li>{@link Type#DOUBLE} {@link #asDoubleMatrix()} returns {@code this}.</li>
   * <li>{@link Type#INT} {@link #asIntMatrix()} returns {@code this}.</li>
   * <li>{@link Type#BOOLEAN} {@link #asBitMatrix()} returns {@code this}.</li>
   * <li>{@link Type#COMPLEX} {@link #asComplexMatrix()} returns {@code this}.</li>
   * </ul>
   * 
   * @return the type of this matrix
   */
  Type getType();

  /**
   * Reshape {@code this}. Returns a new matrix, with {@code this != this.reshape(..., ...)} but
   * where modifications of the reshape propagates. I.e. the reshape is a view of the original
   * matrix.
   *
   * @param rows the new rows
   * @param columns the new columns
   * @return a new matrix
   */
  AnyMatrix reshape(int rows, int columns);

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
  void set(int i, int j, Complex value);

  /**
   * Puts {@code value} at the linearized position {@code index}. Column major order is strictly
   * enforced.
   *
   * @param index the index
   * @param value the value
   * @see #getAsComplex(int)
   */
  void set(int index, Complex value);

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
  void set(int i, int j, double value);

  /**
   * Puts {@code value} at the linearized position {@code index}. Column major order is strictly
   * enforced.
   *
   * @param index the index
   * @param value the value
   * @see #getAsInt(int)
   */
  void set(int index, double value);

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
  void set(int i, int j, int value);

  /**
   * Puts {@code value} at the linearized position {@code index}. Column major order is strictly
   * enforced.
   *
   * @param index the index
   * @param value the value
   * @see #getAsInt(int)
   */
  void set(int index, int value);

  /**
   * Set {@code number} at the row {@code i} and column {@code j}.
   * 
   * @param i the row
   * @param j the column
   * @param number the number
   */
  void set(int i, int j, Number number);

  /**
   * Set {@code number} at the linearized position {@code index}.
   * 
   * @param index the index
   * @param number the number
   */
  void set(int index, Number number);

  /**
   * Set value at {@code atIndex} to the value in {@code from} at {@code fromIndex}
   * 
   * @param atIndex the index
   * @param from the other matrix
   * @param fromIndex the index
   */
  void set(int atIndex, AnyMatrix from, int fromIndex);

  /**
   * Set value at {@code atRow, atColumn} to the value in {@code from} at
   * {@code fromRow, fromColumn}
   * 
   * @param atRow the row index
   * @param atColumn the column index
   * @param from the other matrix
   * @param fromRow the row index
   * @param fromColumn the column index
   */
  void set(int atRow, int atColumn, AnyMatrix from, int fromRow, int fromColumn);

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

  /**
   * @return this matrix as a {@link DoubleMatrix}.
   */
  DoubleMatrix asDoubleMatrix();

  /**
   * @return this matrix as an {@link IntMatrix}.
   */
  IntMatrix asIntMatrix();

  /**
   * @return this matrix as an {@link BitMatrix}.
   */
  BitMatrix asBitMatrix();

  /**
   * @return this matrix as a {@link ComplexMatrix}.
   */
  ComplexMatrix asComplexMatrix();

  /**
   * Return a boolean matrix with element {@code i, j} set to true if
   * {@code get(i, j) < other.get(i, j)}.
   *
   * @param other the matrix
   * @return a boolean matrix
   */
  BitMatrix lessThan(AnyMatrix other);

  /**
   * Return a boolean matrix with element {@code i, j} set to true if {@code get(i, j) < value}.
   *
   * @param value the matrix
   * @return a boolean matrix
   */
  BitMatrix lessThan(Number value);

  /**
   * Return a boolean matrix with element {@code i, j} set to true if
   * {@code get(i, j) <= other.get(i, j)}.
   *
   * @param other the matrix
   * @return a boolean matrix
   */
  BitMatrix lessThanEqual(AnyMatrix other);

  /**
   * Return a boolean matrix with element {@code i, j} set to true if {@code get(i, j) <= value}.
   *
   * @param value the matrix
   * @return a boolean matrix
   */
  BitMatrix lessThanEqual(Number value);

  /**
   * Return a boolean matrix with element {@code i, j} set to true if
   * {@code get(i, j) > other.get(i, j)}.
   *
   * @param other the matrix
   * @return a boolean matrix
   */
  BitMatrix greaterThan(AnyMatrix other);

  /**
   * Return a boolean matrix with element {@code i, j} set to true if {@code get(i, j) > value}.
   *
   * @param value the matrix
   * @return a boolean matrix
   */
  BitMatrix greaterThan(Number value);

  /**
   * Return a boolean matrix with element {@code i, j} set to true if
   * {@code get(i, j) >= other.get(i, j)}.
   *
   * @param other the matrix
   * @return a boolean matrix
   */
  BitMatrix greaterThanEquals(AnyMatrix other);

  /**
   * Return a boolean matrix with element {@code i, j} set to true if {@code get(i, j) >= value}.
   *
   * @param value the matrix
   * @return a boolean matrix
   */
  BitMatrix greaterThanEquals(Number value);

  /**
   * Return a boolean matrix with element {@code i, j} set to true if
   * {@code get(i, j) == other.get(i, j)}.
   *
   * @param other the matrix
   * @return a boolean matrix
   */
  BitMatrix equalsTo(AnyMatrix other);

  /**
   * Return a boolean matrix with element {@code i, j} set to true if {@code get(i, j) == value}.
   *
   * @param value the matrix
   * @return a boolean matrix
   */
  BitMatrix equalsTo(Number value);

  /**
   * @return the transpose of {@code this}.
   */
  AnyMatrix transpose();

  /**
   * Create a copy of this matrix.
   *
   * @return the copy
   */
  AnyMatrix copy();

  /**
   * Incrementally construct a new matrix by adding values.
   * 
   * @return a new builder
   */
  Builder newBuilder();

  /**
   * Construct a new empty matrix with {@code this.getClass()}
   *
   * @param rows the number of rows
   * @param columns the number of columns
   * @return a new empty matrix
   */
  AnyMatrix newEmptyMatrix(int rows, int columns);

  /**
   *
   */
  enum Type {
    DOUBLE, INT, BOOLEAN, COMPLEX
  }

  interface Builder {

    void add(AnyMatrix from, int i, int j);

    void add(AnyMatrix from, int index);

    AnyMatrix build();
  }

}

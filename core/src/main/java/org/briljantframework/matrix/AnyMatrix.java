package org.briljantframework.matrix;

import org.briljantframework.Swappable;
import org.briljantframework.complex.Complex;

// TODO: implement mapToComplex(...), mapToInt(...), mapToDouble(...)
// TODO: implement assign(C, ...), assign(d, mapper), assign(i, mapper)
/**
 * <p>
 * The {@code AnyMatrix} interface is a base interface for several different matrix implementations.
 * 
 * There are four supported matrix types {@code double}, {@code int}, {@code boolean} and
 * {@link org.briljantframework.complex.Complex}, specialized in
 * {@link org.briljantframework.matrix.DoubleMatrix}, {@link org.briljantframework.matrix.IntMatrix}
 * , {@link org.briljantframework.matrix.BitMatrix} and
 * {@link org.briljantframework.matrix.ComplexMatrix} respectively.
 * </p>
 * 
 * <p>
 * The {@code AnyMatrix} interface provides ways to
 *
 * <ul>
 * <li>adapt one implementation to another.</li>
 * <li>get values of any type.</li>
 * <li>set values of any type.</li>
 * <li>set values of any type from another {@code AnyMatrix}, possibly without boxing.</li>
 * <li>compare values of unknown types.</li>
 * </ul>
 * </p>
 *
 * <h1>Adapt {@code AnyMatrix} to another matrix type</h1>
 * <p>
 * {@code AnyMatrix} defines four methods for adapting the current implementation to any of the four
 * specialized types. However, there are some caveats when adapting matrices and perform mutations.
 * 
 * For example, given a {@code DoubleMatrix d} which is adapted to a
 * {@code ComplexMatrix c = d.asComplexMatrix()}, then setting a position to a new {@code Complex}
 * with an imaginary part, e.g., {@code c.set(0, Complex.I)}, would just propagate the real part to
 * the underlying {@code DoubleMatrix}. Likewise, given an {@code IntMatrix} adapted to a
 * {@code DoubleMatrix}, setting a position to a double converts it to an {@code int} (using
 * {@link Math#round(double)}).
 * 
 * Finally, if receiver is
 * <ul>
 * <li>{@link org.briljantframework.matrix.DoubleMatrix}, {@link #asDoubleMatrix()} must return
 * {@code this}</li>
 * <li>{@link org.briljantframework.matrix.IntMatrix}, {@link #asIntMatrix()} must return
 * {@code this}</li>
 * <li>{@link org.briljantframework.matrix.BitMatrix}, {@link #asBitMatrix()} must return
 * {@code this}</li>
 * <li>{@link org.briljantframework.matrix.ComplexMatrix}, {@link #asComplexMatrix()} must return
 * {@code this}</li>
 * </ul>
 * </p>
 * <h1>Implicit conversions</h1>
 * <ul>
 * <li>{@code Complex => double}: {@code value.real()}</li>
 * <li>{@code double => int}: {@code (int) Math.round(value)}</li>
 * <li>{@code int => boolean}: {@code value == 1 ? true : false}</li>
 * <li>{@code boolean => int}; {@code value ? 1 : 0}</li>
 * <li>{@code int => double}: {@code value}</li>
 * <li>{@code double => Complex}: {@code Complex.valueOf(value)}</li>
 * </ul>
 * 
 * <p>
 * Remember that most subclasses provide, {@code get(int, int)} and {@code get(int)}, returning the
 * specialized type. For example, {@link org.briljantframework.matrix.DoubleMatrix#get(int, int)}.
 * </p>
 *
 * <h1>Avoid unboxing/type checking/truncating when transferring values between {@code AnyMatrix}es</h1>
 * <p>
 * Prefer:
 * 
 * <pre>
 *   AnyMatrix a = Doubles.randn(10, 1)
 *   AnyMatrix b = Doubles.zeros(10, 1)
 *   a.set(3, b, 0)
 * </pre>
 * 
 * to:
 * 
 * <pre>
 *   swith(b.getDataType()) {
 *       DataType.COMPLEX: a.set(3, b.getAsComplex(0); break;
 *       ...
 *       ...
 *       ...
 *       default: ...;
 *   }
 * </pre>
 * 
 * </p>
 *
 * @author Isak Karlsson
 */
public interface AnyMatrix extends Swappable {

  /**
   * Get value at row {@code i} and column {@code j}
   *
   * @param i row
   * @param j column
   * @return complex value
   */
  Complex getAsComplex(int i, int j);

  /**
   * Flattens the traversal of the matrix in column-major order. The matrix is traversed in
   * column-major order.
   *
   * @param index the index
   * @return the complex value index
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
   * @return double value
   */
  double getAsDouble(int i, int j);

  /**
   * Flattens the traversal of the matrix in column-major order. The matrix is traversed in
   * column-major order.
   *
   * @param index the index
   * @return the double value index
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
   * @return int value
   */
  int getAsInt(int i, int j);

  /**
   * Flattens the traversal of the matrix in column-major order. The matrix is traversed in
   * column-major order.
   * 
   * @param index the index
   * @return the int value index
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
   * Compare value at {@code a} to value at {@code b}.
   * 
   * @param a first index
   * @param b second index
   * @return the comparison
   * @see java.lang.Double#compare(double, double)
   * @see java.lang.Integer#compare(int, int)
   * @see java.lang.Boolean#compare(boolean, boolean)
   */
  int compare(int a, int b);

  /**
   * Compare value at {@code toIndex} in {@code this} to value at {@code fromIndex} in {@code from}.
   * 
   * @param toIndex index in {@code this}
   * @param fromIndex index in {@code from}
   * @return the comparison
   * @see java.lang.Double#compare(double, double)
   * @see java.lang.Integer#compare(int, int)
   * @see java.lang.Boolean#compare(boolean, boolean)
   */
  int compare(int toIndex, AnyMatrix from, int fromIndex);

  /**
   * Compare value at {@code toRow, toColumn} in {@code this} to value at
   * {@code fromRow, fromColumn} in {@code from}.
   * 
   * @param toRow row in {@code this}
   * @param toColumn column in {@code this}
   * @param from other matrix
   * @param fromRow row in {@code from}
   * @param fromColumn column in {@code from}
   * @return the comparison
   * @see java.lang.Double#compare(double, double)
   * @see java.lang.Integer#compare(int, int)
   * @see java.lang.Boolean#compare(boolean, boolean)
   */
  int compare(int toRow, int toColumn, AnyMatrix from, int fromRow, int fromColumn);

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
   * Get row vector at {@code i}. Modifications will change to original matrix.
   *
   * @param i row
   * @return a vector
   */
  AnyMatrix getRowView(int i);

  /**
   * Gets vector at {@code index}. Modifications will change the original matrix.
   *
   * @param index the index
   * @return the column
   */
  AnyMatrix getColumnView(int index);

  /**
   * Gets a view of the diagonal. Modifications will change the original matrix.
   *
   * @return a diagonal view
   */
  AnyMatrix getDiagonalView();

  /**
   * Get a view of row starting at {@code rowOffset} until {@code rowOffset + rows} and columns
   * starting at {@code colOffset} until {@code colOffset + columns}.
   *
   * For example,
   *
   * <pre>
   *   1 2 3
   *   4 5 6
   *   7 8 9
   * </pre>
   *
   * and {@code matrix.getView(1, 1, 2, 2)} produces
   *
   * <pre>
   *   5 6
   *   8 9
   * </pre>
   *
   * Please note that modifications of the view, mutates the original.
   *
   * @param rowOffset the row offset
   * @param colOffset the column offset
   * @param rows number of rows after row offset
   * @param columns number of columns after column offset
   * @return the matrix view
   */
  AnyMatrix getView(int rowOffset, int colOffset, int rows, int columns);

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
   * then {@code size()} is intuitive. However, if not, size is {@code rows() * columns()} and used
   * when iterating using {@code getAs...(int)}. To avoid cache misses,
   * {@code for(int i = 0; i < m.size(); i++) m.set(i, o.getAsDouble(i))} should be preferred to
   *
   * <pre>
   * for(int i = 0; i < m.rows(); i++)
   *   for(int j = 0; j < m.columns(); j++)
   *      m.set(i, j, o.getAsDouble(i, j))
   * </pre>
   * 
   * Since, {@code set(int, int, ....)} shouldn't be used in conjunction with
   * {@code getAs...(int, int)}, the example above should be written as
   * 
   * <pre>
   * for (int i = 0; i &lt; m.rows(); i++)
   *   for (int j = 0; j &lt; m.columns(); j++)
   *     m.set(i, j, m, i, j);
   * // or
   * for (int i = 0; i &lt; m.size(); i++)
   *   m.set(i, o, i);
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
  IncrementalBuilder newIncrementalBuilder();

  /**
   * Construct a new empty matrix with {@code this.getClass()}
   *
   * @param rows the number of rows
   * @param columns the number of columns
   * @return a new empty matrix
   */
  AnyMatrix newEmptyMatrix(int rows, int columns);

  /**
   * If {@code getType()} equals
   * <ul>
   * <li>{@link org.briljantframework.matrix.AnyMatrix.DataType#DOUBLE} {@link #asDoubleMatrix()}
   * returns {@code this}.</li>
   * <li>{@link org.briljantframework.matrix.AnyMatrix.DataType#INT} {@link #asIntMatrix()} returns
   * {@code this}.</li>
   * <li>{@link org.briljantframework.matrix.AnyMatrix.DataType#BOOLEAN} {@link #asBitMatrix()}
   * returns {@code this}.</li>
   * <li>{@link org.briljantframework.matrix.AnyMatrix.DataType#COMPLEX} {@link #asComplexMatrix()}
   * returns {@code this}.</li>
   * </ul>
   *
   * @return the type of this matrix
   */
  DataType getDataType();

  /**
   *
   */
  enum DataType {
    DOUBLE, INT, BOOLEAN, COMPLEX;
  }

  interface IncrementalBuilder {

    void add(AnyMatrix from, int i, int j);

    void add(AnyMatrix from, int index);

    AnyMatrix build();
  }

}

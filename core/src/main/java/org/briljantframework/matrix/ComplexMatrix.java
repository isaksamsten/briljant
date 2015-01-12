package org.briljantframework.matrix;

import java.util.function.*;

import org.briljantframework.complex.Complex;

/**
 * Implements a 2-dimensional matrix of complex numbers.
 *
 * @author Isak Karlsson
 */
public interface ComplexMatrix extends AnyMatrix, Iterable<Complex> {

  /**
   * Assign value returned by {@link #size()} successive calls to
   * {@link java.util.function.DoubleSupplier#getAsDouble()}
   *
   * @param supplier the supplier
   * @return receiver modified
   */
  ComplexMatrix assign(Supplier<Complex> supplier);

  /**
   * Assign {@code value} to {@code this}
   *
   * @param value the value to assign
   * @return receiver modified
   */
  ComplexMatrix assign(Complex value);

  /**
   * Assign the values in {@code values} to this matrix. The {@code length} of {@code value} must
   * equal {@code this.size()}. The array is assumed to be in column major order, hence
   * {@code [1,2,3,4]} assigned to a matrix will result in {@code [1 3; 2 4]} and not
   * {@code [1,2; 3,4]}, similar to R.
   *
   * @param values the column major array
   * @return receiver modified
   */
  ComplexMatrix assign(Complex[] values);

  /**
   * Assign {@code matrix} to {@code this}. Requires {@code matrix.getShape()} to equal
   * {@code this.getShape()}.
   *
   * @param matrix the matrix
   * @return receiver modified
   */
  ComplexMatrix assign(ComplexMatrix matrix);

  /**
   * Assign {@code matrix} to {@code this}, applying {@code operator} to each value. Compare:
   *
   * <pre>
   * ComplexMatrix original = ...;
   * ComplexMatrix other = ...;
   * for (int i = 0; i &lt; original.size(); i++) {
   *   original.put(i, other.get(i).multiply(3));
   * }
   * </pre>
   *
   * and {@code original.assign(other, x-> x.multiply(3))}
   *
   * @param matrix the matrix
   * @param operator the operator
   * @return receiver modified
   */
  ComplexMatrix assign(ComplexMatrix matrix, UnaryOperator<Complex> operator);

  /**
   * Assign {@code matrix} to this complex matrix.
   * 
   * @param matrix matrix of real values
   * @return receiver modified
   */
  ComplexMatrix assign(DoubleMatrix matrix);

  /**
   * Assign {@code matrix} to this complex matrix transforming each element.
   * 
   * @param matrix the matrix
   * @param operator the operator
   * @return receiver modified
   */
  ComplexMatrix assign(DoubleMatrix matrix, DoubleFunction<? extends Complex> operator);

  /**
   * 
   * @param complexes
   * @return
   */
  ComplexMatrix assignStream(Iterable<? extends Complex> complexes);

  /**
   * 
   * @param iterable
   * @param function
   * @param <T>
   * @return
   */
  <T> ComplexMatrix assignStream(Iterable<T> iterable,
      Function<? super T, ? extends Complex> function);

  /**
   * Perform {@code operator} element wise to receiver.
   *
   * For example, {@code m.map(Complex::sqrt)} is equal to
   *
   * <pre>
   * ComplexMatrix n = m.copy();
   * for (int i = 0; i &lt; n.size(); i++)
   *   n.put(i, n.get(i).sqrt());
   * </pre>
   *
   * To perform the operation in place, modifying {@code m}, use {@code m.assign(m, Complex::sqrt)}
   * or more verbosely
   *
   * <pre>
   * for (int i = 0; i &lt; m.size(); i++)
   *   m.put(i, m.get(i).sqrt());
   * </pre>
   *
   * @param operator the operator to apply to each element
   * @return a new matrix
   */
  ComplexMatrix map(UnaryOperator<Complex> operator);

  /**
   * Perform {@code operator} element wise to receiver.
   *
   * @param operator the operator to apply to each element
   * @return receiver modified
   */
  ComplexMatrix mapi(UnaryOperator<Complex> operator);

  ComplexMatrix filter(Predicate<? super Complex> predicate);

  /**
   * Reduces {@code this} into a real value. For example, summing can be implemented as
   * {@code matrix.reduce(0, (a,b) -> a + b, x -> x)}
   *
   *
   * @param identity the initial value
   * @param reduce takes two values and reduces them to one
   * @param map takes a value and possibly transforms it
   * @return the result
   */
  Complex reduce(Complex identity, BinaryOperator<Complex> reduce, UnaryOperator<Complex> map);

  /**
   * Reduces each column. Column wise summing can be implemented as
   *
   * <pre>
   * matrix.reduceColumns(col -&gt; col.reduce(0, (a, b) -&gt; a + b, x -&gt; x));
   * </pre>
   *
   * @param reduce takes a {@code ComplexMatrix} and returns {@code Complex}
   * @return a new column vector with the reduced value
   */
  ComplexMatrix reduceColumns(Function<? super ComplexMatrix, ? extends Complex> reduce);

  /**
   * Reduces each rows. Row wise summing can be implemented as
   *
   * <pre>
   * matrix.reduceRows(row -&gt; row.reduce(0, (a, b) -&gt; a + b, x -&gt; x));
   * </pre>
   *
   * @param reduce takes a {@code ComplexMatrix} and returns {@code Complex}
   * @return a new column vector with the reduced value
   */
  ComplexMatrix reduceRows(Function<? super ComplexMatrix, ? extends Complex> reduce);

  /**
   * Get row vector at {@code i}. Modifications will change to original matrix.
   *
   * @param i row
   * @return a vector
   */
  ComplexMatrix getRowView(int i);

  /**
   * Gets vector at {@code index}. Modifications will change the original matrix.
   *
   * @param index the index
   * @return the column
   */
  ComplexMatrix getColumnView(int index);

  /**
   * Gets a view of the diagonal. Modifications will change the original matrix.
   *
   * @return a diagonal view
   */
  Diagonal getDiagonalView();

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
  ComplexMatrix getView(int rowOffset, int colOffset, int rows, int columns);

  /**
   * Returns the conjugate transpose of this vector.
   *
   * @return the conjugate transpose
   */
  ComplexMatrix conjugateTranspose();

  /**
   * {@inheritDoc}
   */
  @Override
  ComplexMatrix reshape(int rows, int columns);

  /**
   * Transpose matrix like.
   *
   * @return the matrix like
   */
  ComplexMatrix transpose();

  /**
   * Create a copy of this matrix.
   *
   * @return the copy
   */
  @Override
  ComplexMatrix copy();

  /**
   * Construct a new empty matrix with {@code this.getClass()}
   *
   * @param rows the number of rows
   * @param columns the number of colums
   * @return a new empty matrix (
   */
  ComplexMatrix newEmptyMatrix(int rows, int columns);

  /**
   * Returns a new matrix with elements negated.
   *
   * @return a new matrix
   */
  ComplexMatrix negate();

  /**
   * Get value at row {@code i} and column {@code j}
   *
   * @param i row
   * @param j column
   * @return value Complex
   */
  Complex get(int i, int j);

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
   * @return the value index
   */
  Complex get(int index);

  /**
   * @return the matrix as a column-major Complex array
   * @see #isArrayBased()
   */
  double[] asDoubleArray();

  /**
   * @return true if {@link #asDoubleArray()} is {@code O(1)}
   */
  boolean isArrayBased();

  // Arithmetical operations ///////////

  /**
   * <u>m</u>atrix<u>m</u>ultiplication
   *
   * @param other the other
   * @return r r
   */
  ComplexMatrix mmul(ComplexMatrix other);

  /**
   * <u>M</u>atrix <u>M</u>atrix <u>M</u>ultiplication. Scaling {@code this} with {@code alpha} and
   * {@code other} with {@code beta}. Hence, it computes
   * {@code this.mul(alpha).mul(other.mul(beta))}, but in one pass.
   *
   * @param alpha scaling for {@code this}
   * @param other the other matrix
   * @param beta scaling for {@code other}
   * @return a new matrix
   */
  ComplexMatrix mmul(Complex alpha, ComplexMatrix other, Complex beta);

  ComplexMatrix mmul(Transpose a, ComplexMatrix other, Transpose b);

  ComplexMatrix mmul(Complex alpha, Transpose a, ComplexMatrix other, Complex beta, Transpose b);

  /**
   * Element wise <u>m</u>ultiplication
   *
   * @param other the matrix
   * @return a new matrix
   */
  ComplexMatrix mul(ComplexMatrix other);

  /**
   * Element wise multiplication. Scaling {@code this} with {@code alpha} and {@code other} with
   * {@code beta}. Hence, it computes {@code this.mul(alpha).mul(other.mul(beta))}, but in one pass.
   *
   * @param alpha scaling for {@code this}
   * @param other the other matrix
   * @param beta scaling for {@code other}
   * @return a new matrix
   */
  ComplexMatrix mul(Complex alpha, ComplexMatrix other, Complex beta);

  /**
   * Element wise <u>m</u>ultiplication
   *
   * @param scalar the scalar
   * @return a new matrix
   */
  ComplexMatrix mul(Complex scalar);

  /**
   * In place element wise <u>m</u>ultiplication.
   *
   * @param other the other
   * @return receiver modified
   */
  ComplexMatrix muli(ComplexMatrix other);

  /**
   * In place element wise <u>m</u>ultiplication.
   *
   * @param scalar the scalar
   * @return receiver multiplied
   */
  ComplexMatrix muli(Complex scalar);

  /**
   * In place Element wise subtraction.
   *
   * @param alpha scaling for {@code this}
   * @param other the other matrix
   * @param beta scaling for {@code other}
   * @see #mul(Complex, ComplexMatrix, Complex)
   * @return a new matrix
   */
  ComplexMatrix muli(Complex alpha, ComplexMatrix other, Complex beta);

  /**
   * Element wise addition.
   *
   * @param other the other matrix
   * @return a new matrix
   */
  ComplexMatrix add(ComplexMatrix other);

  /**
   * Element wise addition.
   *
   * @param scalar the scalar
   * @return a new matrix
   */
  ComplexMatrix add(Complex scalar);

  /**
   * Element wise addition. Scaling {@code this} with {@code alpha} and {@code other} with
   * {@code beta}. Hence, it computes {@code this.mul(alpha).add(other.mul(beta))}, but in one pass.
   *
   * @param alpha scaling for {@code this}
   * @param other the other matrix
   * @param beta scaling for {@code other}
   * @return a new matrix
   */
  ComplexMatrix add(Complex alpha, ComplexMatrix other, Complex beta);

  /**
   * In place element wise addition.
   *
   * @param other the other matrix
   * @return a new matrix
   */
  ComplexMatrix addi(ComplexMatrix other);

  /**
   * In place element wise addition.
   *
   * @param scalar the scalar
   * @return receiver modified
   */
  ComplexMatrix addi(Complex scalar);

  /**
   * In place element wise subtraction.
   *
   * @param alpha scaling for {@code this}
   * @param other the other matrix
   * @param beta scaling for {@code other}
   * @see #add(Complex, ComplexMatrix, Complex)
   * @return a new matrix
   */
  ComplexMatrix addi(Complex alpha, ComplexMatrix other, Complex beta);

  /**
   * Element wise subtraction. {@code this - other}.
   *
   * @param other the other matrix
   * @return a new matrix
   */
  ComplexMatrix sub(ComplexMatrix other);

  /**
   * Element wise subtraction. {@code this - other}.
   *
   * @param scalar the scalar
   * @return r r
   */
  ComplexMatrix sub(Complex scalar);

  /**
   * Element wise subtraction. Scaling {@code this} with {@code alpha} and {@code other} with
   * {@code beta}. Hence, it computes {@code this.mul(alpha).sub(other.mul(beta))}, but in one pass.
   *
   * @param alpha scaling for {@code this}
   * @param other the other matrix
   * @param beta scaling for {@code other}
   * @return a new matrix
   */
  ComplexMatrix sub(Complex alpha, ComplexMatrix other, Complex beta);

  /**
   * In place element wise subtraction.
   *
   * @param other the other matrix
   * @return receiver modified
   */
  ComplexMatrix subi(ComplexMatrix other);

  /**
   * In place element wise subtraction.
   *
   * @param scalar the scalar
   * @return receiver modified
   */
  ComplexMatrix subi(Complex scalar);

  /**
   * In place Element wise subtraction.
   *
   * @param alpha scaling for {@code this}
   * @param other the other matrix
   * @param beta scaling for {@code other}
   * @see #sub(Complex, ComplexMatrix, Complex)
   * @return a new matrix
   */
  ComplexMatrix subi(Complex alpha, ComplexMatrix other, Complex beta);

  /**
   * <u>R</u>eversed element wise subtraction. {@code scalar - this}.
   *
   * @param scalar the scalar
   * @return a new matrix
   */
  ComplexMatrix rsub(Complex scalar);

  /**
   * In place <u>r</u>eversed element wise subtraction. {@code scalar - this}.
   *
   * @param scalar the scalar
   * @return r r
   */
  ComplexMatrix rsubi(Complex scalar);

  /**
   * Element wise division. {@code this / other}.
   *
   * @param other the other
   * @return a new matrix
   * @throws java.lang.ArithmeticException if {@code other} contains {@code 0}
   */
  ComplexMatrix div(ComplexMatrix other);

  /**
   * Element wise division. {@code this / other}.
   *
   * @param other the scalar
   * @return a new matrix
   * @throws java.lang.ArithmeticException if {@code other} contains {@code 0}
   */
  ComplexMatrix div(Complex other);

  /**
   * In place element wise division.
   *
   * @param other the other matrix
   * @return receiver modified
   * @throws java.lang.ArithmeticException if {@code other} contains {@code 0}
   */
  ComplexMatrix divi(ComplexMatrix other);

  /**
   * In place element wise division.
   *
   * @param other the other
   * @return receiver modified
   * @throws java.lang.ArithmeticException if {@code other} contains {@code 0}
   */
  ComplexMatrix divi(Complex other);

  /**
   * Element wise division. {@code other / this}.
   *
   * @param other the scalar
   * @return a new matrix
   * @throws java.lang.ArithmeticException if {@code this} contains {@code 0}
   */
  ComplexMatrix rdiv(Complex other);

  /**
   * In place element wise division. {@code other / this}.
   *
   * @param other the scalar
   * @return a new matrix
   * @throws java.lang.ArithmeticException if {@code this} contains {@code 0}
   */
  ComplexMatrix rdivi(Complex other);
}

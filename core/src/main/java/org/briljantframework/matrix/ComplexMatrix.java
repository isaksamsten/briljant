package org.briljantframework.matrix;

import java.util.Collection;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.BinaryOperator;
import java.util.function.DoubleFunction;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.LongFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import org.briljantframework.complex.Complex;

/**
 * Implements a 2-dimensional matrix of complex numbers.
 *
 * @author Isak Karlsson
 */
public interface ComplexMatrix extends Matrix<ComplexMatrix>, Iterable<Complex> {

  /**
   * Assign {@code value} to {@code this}
   *
   * @param value the value to assign
   * @return receiver modified
   */
  ComplexMatrix assign(Complex value);

  default ComplexMatrix assign(double real) {
    return assign(Complex.valueOf(real));
  }

  /**
   * Assign value returned by {@link #size()} successive calls to
   * {@link java.util.function.DoubleSupplier#getAsDouble()}
   *
   * @param supplier the supplier
   * @return receiver modified
   */
  ComplexMatrix assign(Supplier<Complex> supplier);

  /**
   * Assign {@code matrix} to {@code this}. Requires {@code matrix.getShape()} to equal
   * {@code this.getShape()}.
   *
   * @param matrix the matrix
   * @return receiver modified
   */
  ComplexMatrix assign(ComplexMatrix matrix);

  /**
   * Assign {@code matrix} to {@code this}, applying {@code operator} to each value.
   *
   * @param matrix the matrix
   * @param operator the operator
   * @return receiver modified
   */
  ComplexMatrix assign(ComplexMatrix matrix, UnaryOperator<Complex> operator);

  ComplexMatrix assign(ComplexMatrix matrix, BinaryOperator<Complex> combine);

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
  ComplexMatrix assign(DoubleMatrix matrix, DoubleFunction<Complex> operator);

  ComplexMatrix assign(LongMatrix matrix, LongFunction<Complex> operator);

  ComplexMatrix assign(IntMatrix matrix, IntFunction<Complex> operator);

  /**
   * Perform {@code operator} element wise to receiver.
   *
   * @param operator the operator to apply to each element
   * @return receiver modified
   */
  ComplexMatrix update(UnaryOperator<Complex> operator);

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

  IntMatrix mapToInt(ToIntFunction<Complex> function);

  LongMatrix mapToLong(ToLongFunction<Complex> function);

  DoubleMatrix mapToDouble(ToDoubleFunction<Complex> function);

  ComplexMatrix filter(Predicate<Complex> predicate);

  BitMatrix satisfies(Predicate<Complex> predicate);

  BitMatrix satisfies(ComplexMatrix matrix, BiPredicate<Complex, Complex> predicate);

  Complex reduce(Complex identity, BinaryOperator<Complex> reduce);

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
   * Returns the conjugate transpose of this vector.
   *
   * @return the conjugate transpose
   */
  ComplexMatrix conjugateTranspose();

  /**
   * {@inheritDoc}
   * 
   * @param rows
   * @param columns
   */
  @Override
  ComplexMatrix reshape(int rows, int columns);

  void set(int index, Complex complex);

  void set(int i, int j, Complex complex);

  void setRow(int index, ComplexMatrix row);

  void setColumn(int index, ComplexMatrix column);

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
   * {@inheritDoc}
   * 
   * @param i
   */
  @Override
  ComplexMatrix getRowView(int i);

  /**
   * {@inheritDoc}
   * 
   * @param index
   */
  @Override
  ComplexMatrix getColumnView(int index);

  /**
   * {@inheritDoc}
   */
  @Override
  ComplexMatrix getDiagonalView();

  /**
   * {@inheritDoc}
   * 
   * @param rowOffset
   * @param colOffset
   * @param rows
   * @param columns
   */
  @Override
  ComplexMatrix getView(int rowOffset, int colOffset, int rows, int columns);

  @Override
  ComplexMatrix slice(Range rows, Range columns);

  @Override
  ComplexMatrix slice(Range range);

  @Override
  ComplexMatrix slice(Range range, Axis axis);

  @Override
  ComplexMatrix slice(Collection<Integer> rows, Collection<Integer> columns);

  @Override
  ComplexMatrix slice(Collection<Integer> indexes);

  @Override
  ComplexMatrix slice(Collection<Integer> indexes, Axis axis);

  @Override
  ComplexMatrix slice(BitMatrix bits);

  @Override
  ComplexMatrix slice(BitMatrix indexes, Axis axis);

  /**
   * Transpose matrix like.
   *
   * @return the matrix like
   */
  @Override
  ComplexMatrix transpose();

  /**
   * Create a copy of this matrix.
   *
   * @return the copy
   */
  @Override
  ComplexMatrix copy();

  Stream<Complex> stream();

  List<Complex> asList();

  /**
   * Construct a new empty matrix with {@code this.getClass()}
   *
   * @param rows the number of rows
   * @param columns the number of colums
   * @return a new empty matrix (
   */
  ComplexMatrix newEmptyMatrix(int rows, int columns);

  ComplexMatrix newEmptyVector(int size);

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
   * @param alpha scaling for {@code this*other}
   * @param other the other matrix
   * @return a new matrix
   */
  ComplexMatrix mmul(Complex alpha, ComplexMatrix other);

  ComplexMatrix mmul(Transpose a, ComplexMatrix other, Transpose b);

  ComplexMatrix mmul(Complex alpha, Transpose a, ComplexMatrix other, Transpose b);

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
   * @return a new matrixget(
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
   * <u>R</u>eversed element wise subtraction. {@code scalar - this}.
   *
   * @param scalar the scalar
   * @return a new matrix
   */
  ComplexMatrix rsub(Complex scalar);

  ComplexMatrix rsub(ComplexMatrix matrix, Axis axis);

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
   * Element wise division. {@code other / this}.
   *
   * @param other the scalar
   * @return a new matrix
   * @throws java.lang.ArithmeticException if {@code this} contains {@code 0}
   */
  ComplexMatrix rdiv(Complex other);


  /**
   * Returns a new matrix with elements negated.
   *
   * @return a new matrix
   */
  ComplexMatrix negate();
}
